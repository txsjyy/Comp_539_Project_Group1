package com.comp413.clientapi.api;

import com.comp413.clientapi.obj.credentialsRequest;
import com.comp413.clientapi.obj.marketOrderRequest;

import com.comp413.clientapi.obj.timeRange;
import com.comp413.clientapi.server.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.Timer;

/**
 * Requests are made here and routed through the server.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("api")
public class ClientApiController {

    /**
     * Service that performs the business logic of request handling - "The Server".
     */
    private final ServerService serverService;

    /**
     * Constructor for API Controller that passes requests to a server instance.
     * @param serverService     Instance of server handling business logic of requests.
     */
    @Autowired
    public ClientApiController(ServerService serverService) {
        this.serverService = serverService;
    }

    /**
     * When providing matching credentials (username, password), a user is granted access to all the services in the
     * Stock Trading platform. This WILL SOON begin a new session (create a local cookie) for the user and allow them to
     * perform all the requests specific to their user.
     *
     * @param request User credentials request body consists of a username and password pair
     * @return Upon successful login (currently this is EVERY login) the response yields a 200 OK status.
     */
    @PostMapping("/login/login")
    public ResponseEntity<String> login(@RequestBody credentialsRequest request) {
        return serverService.login(request);
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
    public ResponseEntity<String> register(@RequestBody credentialsRequest request) {
        return serverService.register(request);
    }

    /**
     * A user can make a market order by providing the necessary details. FOR NOW the order details are passed directly
     * to the FinSim which will process the order.
     *
     * @param request Order request contains all the details necessary for an order. A user must specify the following
     *                for a valid order request:
     *                 - (long) userId
     *                 - (long) portfolioId
     *                 - (String) ticker
     *                 - (int) quantity
     *                 - (enum OrderType) side
     * @return If the order is successful, the response yields a brief message and a 201 CREATED status.
     */
    @PostMapping("/order/placeMarketOrder/{sessionId}")
    public ResponseEntity<String> placeMarketOrder(@PathVariable String sessionId, @RequestBody marketOrderRequest request) {
        return serverService.placeMarketOrder(sessionId, request);
    }

    /**
     * Cancel a given user's order provided their session cookie and order identifier.
     *
     * @param sessionId     Session cookie of logged-in user.
     * @param orderId       Unique identifier of order
     * @return              Upon success the response yields a brief message and a 200 OK status code. Upon failure a
     *                      brief message describing the failure is returned.
     */
    @DeleteMapping("/order/cancelOrder/{sessionId}&{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable String sessionId, @PathVariable String orderId) {
        return serverService.cancelOrder(sessionId, orderId);
    }

    /**
     * Provide a valuation of a user's portfolio on their dashboard.
     *
     * @param sessionId     Session cookie of logged-in user.
     * @return              Double value representing a portfolio's value.
     */
    @GetMapping("/dashboard/getPFValue/{sessionId}")
    public ResponseEntity<String> getPFValue(@PathVariable String sessionId) {
        return serverService.getPFValue(sessionId);
    }

    /**
     * Provide the total amount of cash on-hand in a user's account.
     *
     * @param sessionId     Session cookie of logged-in user.
     * @return              Double value representing a user's cash.
     */
    @GetMapping("/dashboard/getCash/{sessionId}")
    public ResponseEntity<String> getCash(@PathVariable String sessionId) {
        return serverService.getCash(sessionId);
    }

    /**
     * Fetch data regarding an entity with the provided symbol.
     *
     * @param symbol The symbol of the relevant asset (e.g., stock ticker GOOG)
     * @return an object holding the data of the stock. It is serialized into JSON upon receipt.
     */
    @GetMapping("/dashboard/getStock/{symbol}")
    public ResponseEntity<String> getStock(@PathVariable String symbol) {
        return serverService.getStock(symbol);
    }

    /**
     * Fetch data regarding an entity with the provided symbol.
     *
     * @param symbol The symbol of the relevant asset (e.g., stock ticker GOOG)
     * @return an object holding the data of the stock. It is serialized into JSON upon receipt.
     */
    @GetMapping("/dashboard/getStockHistory/{symbol}&{range}}")
    public ResponseEntity<String> getStockHistory(@PathVariable String symbol, @PathVariable timeRange range) {
        return serverService.fetchHistoricalData(symbol, range);
    }

    /**
     * Query for all of a user's completed transactions (filled orders).
     *
     * @param sessionId cookie
     * @return A list of transaction objects are returned. They are serialized into JSON upon receipt.
     */
    @GetMapping("/dashboard/getTransactionHistory/{sessionId}")
    public ResponseEntity<String> getTransactionHistory(@PathVariable String sessionId) {
        return serverService.getTransactionHistory(sessionId);
    }

    /**
     * Query for all of a user's open orders (pending orders).
     *
     * @param sessionId     Session cookie of logged-in user.
     * @return a list of order objects are returned. They are serialized into JSON upon receipt.
     */
    @GetMapping("/dashboard/getPendingOrders/{sessionId}")
    public ResponseEntity<String> getPendingOrder(@PathVariable String sessionId) {
        return serverService.getPendingOrders(sessionId);
    }

    /**
     * Query for all of a user's cancelled orders.
     *
     * @param sessionId     Session cookie of logged-in user.
     * @return a list of order objects are returned. They are serialized into JSON upon receipt.
     */
    @GetMapping("/dashboard/getCancelledOrders/{sessionId}")
    public ResponseEntity<String> getCancelledOrder(@PathVariable String sessionId) {
        return serverService.getCancelledOrders(sessionId);
    }

    /**
     * Query for all of a user's holdings.
     *
     * @param sessionId     Session cookie of logged-in user.
     * @return              A user's full list of holdings.
     */
    @GetMapping("/dashboard/getHoldings/{sessionId}")
    public ResponseEntity<String> getHoldings(@PathVariable String sessionId) {
        return serverService.getHoldings(sessionId);
    }
}
