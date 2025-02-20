package com.snaplink.urlshortener.model;

public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private String subscriptionPlan;

    // Constructors
    public User(String id, String username, String email, String password, String subscriptionPlan) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.subscriptionPlan = subscriptionPlan;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getSubscriptionPlan() { return subscriptionPlan; }
}
