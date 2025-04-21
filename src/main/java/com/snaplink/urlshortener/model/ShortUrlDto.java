package com.snaplink.urlshortener.model;

import java.io.Serializable;

public class ShortUrlDto implements Serializable {
    private String shortCode;
    private String longUrl;
    private int clickCount;

    public ShortUrlDto(ShortUrl url, int clickCount) {
        this.shortCode = url.getShortCode();
        this.longUrl = url.getLongUrl();
        this.clickCount = clickCount;
    }

    public String getShortCode() {
        return shortCode;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public int getClickCount() {
        return clickCount;
    }
}
