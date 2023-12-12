package com.comp413.clientapi.server;

import com.comp413.clientapi.api.ClientApiController;
import com.comp413.clientapi.dbapi.BigTableManager;
import com.comp413.clientapi.dbapi.holding.Holding;
import com.comp413.clientapi.dbapi.order.Order;
import com.comp413.clientapi.dbapi.user.User;
import com.comp413.clientapi.obj.credentialsRequest;
import com.comp413.clientapi.obj.orderRequest;
import com.comp413.clientapi.obj.timeRange;
import com.google.cloud.Tuple;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This is the server to hold state and handle requests.
 */
@Service
public class ServerService {

    /**
     * URL to where the server routes FinSim requests
     */
    final String fin_sim_url = "https://comp-413-finsim-dot-rice-comp-539-spring-2022.uk.r.appspot.com/api/";

    /**
     * Map of SessionId (cookie) to portfolioId,username tuple (unique users). Maintains data on logged-in users.
     */
    final Map<String, Tuple<String, String>> ONLINE_MAP = new HashMap<>();
    /**
     * Random state for generating cookies.
     */
    final Random RANDOM = new Random(0xC413);
    /**
     * HTTP client for sending messages.
     */
    final HttpClient CLIENT;

    /**
     * Database handler
     */
    final BigTableManager DB;

    /**
     * Timestamp formatter
     */
    DateTimeFormatter TSFORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    /**
     * No-arg server constructor.
     */
    public ServerService() throws IOException {
        CLIENT = HttpClient.newHttpClient();
        DB = new BigTableManager();
    }

    /**
     * Log a user in provided matching credentials. A cookie header is returned to the requester.
     *
     * @param request   Necessary info to create a new user.
     * @return          Upon successful login, a brief message with a 200 OK status code is returned. A cookie is
     *                  contained within the response.
     */
    public ResponseEntity<String> login(credentialsRequest request) {

        String username = request.username();
        String portfolioId = DB.validateUser(username, request.password());

        if (portfolioId == null)
            return new ResponseEntity<>("Failed to log user in - credentials did not match.", HttpStatus.BAD_REQUEST);

        // Construct cookie value
        String random_val = String.valueOf(Math.abs(RANDOM.nextLong()));
        String time_stamp = timestamp().replace(" ", "T");  // replace the timestamp space
        String cookie_val = portfolioId + "-" + random_val + "-" + time_stamp;

        ONLINE_MAP.put(cookie_val, Tuple.of(portfolioId, username));

        System.out.println(ClientApiController.login_cookie_name);
        System.out.println(cookie_val);

        // Cookie lasts for 1hr
        HttpCookie cookie = ResponseCookie.from(ClientApiController.login_cookie_name, cookie_val)
                .maxAge(3600)
                .build();

        System.out.println("Set-Cookie: " + cookie);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Successful user login.");
    }

    /**
     * Register a user. The user's portfolioId is its unique identifier (for the time being - this setup must change).
     * Upon collision of this unique identifier, the registration will not proceed and the user's data will not be
     * updated or overwritten.
     *
     * @param request   Necessary info to create a new user.
     * @return          Upon successful registration, a brief message with a 201 CREATED status code is returned.
     */
    public ResponseEntity<String> register(credentialsRequest request) {

        String username = request.username();
        String password = request.password();
        String email = request.email();

        if (username == null)
            return new ResponseEntity<>("No username provided.", HttpStatus.BAD_REQUEST);

        if (password == null)
            return new ResponseEntity<>("No password provided.", HttpStatus.BAD_REQUEST);

        if (email == null)
            return new ResponseEntity<>("No email provided.", HttpStatus.BAD_REQUEST);

        // need to check if user exists before writing new user

        User user = new User(
                username,
                request.email(),
                request.password(),
                timestamp(), "", 1.e5f);

        if (!DB.writeNewUser(user))
            return new ResponseEntity<>("Failed to register the user - username clash.", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>("User \"" + username + "\" successfully registered.", HttpStatus.CREATED);
    }

    /**
     * Cancel an order.
     *
     * @param sessionId     Session cookie of logged-in user.
     * @param orderId       Unique identifier associated with an order.
     * @return              Upon success, a brief message with a 200 OK code is returned.
     */
    public ResponseEntity<String> cancelOrder(String sessionId, String orderId) {

        String portfolioId = ONLINE_MAP.get(sessionId).x();
        // use sessionId to ping DB if order has matching PFID

        // craft FinSim request
        String url = fin_sim_url + "cancel-order/" + orderId;
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .method("patch", HttpRequest.BodyPublishers.noBody())
                .build();

        // Attempt to send request
        try {
            String succMsg = "Order successfully cancelled.";
            HttpStatusCode succCode = HttpStatus.OK;
            HttpResponse<String> response = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            String logstring = succMsg + "\n" +
                    "\n[" + response.statusCode() + "] " + response.body();
            System.out.println(logstring);
            return new ResponseEntity<>(logstring, succCode);
        } catch (Exception e) {
            e.printStackTrace();
            String failMsg = "Failed to cancel the requested order: " + orderId;
            HttpStatusCode failCode = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(failMsg, failCode);
        }
    }

    /**
     * Place an order of a valid orderType: MARKET, LIMIT, or STOP.
     *
     * @param request   All the necessary information needed to process a order.
     * @return          Upon success, a brief message with a 201 CREATED code is returned.
     */
    public ResponseEntity<String> placeOrder(String sessionId, orderRequest request) {

        // determine endpoint of finsim API based on orderRequest type
        String order_URL;
        orderRequest.OrderType order_type = request.type();
        if (order_type == orderRequest.OrderType.MARKET) {
            order_URL = "place-market-order/";
        } else if (order_type == orderRequest.OrderType.LIMIT) {
            order_URL = "place-limit-order/";
        } else if (order_type == orderRequest.OrderType.STOP) {
            order_URL = "place-stop-order/";
        } else {
            return new ResponseEntity<>("Order type is invalid. Must be one of \"MARKET\", \"LIMIT\", or \"STOP\"", HttpStatus.BAD_REQUEST);
        }

        return handlePostRequest(
                order_URL,
                request.toString(),
                "Order successfully placed.",
                HttpStatus.CREATED,
                "Failed to make an order.",
                HttpStatus.BAD_REQUEST
        );
    }

    public ResponseEntity<Integer> getOnlineCount() {
        return new ResponseEntity<>(ONLINE_MAP.size(), HttpStatus.OK);
    }

    /**
     * Get portfolio value from DB.
     *
     * @param sessionId     Session cookie of logged-in user.
     * @return              Valuation of portfolio.
     */
    public ResponseEntity<Float> getPFValue(String sessionId) {
        String portfolioId = ONLINE_MAP.get(sessionId).x();
        Float PFvalue = DB.getTotalValue(portfolioId);
        return new ResponseEntity<>(PFvalue, HttpStatus.OK);
    }

    /**
     * Returns total cash held in user's account.
     *
     * @param sessionId     Session cookie of logged-in user.
     * @return              Cash in user's account.
     */
    public ResponseEntity<Float> getCash(String sessionId) {
        String username = ONLINE_MAP.get(sessionId).y();
        Float cash = DB.getUserCash(username);
        return new ResponseEntity<>(cash, HttpStatus.OK);
    }

    /**
     * Request the database for an individual stock's data.
     *
     * @param symbol    Symbol of asset for which data are requested.
     * @return          If asset exists, the response contained data pertinent to the symbol.
     */
    public ResponseEntity<String> getStock(String symbol) {
        // pass params for requested data
        return handleGetRequest(
                "get-asset-price/" + symbol,
                "Stock data successfully retrieved.",
                HttpStatus.OK,
                "Failed to fetch stock data for requested asset: " + symbol,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Request historical data for an individual stock
     *
     * @param symbol    ticker for the stock for which data is requested
     * @return          If asset exists, the response contains historical data related to it
     */
    public ResponseEntity<String> fetchHistoricalData(String symbol, timeRange range) {
//        DB.getStockDataUsingTimeRanged()
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Get transaction data for a logged-in user from the database. Fetches filled orders.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return          List of (completed) transactions associated with the user.
     */
    public ResponseEntity<List<Order>> getTransactionHistory(String sessionId) {
        String portfolioId = ONLINE_MAP.get(sessionId).x();
        List<Order> transactions = DB.getOrders(portfolioId, "filled");
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    /**
     * Get order data for a logged-in user from the database.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return          List of (pending) orders associated with the user.
     */
    public ResponseEntity<List<Order>> getPendingOrders(String sessionId) {
        String portfolioId = ONLINE_MAP.get(sessionId).x();
        List<Order> pending = DB.getOrders(portfolioId, "pending");
        return new ResponseEntity<>(pending, HttpStatus.OK);
    }

    /**
     * Get cancelled order data for a logged-in user from the database.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return          List of cancelled orders associated with the user.
     */
    public ResponseEntity<List<Order>> getCancelledOrders(String sessionId) {
        String portfolioId = ONLINE_MAP.get(sessionId).x();
        List<Order> cancelled = DB.getOrders(portfolioId, "cancelled");
        return new ResponseEntity<>(cancelled, HttpStatus.OK);
    }

    /**
     * List all assets held by a user.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return  list of held assets.
     */
    public ResponseEntity<List<Holding>> getHoldings(String sessionId) {
        String portfolioId = ONLINE_MAP.get(sessionId).x();
        List<Holding> holdings = DB.getHoldings(portfolioId);
        return new ResponseEntity<>(holdings, HttpStatus.OK);
    }


    /**
     * Helper method to easily handle GET requests in a uniform fashion.
     *
     * @param suffix    location of API endpoint relative to fin_sim API URL to where the data are re-routed
     * @param succMsg   brief message explaining the successful request
     * @param succCode  HTTP code returned upon successful fulfillment of request
     * @param failMsg   brief message explaining the failed request
     * @param failCode  HTTP code returned upon unsuccessful fulfillment of request
     * @return          response containing results of request
     */
    private ResponseEntity<String> handleGetRequest(String suffix, String succMsg, HttpStatus succCode, String failMsg, HttpStatus failCode) {

        String url = fin_sim_url + suffix;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            String logstring = succMsg + "\n" +
                    "\n[" + response.statusCode() + "] " + body;
            System.out.println(logstring);
            return new ResponseEntity<>(body, succCode);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(failMsg, failCode);
        }
    }

    /**
     * Helper method to easily handle POST requests in a uniform fashion.
     *
     * @param suffix    location of API endpoint relative to fin_sim API URL to where the data are re-routed
     * @param reqBody   body of client request
     * @param succMsg   brief message explaining the successful request
     * @param succCode  HTTP code returned upon successful fulfillment of request
     * @param failMsg   brief message explaining the failed request
     * @param failCode  HTTP code returned upon unsuccessful fulfillment of request
     * @return          response containing brief details regarding request
     */
    private ResponseEntity<String> handlePostRequest(String suffix, String reqBody, String succMsg, HttpStatus succCode, String failMsg, HttpStatus failCode) {

        String url = fin_sim_url + suffix;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            String logstring = succMsg + "\n" + reqBody +
                    "\n[" + response.statusCode() + "] " + response.body();
            System.out.println(logstring);
            return new ResponseEntity<>(logstring, succCode);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(failMsg, failCode);
        }
    }

    /**
     * Make UTC timestamp string for the current moment.
     * @return  A UTC timestamp string.
     */
    private String timestamp() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(TSFORMATTER);
    }
}
