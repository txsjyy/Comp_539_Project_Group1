package com.comp413.clientapi.server;

import com.comp413.clientapi.obj.credentialsRequest;
import com.comp413.clientapi.obj.marketOrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.Random;

/**
 * This is a mock server to hold minimal state for now. NOT USED CURRENTLY.
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
    final Map<String, String> ONLINE_MAP = new HashMap<>();
    final Random RANDOM = new Random(0xC413);
    final HttpClient CLIENT = HttpClient.newHttpClient();

    /**
     * No-arg server constructor.
     */
    @Autowired
    public ServerService() {}

    public ResponseEntity<String> login(credentialsRequest request) {
        String timestamp = java.time.ZonedDateTime.now().toString();
        String portfolioId = "tempId";
        // Append necessary items to response body
        String body = request.toString();
        body = body.substring(0, body.length()-1);
        // TODO: handle timestamp in server
        body += ", \"registrationTimestamp\": \"" + timestamp + "\"";
        // TODO: handle portfolio ID generation in DB
        body += ", \"portfolioId\": \"" + portfolioId + "\"";
        // Enclose JSON format in ending bracket
        body += "}";

//        return handleRequest(
//
//        );
        return new ResponseEntity<>("Login Successful!", HttpStatus.OK);

    }

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

        return handleRequest(
                db_url + "database/users/storeUser",
                body,
                HttpStatus.CREATED,
                "Failed to register the user.",
                HttpStatus.BAD_REQUEST);
//            return new ResponseEntity<>("Cannot register a new user under provided username: such a user already exists", HttpStatus.CONFLICT);
    }

    public ResponseEntity<String> cancelOrder(String sessionId, String orderId) {
        // use sessionId to ping DB if that user has order? or just pass to finsim

        String portfolioId = ONLINE_MAP.get(sessionId);
        return handleRequest(
                fin_sim_url + "/api/v0/cancel-order/" + orderId,
                null,
//                HttpStatus.valueOf(299),    // custom code for good delete?
                HttpStatus.OK,
                "Failed to cancel the requested order " + orderId,
                HttpStatus.BAD_REQUEST
        );
    }

    public ResponseEntity<String> placeMarketOrder(marketOrderRequest request) {
        return handleRequest(
                fin_sim_url + "api/v0/place-market-order/",
                request.toString(),
                HttpStatus.CREATED,
                "Failed to make an order.",
                HttpStatus.BAD_REQUEST
        );
    }

    public ResponseEntity<String> getPFValue(String sessionId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<String> getCash(String sessionId) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<String> getStock(String symbol) {
        String body = "";

        return handleRequest(
                db_url + "database/",
                body,
                HttpStatus.OK,
                "Failed to fetch stock data for requested asset: " + symbol,
                HttpStatus.BAD_REQUEST
        );
    }

    public ResponseEntity<String> getTransactionHistory(String sessionId) {
        String body = "";

        return handleRequest(
                db_url + "database/getOrders",
                body,
                HttpStatus.OK,
                "Failed to fetch transaction data for requesting user.",
                HttpStatus.BAD_REQUEST
        );
    }

    public ResponseEntity<String> getOrderHistory(String sessionId) {
        String body = "";

        return handleRequest(
                db_url + "database/getOrders",
                body,
                HttpStatus.OK,
                "Failed to fetch order data for requesting user.",
                HttpStatus.BAD_REQUEST
        );
    }

    public ResponseEntity<String> getHoldings(String sessionId) {
        String portfolioId = ONLINE_MAP.get(sessionId);

        return handleRequest(
                db_url + "database/holding/" + portfolioId,
                null,
                HttpStatus.OK,
                "Failed to fetch holdings for requesting user.",
                HttpStatus.BAD_REQUEST
        );
    }


    /**
     * Helper method to easily handle requests in a uniform fashion.
     *
     * @param url       location of API endpoint the data are re-routed to
     * @param reqBody   body of client request
     * @param succCode  HTTP code returned upon successful fulfillment of request
     * @param failMsg   brief message explaining the failed request
     * @param failCode  HTTP code returned upon unsuccessful fulfillment of request
     * @return          response containing brief details regarding request
     */
    private ResponseEntity<String> handleRequest(String url, String reqBody, HttpStatus succCode, String failMsg, HttpStatus failCode) {

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        try {
            HttpResponse<String> response = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
            String logstring = "Registered user\n" + reqBody +
                    "\n[" + response.statusCode() + "] " + response.body();
            System.out.println(logstring);
            return new ResponseEntity<>(logstring, succCode);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(failMsg, failCode);
        }
    }

    String timestamp() {
        return java.time.ZonedDateTime.now().toString();
    }

    /**
     * Get the portfolioId associated with a given cookie.
     * @param sessionId cookie
     * @return portfolioId
     */
    public String getCookieValue(String sessionId) {
        return ONLINE_MAP.get(sessionId);
    }

    /**
     * Make a cookie to associate a portfolioId with it if it is not already present.
     *
     * @param portfolioId unique identifier of a user's portfolio
     * @return the new cookie assigned to hold this portfolioId.
     */
    public String makeCookie(String portfolioId) {
        if (ONLINE_MAP.containsValue(portfolioId))
                return null;
        else {
            String cookie = generateCookie();
            ONLINE_MAP.put(cookie, portfolioId);
            return cookie;
        }
    }

    /**
     * Remove a cookie. This should occur when the server is notified that the cookie expired.
     * @param sessionId cookie
     */
    public void removeCookie(String sessionId) {
        ONLINE_MAP.remove(sessionId);
    }

    /**
     * Generate a cookie
     *
     * @return A String cookie
     */
    private String generateCookie() {
        return "cookie";
    }

}
