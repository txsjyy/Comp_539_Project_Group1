package com.comp413.clientapi.api;

import com.comp413.clientapi.obj.credentialsRequest;
import com.comp413.clientapi.obj.marketOrderRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.*;

/**
 * Requests are routed through here.
 */
@RestController
@RequestMapping("api")
public class ClientapiController {

    /**
     * URL to where the server routes FinSim requests
     */
    String fin_sim_url = "https://comp-413-finsim-dot-rice-comp-539-spring-2022.uk.r.appspot.com/";
    /**
     * URL to where the server routes Database requests
     */
    String db_url = "https://comp-413-db-dot-rice-comp-539-spring-2022.uk.r.appspot.com/api/";

    /**
     * When providing matching credentials (username, password), a user is granted access to all the services in the
     * Stock Trading platform. This WILL SOON begin a new session (create a local cookie) for the user and allow them to
     * perform all the requests specific to their user.
     *
     * @param request User credentials request body consists of a username and password pair
     * @return Upon successful login (currently this is EVERY login) the response yields a 200 OK status.
     */
    @PostMapping("/login/login")
    public ResponseEntity<Object> login(@RequestBody credentialsRequest request) {

//            return new ResponseEntity<>("No such combination of username and password", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("Login Successful", HttpStatus.OK);
    }

    /**
     * A new user may register with the Stock Trading service by providing a credential pair consisting of a unique
     * username and a password.
     *
     * @param request User credentials request body consists of a username and password pair
     * @return Upon successful user creation (currently this is EVERY registration) the response yields a 201 CREATED
     * status and a brief message. It WILL SOON return an error if the requested account credentials collide with those
     * of an already-existing user.
     */
    @PostMapping("/login/register")
    public ResponseEntity<Object> register(@RequestBody credentialsRequest request) {

        String timestamp = java.time.ZonedDateTime.now().toString();
        String portfolioId = "tempId";
        // Append necessary items to response body
        String body = request.toString();
        body = body.substring(0, body.length()-1);
        // TODO: handle timestamp in server
        body += "\"registrationTimestamp\": " + timestamp + "\",";
        // TODO: handle portfolio ID generation in DB
        body += "\"portfolioId\": \"" + portfolioId + "\"";
        // Enclose JSON format in ending bracket
        body += "}";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(db_url + "/database/users/storeUser"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
            System.out.println(
                    "Registered user\n" + request +
                    "\n[" + response.statusCode() + "] " + response.body());
            return new ResponseEntity<>(response.body(), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Registering the user failed.", HttpStatus.BAD_REQUEST);
        }
//            return new ResponseEntity<>("Cannot register a new user under provided username: such a user already exists", HttpStatus.CONFLICT);
    }

    /**
     * A user can make a market order by providing the necessary details. FOR NOW the order details are passed directly
     * to the FinSim which will process the order.
     *
     * @param request Order request contains all the details necessary for an order. A user must specify the following
     *                for a valid order request:
     *                 - (int) userId
     *                 - (int) portfolioId
     *                 - (String) ticker
     *                 - (int) quantity
     *                 - (enum OrderType) side
     * @return If the order is successful, the response yields a brief message and a 201 CREATED status.
     */
    @PostMapping("/order/marketOrder")
    public ResponseEntity<Object> marketOrder(@RequestBody marketOrderRequest request) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(fin_sim_url + "/api/v0/place-market-order/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request.toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

            System.out.println(
                    "Placed market order\n" + request +
                    "\n[" + response.statusCode() + "] " + response.body());
            return new ResponseEntity<>(response.body(),HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Making an order failed.", HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * Fetch data regarding an entity with the provided symbol.
     *
     * @param symbol The symbol of the relevant asset (e.g., stock ticker GOOG)
     * @return an object holding the data of the stock. It is serialized into JSON upon receipt.
     */
    @GetMapping("dashboard/getStock")
    public ResponseEntity<Object> getStock(@RequestBody String symbol) {
        String timeEarliest = "";
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Query for all of a user's completed transactions (filled orders).
     *
     * @param sessionId cookie
     * @return A list of transaction objects are returned. They are serialized into JSON upon receipt.
     */
    @GetMapping("dashboard/getTransactionHistory/{sessionId}")
    public ResponseEntity<Object> getTransactionHistory(@PathVariable String sessionId) {
//        HttpClient client = HttpClient.newHttpClient();
//
//        HttpRequest req = HttpRequest.newBuilder()
//                .uri(URI.create(db_url + "/database/getOrders"))
//                .header("Content-Type", "application/json")
//                .GET(HttpRequest.BodyPublishers.ofString(request.toString()))
//                .build();
//
//        try {
//            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
//
//            System.out.println("Response Code: " + response.statusCode());
//            System.out.println("Response Body: " + response.body());
//            return new ResponseEntity<>(response.body(),HttpStatus.OK);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>("Fetching the transaction history failed.", HttpStatus.BAD_REQUEST);
//        }
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    /**
     * Query for all of a user's open orders (pending orders)
     *
     * @return a list of order objects are returned. They are serialized into JSON upon receipt.
     */
    @GetMapping("dashboard/getOrderHistory")
    public ResponseEntity<Object> getOrderHistory() {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }


}
