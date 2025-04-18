// src/main/java/com/snaplink/urlshortener/model/ResetPasswordRequest.java
package com.snaplink.urlshortener.service;

public class ResetPasswordRequest {
    private String token;
    private String newPassword;
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
