package com.snaplink.urlshortener.model;

import jakarta.validation.constraints.*;

/**
 * Contains request models for authentication operations.
 * This class includes nested classes for sign-up and login requests.
 */
public class AuthRequest {

    /**
     * Request model for user registration.
     * Contains validation constraints for user input fields.
     */
    public static class SignUp {
        @NotBlank @Size(min = 3, max = 20)
        private String username;

        @NotBlank @Email
        private String email;

        @NotBlank @Size(min = 8)
        private String password;

        @NotBlank
        private String confirmPassword;

        private String subscriptionPlan = "free";

        // Getters & Setters
        /**
         * Returns the username for registration.
         *
         * @return The username
         */
        public String getUsername() { return username; }

        /**
         * Sets the username for registration.
         *
         * @param username The new username
         */
        public void setUsername(String username) { this.username = username; }

        /**
         * Returns the email address for registration.
         *
         * @return The email address
         */
        public String getEmail() { return email; }

        /**
         * Sets the email address for registration.
         *
         * @param email The new email address
         */
        public void setEmail(String email) { this.email = email; }

        /**
         * Returns the password for registration.
         *
         * @return The password
         */
        public String getPassword() { return password; }

        /**
         * Sets the password for registration.
         *
         * @param password The new password
         */
        public void setPassword(String password) { this.password = password; }

        /**
         * Returns the password confirmation.
         *
         * @return The confirmed password
         */
        public String getConfirmPassword() { return confirmPassword; }

        /**
         * Sets the password confirmation.
         *
         * @param confirmPassword The password confirmation
         */
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

        /**
         * Returns the subscription plan.
         *
         * @return The subscription plan
         */
        public String getSubscriptionPlan() { return subscriptionPlan; }

        /**
         * Sets the subscription plan.
         *
         * @param plan The new subscription plan
         */
        public void setSubscriptionPlan(String plan) { this.subscriptionPlan = plan; }
    }

    /**
     * Request model for user login.
     * Contains validation constraints for login credentials.
     */
    public static class Login {
        @NotBlank @Email
        private String email;

        @NotBlank
        private String password;

        // Getters & Setters
        /**
         * Returns the email address for login.
         *
         * @return The email address
         */
        public String getEmail() { return email; }

        /**
         * Sets the email address for login.
         *
         * @param email The new email address
         */
        public void setEmail(String email) { this.email = email; }

        /**
         * Returns the password for login.
         *
         * @return The password
         */
        public String getPassword() { return password; }

        /**
         * Sets the password for login.
         *
         * @param password The new password
         */
        public void setPassword(String password) { this.password = password; }
    }
}