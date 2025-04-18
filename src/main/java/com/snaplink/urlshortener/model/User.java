// src/main/java/com/snaplink/urlshortener/model/User.java
package com.snaplink.urlshortener.model;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String username;
    private String email;
    private String password;
    private String subscriptionPlan;
    private LocalDateTime createdAt;

    // New fields for password‑reset flow
    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    public User() {
        // no‑args for deserialization / proxies
    }

    /**
     * Primary constructor for new users (no resetToken yet).
     */
    public User(String id,
                String username,
                String email,
                String password,
                String subscriptionPlan,
                LocalDateTime createdAt) {
        this.id               = id;
        this.username         = username;
        this.email            = email;
        this.password         = password;
        this.subscriptionPlan = subscriptionPlan;
        this.createdAt        = createdAt;
    }

    /**
     * Full constructor (e.g. when loading from Bigtable, includes reset fields).
     */
    public User(String id,
                String username,
                String email,
                String password,
                String subscriptionPlan,
                LocalDateTime createdAt,
                String resetToken,
                LocalDateTime resetTokenExpiry) {
        this(id, username, email, password, subscriptionPlan, createdAt);
        this.resetToken        = resetToken;
        this.resetTokenExpiry  = resetTokenExpiry;
    }

    // --- Getters & Setters ---

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }
    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getResetToken() {
        return resetToken;
    }
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }
}
