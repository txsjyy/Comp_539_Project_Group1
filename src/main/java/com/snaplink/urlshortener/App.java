package com.snaplink.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Boot application.
 */
@SpringBootApplication(scanBasePackages = "com.snaplink.urlshortener")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("🚀 URL Shortener App is running at http://localhost:8080");
    }
}
