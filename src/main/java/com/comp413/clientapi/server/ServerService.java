package com.comp413.clientapi.server;

import com.comp413.clientapi.dbapi.BigTableManager;
import com.comp413.clientapi.dbapi.holding.Holding;
import com.comp413.clientapi.dbapi.user.User;
import com.comp413.clientapi.obj.credentialsRequest;
import com.comp413.clientapi.obj.marketOrderRequest;
import com.comp413.clientapi.obj.timeRange;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.System.exit;

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
     * Map of SessionId (cookie) to portfolioId (unique users). Maintains data on logged-in users.
     */
    final Map<String, String> ONLINE_MAP = new HashMap<>();
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

        if (!DB.validateUser(username, request.password()))
            return new ResponseEntity<>("Failed to log user in - credentials did not match.", HttpStatus.BAD_REQUEST);

        // Construct cookie value
        Long pfID = 1L;  // temp until DB gives us pfID for user after validating.
        Long random_val = RANDOM.nextLong();
        String time_stamp = timestamp();
        String cookie_val = String.valueOf(pfID) + "-" + String.valueOf(random_val) + "-" + time_stamp;

        // TODO: determine if it is more useful to track pfID or username with cookie
        ONLINE_MAP.put(cookie_val, String.valueOf(pfID));

        // Cookie lasts for 1hr
        HttpCookie cookie = ResponseCookie.from("STSESSIONID", cookie_val)
                .maxAge(3600)
                .build();

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

        // need to check if user exists before writing new user

        User user = new User(
                username,
                request.email(),
                request.password(),
                null, null, null, null);
        DB.writeNewUser(user);

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
        // use sessionId to ping DB if that user has order? or just pass to finsim

        String portfolioId = ONLINE_MAP.get(sessionId);
        return handleDeleteRequest(
                "cancel-order/" + orderId,
                "Order successfully cancelled.",
                HttpStatus.OK,
                "Failed to cancel the requested order " + orderId,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Place a market order.
     *
     * @param request   All the necessary information needed to process a market order.
     * @return          Upon success, a brief message with a 201 CREATED code is returned.
     */
    public ResponseEntity<String> placeMarketOrder(String sessionId, marketOrderRequest request) {
        return handlePostRequest(
                "place-market-order/",
                request.toString(),
                "Market order successfully placed.",
                HttpStatus.CREATED,
                "Failed to make an order.",
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Get portfolio value from DB.
     *
     * @param sessionId     Session cookie of logged-in user.
     * @return              Valuation of portfolio.
     */
    public ResponseEntity<String> getPFValue(String sessionId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Returns total cash held in user's account.
     *
     * @param sessionId     Session cookie of logged-in user.
     * @return              Cash in user's account.
     */
    public ResponseEntity<String> getCash(String sessionId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Request the database for an individual stock's data.
     *
     * @param symbol    Symbol of asset for which data are requested.
     * @return          If asset exists, the response contained data pertinent to the symbol.
     */
    public ResponseEntity<String> getStock(String symbol) {
//        return handleGetRequest(
//                db_url + "database/getStock/" + symbol,
//                "Stock data successfully retrieved.",
//                HttpStatus.OK,
//                "Failed to fetch stock data for requested asset: " + symbol,
//                HttpStatus.BAD_REQUEST
//        );
        // pass params for requested data
        return handleGetRequest(
                "get-asset-price/" + symbol,
                "Stock data successfully retrieved.",
                HttpStatus.OK,
                "Failed to fetch stock data for requested asset: " + symbol,
                HttpStatus.BAD_REQUEST
        );
//        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Request historical data for an individual stock
     *
     * @param symbol    ticker for the stock for which data is requested
     * @return          If asset exists, the response contains historical data related to it
     */
    public ResponseEntity<String> fetchHistoricalData(String symbol, timeRange range) {
        return handleGetRequest(
                //TODO: need correct URL for getting historical data
                "historicalData/" + symbol,
                "Stock data successfully retrieved.",
                HttpStatus.OK,
                "Failed to fetch stock data for requested asset: " + symbol,
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Validates that an order is possible to fulfill
     * @param marketOrderRequest    the order that is intended
     * @return                      true if the order can be fulfilled, false if not
     */
    public boolean validateOrder(marketOrderRequest marketOrderRequest) {
        // Check if ticker is valid
        if (!isValidTicker(marketOrderRequest.ticker())) {
            System.out.println("Invalid ticker");
            return false;
        }

        // Check if user has enough funds (you'll likely need another service for this)
        if (!hasSufficientFunds(marketOrderRequest.userId(), marketOrderRequest.quantity())) {
            System.out.println("Insufficient funds");
            return false;
        }

        // Other validation checks go here...

        return true;
    }

    /**
     * Checks if the ticker is valid
     * @param ticker    the ticker to check
     * @return          true if the ticker is valid, false if not
     */
    private boolean isValidTicker(String ticker) {
        // will check against ticker list in DB to ensure it is a valid ticket
        //TODO: add database check
        return ticker != null && !ticker.trim().isEmpty();
    }

    /**
     * Checks if the user has sufficient funds for an order
     * @param userId        the ID of the user
     * @param quantity      the quantity of stock desired
     * @return              true if the user has sufficient funds, false if not
     */
    private boolean hasSufficientFunds(int userId, int quantity) {
        //TODO: implement this
        return quantity <= 100;
    }

    /**
     * Get transaction data for a logged-in user from the database. CURRENTLY just fetches (pending) orders.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return          List of (completed) transactions associated with the user.
     */
    public ResponseEntity<String> getTransactionHistory(String sessionId) {
        String portfolioId = ONLINE_MAP.get(sessionId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * Get order data for a logged-in user from the database.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return          List of (pending) orders associated with the user.
     */
    public ResponseEntity<String> getPendingOrders(String sessionId) {
        String portfolioId = ONLINE_MAP.get(sessionId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * Get cancelled order data for a logged-in user from the database.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return          List of cancelled orders associated with the user.
     */
    public ResponseEntity<String> getCancelledOrders(String sessionId) {
        String portfolioId = ONLINE_MAP.get(sessionId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * List all assets held by a user.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return  list of held assets.
     */
    public ResponseEntity<List<Holding>> getHoldings(String sessionId) {
        String portfolioId = ONLINE_MAP.get(sessionId);

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
     * @return          response containing brief details regarding request
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
            String logstring = succMsg + "\n" +
                    "\n[" + response.statusCode() + "] " + response.body();
            System.out.println(logstring);
            return new ResponseEntity<>(logstring, succCode);
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
     * Helper method to easily handle POST requests in a uniform fashion.
     *
     * @param suffix    location of API endpoint relative to fin_sim API URL to where the data are re-routed
     * @param succMsg   brief message explaining the successful request
     * @param succCode  HTTP code returned upon successful fulfillment of request
     * @param failMsg   brief message explaining the failed request
     * @param failCode  HTTP code returned upon unsuccessful fulfillment of request
     * @return          response containing brief details regarding request
     */
    private ResponseEntity<String> handleDeleteRequest(String suffix, String succMsg, HttpStatus succCode, String failMsg, HttpStatus failCode) {

        String url = fin_sim_url + suffix;

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            String logstring = succMsg + "\n" +
                    "\n[" + response.statusCode() + "] " + response.body();
            System.out.println(logstring);
            return new ResponseEntity<>(logstring, succCode);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(failMsg, failCode);
        }
    }

    /**
     * Make zoned timestamp.
     * @return  A zoned timestamp.
     */
    String timestamp() {
        return java.time.ZonedDateTime.now().toString();
    }
}
