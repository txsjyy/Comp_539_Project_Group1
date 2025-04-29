// src/main/java/com/snaplink/urlshortener/model/User.java
package com.snaplink.urlshortener.model;

import java.time.LocalDateTime;

/**
 * Represents a user in the URL shortener system.
 * This class contains user information including authentication details,
 * subscription plan, and password reset functionality.
 */
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

    /**
     * Default constructor for deserialization and proxy creation.
     */
    public User() {
        // no‑args for deserialization / proxies
    }

    /**
     * Primary constructor for new users (no resetToken yet).
     *
     * @param id The unique identifier for the user
     * @param username The user's chosen username
     * @param email The user's email address
     * @param password The user's hashed password
     * @param subscriptionPlan The user's subscription plan
     * @param createdAt The date and time when the user was created
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
     *
     * @param id The unique identifier for the user
     * @param username The user's chosen username
     * @param email The user's email address
     * @param password The user's hashed password
     * @param subscriptionPlan The user's subscription plan
     * @param createdAt The date and time when the user was created
     * @param resetToken The token used for password reset
     * @param resetTokenExpiry The expiration time of the reset token
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

    /**
     * Returns the user's unique identifier.
     *
     * @return The user ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user's unique identifier.
     *
     * @param id The new user ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the user's username.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user's username.
     *
     * @param username The new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the user's email address.
     *
     * @return The email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The new email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's hashed password.
     *
     * @return The hashed password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The new hashed password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the user's subscription plan.
     *
     * @return The subscription plan
     */
    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    /**
     * Sets the user's subscription plan.
     *
     * @param subscriptionPlan The new subscription plan
     */
    public void setSubscriptionPlan(String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    /**
     * Returns the date and time when the user was created.
     *
     * @return The creation timestamp
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation date and time for the user.
     *
     * @param createdAt The new creation timestamp
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the password reset token.
     *
     * @return The reset token
     */
    public String getResetToken() {
        return resetToken;
    }

    /**
     * Sets the password reset token.
     *
     * @param resetToken The new reset token
     */
    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    /**
     * Returns the expiration time of the reset token.
     *
     * @return The reset token expiration time
     */
    public LocalDateTime getResetTokenExpiry() {
        return resetTokenExpiry;
    }

    /**
     * Sets the expiration time for the reset token.
     *
     * @param resetTokenExpiry The new reset token expiration time
     */
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) {
        this.resetTokenExpiry = resetTokenExpiry;
    }
}
