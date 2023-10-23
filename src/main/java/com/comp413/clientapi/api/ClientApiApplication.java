package com.comp413.clientapi.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * This is the entry point into the client API service. This should only be used locally for testing or by GCP products
 * when deployed and hosting the API.
 */
@SpringBootApplication
public class ClientApiApplication {

    /**
     *
     *
     * @param args command-line args (unused)
     */
    public static void main(String[] args) {
        SpringApplication.run(ClientApiApplication.class, args);
    }

}
