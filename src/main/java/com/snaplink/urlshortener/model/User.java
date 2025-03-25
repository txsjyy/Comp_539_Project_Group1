package com.snaplink.urlshortener.model;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private String subscriptionPlan;
    private LocalDateTime createdAt;

    public User() {}

    public User(String id, String username, String email, String password, 
               String subscriptionPlan, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.subscriptionPlan = subscriptionPlan;
        this.createdAt = createdAt;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSubscriptionPlan() { return subscriptionPlan; }
    public void setSubscriptionPlan(String plan) { this.subscriptionPlan = plan; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime time) { this.createdAt = time; }
}