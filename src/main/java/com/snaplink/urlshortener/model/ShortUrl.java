package com.snaplink.urlshortener.model;

/**
 * Represents a shortened URL in the system.
 * This class contains information about the original URL, its shortened version,
 * and various metadata such as creation date, expiration, and usage restrictions.
 */
public class ShortUrl {
    private String shortCode;       // Unique identifier for the shortened URL
    private String longUrl;         // Original URL
    private String userId;          // Associated user ID
    private String creationDate;    // Date of URL creation
    private String expirationDate;  // Optional expiration date
    private boolean oneTime;        // If true, link expires after one use
    private boolean isActive;       // Link status (active/inactive)
    private String customAlias;     // Optional custom alias, passed in from frontend

    /**
     * Default constructor for deserialization and proxy creation.
     */
    public ShortUrl() {}

    /**
     * Full constructor for creating a new ShortUrl with all properties.
     *
     * @param shortCode The unique short code for the URL
     * @param longUrl The original long URL
     * @param userId The ID of the user who created the short URL
     * @param creationDate The date when the short URL was created
     * @param expirationDate The optional expiration date of the short URL
     * @param oneTime Whether the URL can only be used once
     * @param isActive Whether the URL is currently active
     * @param customAlias Optional custom alias for the short URL
     */
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
    /**
     * Returns the short code of the URL.
     *
     * @return The short code
     */
    public String getShortCode() { return shortCode; }

    /**
     * Sets the short code of the URL.
     *
     * @param shortCode The new short code
     */
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    /**
     * Returns the original long URL.
     *
     * @return The long URL
     */
    public String getLongUrl() { return longUrl; }

    /**
     * Sets the original long URL.
     *
     * @param longUrl The new long URL
     */
    public void setLongUrl(String longUrl) { this.longUrl = longUrl; }

    /**
     * Returns the ID of the user who created the short URL.
     *
     * @return The user ID
     */
    public String getUserId() { return userId; }

    /**
     * Sets the ID of the user who created the short URL.
     *
     * @param userId The new user ID
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Returns the creation date of the short URL.
     *
     * @return The creation date
     */
    public String getCreationDate() { return creationDate; }

    /**
     * Sets the creation date of the short URL.
     *
     * @param creationDate The new creation date
     */
    public void setCreationDate(String creationDate) { this.creationDate = creationDate; }

    /**
     * Returns the expiration date of the short URL.
     *
     * @return The expiration date
     */
    public String getExpirationDate() { return expirationDate; }

    /**
     * Sets the expiration date of the short URL.
     *
     * @param expirationDate The new expiration date
     */
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    /**
     * Returns whether the URL can only be used once.
     *
     * @return True if the URL is one-time use, false otherwise
     */
    public boolean isOneTime() { return oneTime; }

    /**
     * Sets whether the URL can only be used once.
     *
     * @param oneTime The new one-time use status
     */
    public void setOneTime(boolean oneTime) { this.oneTime = oneTime; }

    /**
     * Returns whether the URL is currently active.
     *
     * @return True if the URL is active, false otherwise
     */
    public boolean isActive() { return isActive; }

    /**
     * Sets whether the URL is currently active.
     *
     * @param active The new active status
     */
    public void setActive(boolean active) { isActive = active; }

    /**
     * Returns the custom alias for the short URL.
     *
     * @return The custom alias
     */
    public String getCustomAlias() { return customAlias; }

    /**
     * Sets the custom alias for the short URL.
     *
     * @param customAlias The new custom alias
     */
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }

    /**
     * Returns a string representation of the ShortUrl object.
     * Used for debugging purposes.
     *
     * @return A string containing the object's properties
     */
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
