package com.comp413.clientapi.server;

import com.comp413.clientapi.obj.credentialsRequest;
import com.comp413.clientapi.obj.marketOrderRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    final String fin_sim_url = "https://comp-413-finsim-dot-rice-comp-539-spring-2022.uk.r.appspot.com/";
    /**
     * URL to where the server routes Database requests
     */
    final String db_url = "https://comp-413-db-dot-rice-comp-539-spring-2022.uk.r.appspot.com/api/";
    /**
     * Map of SessionId (cookie) to portfolioId (unique users). Maintains data on logged-in users.
     */
    final Map<String, Long> sessionPortfolioMap = new HashMap<>();
    /**
     * Random state for generating cookies.
     */
    final Random RANDOM = new Random(0xC413);
    /**
     * HTTP client for sending messages.
     */
    final HttpClient CLIENT;

    /**
     * No-arg server constructor.
     */
    public ServerService() {
        CLIENT = HttpClient.newHttpClient();
    }

    /**
     * Log a user in provided matching credentials. A cookie header is returned to the requester.
     *
     * @param request   Necessary info to create a new user.
     * @return          Upon successful login, a brief message with a 200 OK status code is returned. A cookie is
     *                  contained within the response.
     */
    public ResponseEntity<String> login(credentialsRequest request) {

        if (!authenticate(request.username(), request.password()))
            return new ResponseEntity<>("Failed to log user in - credentials did not match.", HttpStatus.BAD_REQUEST);

        java.time.ZonedDateTime time = java.time.ZonedDateTime.now();
        String timestamp = time.toString();
        Long portfolioId = RANDOM.nextLong();
        // Append necessary items to response body
        String body = request.toString();
        body = body.substring(0, body.length()-1);
        // TODO: handle timestamp in server
        body += ", \"registrationTimestamp\": \"" + timestamp + "\"";
        // TODO: handle portfolio ID generation in DB
        body += ", \"portfolioId\": \"" + portfolioId + "\"";
        // Enclose JSON format in ending bracket
        body += "}";

        // TODO: figure out how to compare a user/pass pair to those in DB w/o requesting full data.
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(db_url + "database/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());

            String cookieValue = portfolioId + "-" + RANDOM.nextLong() + "-" + time.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
            sessionPortfolioMap.put(cookieValue, portfolioId);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", "STSESSIONID=" + cookieValue + "; Max-Age=3600");

            String logstring = "User logged in successfully.\n" + body +
                    "\n[" + response.statusCode() + "] " + response.body();
            System.out.println(logstring);
            return ResponseEntity.status(HttpStatus.OK)
                    .headers(headers)
                    .body("Login Successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to log user in - server issue.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Authenticate a user given their username-password pair.
     *
     * @param username      Unique username.
     * @param password      Matching password.
     * @return              If authentication is successful, true is returned; else, false.
     */
    private boolean authenticate(String username, String password) {
        return true;
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
        String timeNow = timestamp();
        String portfolioId = String.valueOf(RANDOM.nextLong());
        // Append necessary items to response body
        String body = request.toString();
        body = body.substring(0, body.length()-1);
        // TODO: handle timestamp in server
        body += ", \"registrationTimestamp\": \"" + timeNow + "\"";
        // TODO: handle portfolio ID generation in DB
        body += ", \"portfolioId\": \"" + portfolioId + "\"";
        // Enclose JSON format in ending bracket
        body += "}";

        return handlePostRequest(
                db_url + "database/users/storeUser",
                body,
                "User successfully registered.",
                HttpStatus.CREATED,
                "Failed to register the user.",
                HttpStatus.BAD_REQUEST);
//            return new ResponseEntity<>("Cannot register a new user under provided username: such a user already exists", HttpStatus.CONFLICT);
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

        Long portfolioId = sessionPortfolioMap.get(sessionId);
        return handleDeleteRequest(
                fin_sim_url + "/api/v0/cancel-order/" + orderId,
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
        Long portfolioId = sessionPortfolioMap.get(sessionId);
        return handlePostRequest(
                fin_sim_url + "api/v0/place-market-order/",
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
        // pass params for requested data
        return handleGetRequest(
                fin_sim_url + "api/v0/get-asset-price/" + symbol,
                "Stock data successfully retrieved.",
                HttpStatus.OK,
                "Failed to fetch stock data for requested asset: " + symbol,
                HttpStatus.BAD_REQUEST
        );
//        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    // get stock history
    // get stock market price

    // gets for any lists should default to 50 results
    // bigtable: limit=50, offset = 100

    // user id

    /**
     * Get transaction data for a logged-in user from the database. CURRENTLY just fetches (pending) orders.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return          List of (completed) transactions associated with the user.
     */
    public ResponseEntity<String> getTransactionHistory(String sessionId) {
        Long portfolioId = sessionPortfolioMap.get(sessionId);
        return handleGetRequest(
                db_url + "database/getOrders/" + portfolioId,
                "Transactions successfully retrieved.",
                HttpStatus.OK,
                "Failed to fetch transaction data for requesting user.",
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Get order data for a logged-in user from the database.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return          List of (pending) orders associated with the user.
     */
    public ResponseEntity<String> getOrderHistory(String sessionId) {
        Long portfolioId = sessionPortfolioMap.get(sessionId);
        return handleGetRequest(
                db_url + "database/getOrders/" + portfolioId,
                "Orders successfully retrieved.",
                HttpStatus.OK,
                "Failed to fetch order data for requesting user.",
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * List all assets held by a user.
     *
     * @param sessionId Session cookie of logged-in user.
     * @return  list of held assets.
     */
    public ResponseEntity<String> getHoldings(String sessionId) {
        Long portfolioId = sessionPortfolioMap.get(sessionId);
        return handleGetRequest(
                db_url + "database/holding/" + portfolioId,
                "Holdings successfully retrieved.",
                HttpStatus.OK,
                "Failed to fetch holdings for requesting user.",
                HttpStatus.BAD_REQUEST
        );
    }


    /**
     * Helper method to easily handle GET requests in a uniform fashion.
     *
     * @param url       location of API endpoint the data are re-routed to
     * @param succMsg   brief message explaining the successful request
     * @param succCode  HTTP code returned upon successful fulfillment of request
     * @param failMsg   brief message explaining the failed request
     * @param failCode  HTTP code returned upon unsuccessful fulfillment of request
     * @return          response containing brief details regarding request
     */
    private ResponseEntity<String> handleGetRequest(String url, String succMsg, HttpStatus succCode, String failMsg, HttpStatus failCode) {

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
     * @param url       location of API endpoint the data are re-routed to
     * @param reqBody   body of client request
     * @param succMsg   brief message explaining the successful request
     * @param succCode  HTTP code returned upon successful fulfillment of request
     * @param failMsg   brief message explaining the failed request
     * @param failCode  HTTP code returned upon unsuccessful fulfillment of request
     * @return          response containing brief details regarding request
     */
    private ResponseEntity<String> handlePostRequest(String url, String reqBody, String succMsg, HttpStatus succCode, String failMsg, HttpStatus failCode) {

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
     * @param url       location of API endpoint the data are re-routed to
     * @param succMsg   brief message explaining the successful request
     * @param succCode  HTTP code returned upon successful fulfillment of request
     * @param failMsg   brief message explaining the failed request
     * @param failCode  HTTP code returned upon unsuccessful fulfillment of request
     * @return          response containing brief details regarding request
     */
    private ResponseEntity<String> handleDeleteRequest(String url, String succMsg, HttpStatus succCode, String failMsg, HttpStatus failCode) {

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
