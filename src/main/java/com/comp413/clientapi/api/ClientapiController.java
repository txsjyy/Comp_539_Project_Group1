package com.comp413.clientapi.api;

import com.comp413.clientapi.obj.credentialsRequest;
import com.comp413.clientapi.obj.marketOrderRequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.*;


import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("api")
public class ClientapiController {

    String fin_sim_url = "https://comp-413-finsim-dot-rice-comp-539-spring-2022.uk.r.appspot.com/";

    @PostMapping("/login/login")
    public ResponseEntity<Object> login(@RequestBody credentialsRequest request) {
        // TODO: send request to

//            return new ResponseEntity<>("No such combination of username and password", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("Login Successful", HttpStatus.OK);
    }

    @PostMapping("/login/register")
    public ResponseEntity<Object> register(@RequestBody credentialsRequest request) {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://comp-413-db-dot-rice-comp-539-spring-2022.uk.r.appspot.com/api/database/users/storeUser"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request.toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());

            // For added detail, you can print out the headers:
//            HttpHeaders headers = response.headers();
//            headers.map().forEach((k, v) -> System.out.println(k + ":" + v));
            return new ResponseEntity<>(response.body(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
//            return new ResponseEntity<>("Cannot register a new user under provided username: such a user already exists", HttpStatus.CONFLICT);
//        return new ResponseEntity<>("User Registration Successful", HttpStatus.OK);
    }

    @PostMapping("/order/makeOrder")
    public ResponseEntity<Object> makeOrder(@RequestBody marketOrderRequest request) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://comp-413-finsim-dot-rice-comp-539-spring-2022.uk.r.appspot.com/api/v0/place-market-order/"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request.toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return new ResponseEntity<>(response.body(),HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("dashboard/getStock")
    public ResponseEntity<Object> getStock(@RequestBody String symbol) {
        String timeEarliest = "";
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("dashboard/getTransactionHistory")
    public ResponseEntity<Object> getTransactionHistory(@RequestBody credentialsRequest request) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://comp-413-db-dot-rice-comp-539-spring-2022.uk.r.appspot.com/api/database/getOrders"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(request.toString()))
                .build();

        try {
            HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());

            System.out.println("Response Code: " + response.statusCode());
            System.out.println("Response Body: " + response.body());
            return new ResponseEntity<>(response.body(),HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("dashboard/getOrderHistory")
    public ResponseEntity<Object> getOrderHistory(@RequestBody uhhhhhh) {

    }


}
