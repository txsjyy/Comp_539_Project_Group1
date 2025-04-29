package com.snaplink.urlshortener.model;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) for ShortUrl that includes click count information.
 * This class is used to transfer short URL data between layers of the application.
 */
public class ShortUrlDto implements Serializable {
    private String shortCode;
    private String longUrl;
    private int clickCount;

    /**
     * Constructs a new ShortUrlDto from a ShortUrl entity and its click count.
     *
     * @param url The ShortUrl entity to convert to DTO
     * @param clickCount The number of times the short URL has been clicked
     */
    public ShortUrlDto(ShortUrl url, int clickCount) {
        this.shortCode = url.getShortCode();
        this.longUrl = url.getLongUrl();
        this.clickCount = clickCount;
    }

    /**
     * Returns the short code of the URL.
     *
     * @return The short code
     */
    public String getShortCode() {
        return shortCode;
    }

    /**
     * Returns the original long URL.
     *
     * @return The long URL
     */
    public String getLongUrl() {
        return longUrl;
    }

    /**
     * Returns the number of times the short URL has been clicked.
     *
     * @return The click count
     */
    public int getClickCount() {
        return clickCount;
    }
}
