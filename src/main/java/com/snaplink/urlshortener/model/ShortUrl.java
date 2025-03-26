package com.snaplink.urlshortener.model;

public class ShortUrl {
    private String shortCode;       // Unique identifier for the shortened URL
    private String longUrl;         // Original URL
    private String userId;          // Associated user ID
    private String creationDate;    // Date of URL creation
    private String expirationDate;  // Optional expiration date
    private boolean oneTime;        // If true, link expires after one use
    private boolean isActive;       // Link status (active/inactive)
    private String customAlias; // Optional custom alias, passed in from frontend

    public ShortUrl() {}

    // Full constructor
    public ShortUrl(String shortCode, String longUrl, String userId, String creationDate, String expirationDate, boolean oneTime, boolean isActive, String customAlias) {
        this.shortCode = shortCode;
        this.longUrl = longUrl;
        this.userId = userId;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.oneTime = oneTime;
        this.isActive = isActive;
        this.customAlias = customAlias;
    }

    // Getters and Setters
    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getLongUrl() { return longUrl; }
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCreationDate() { return creationDate; }
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public boolean isOneTime() { return oneTime; }
    public void setOneTime(boolean oneTime) { this.oneTime = oneTime; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getCustomAlias() { return customAlias; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }


    // Override toString for debugging
    @Override
    public String toString() {
        return "ShortUrl{" +
                "shortCode='" + shortCode + '\'' +
                ", longUrl='" + longUrl + '\'' +
                ", userId='" + userId + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                ", oneTime=" + oneTime +
                ", isActive=" + isActive +
                '}';
    }
}
