package com.comp413.clientapi.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the entry point into the client API service. This should only be used locally for testing or by GCP products
 * when deployed and hosting the API.
 */
@SpringBootApplication(scanBasePackages = {"com.comp413.clientapi"})
public class ClientApiApplication {

    /**
     * Runs the API service and server service on the local machine.
     *
     * @param args command-line args (unused)
     */
    public static void main(String[] args) {
        SpringApplication.run(ClientApiApplication.class, args);
    }

}
