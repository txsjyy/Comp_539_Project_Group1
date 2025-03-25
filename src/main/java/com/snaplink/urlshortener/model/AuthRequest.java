package com.snaplink.urlshortener.model;

import jakarta.validation.constraints.*;

public class AuthRequest {

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
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
        public String getSubscriptionPlan() { return subscriptionPlan; }
        public void setSubscriptionPlan(String plan) { this.subscriptionPlan = plan; }
    }

    public static class Login {
        @NotBlank @Email
        private String email;

        @NotBlank
        private String password;

        // Getters & Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}