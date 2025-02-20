package com.snaplink.urlshortener.service;

import com.snaplink.urlshortener.model.ShortUrl;
import com.snaplink.urlshortener.repository.BigtableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UrlShortenerService {

    private final BigtableRepository bigtableRepository;

    @Autowired
    public UrlShortenerService(BigtableRepository bigtableRepository) {
        this.bigtableRepository = bigtableRepository;
    }

    // Generate short code from long URL hash
    public String generateShortCode(String longUrl) {
        return Integer.toHexString(longUrl.hashCode()).substring(0, 6);
    }

    // Create Short URL
    public ShortUrl createShortUrl(String longUrl, String userId, boolean oneTime, String expirationDate) {
        // Generate a short code for the long URL
        String shortCode = generateShortCode(longUrl);
        String creationDate = Instant.now().toString();

        // Ensure expirationDate is not null
        String finalExpirationDate = (expirationDate != null && !expirationDate.isEmpty())
                ? expirationDate
                : "2030-01-01T00:00:00Z";  // Default expiration date if not provided

        // Create the ShortUrl object
        ShortUrl url = new ShortUrl(shortCode, longUrl, userId, creationDate, finalExpirationDate, oneTime, true);

        // Save to Bigtable
        bigtableRepository.createShortUrl(url);
        return url;
    }

    // Get Short URL by code
    public ShortUrl getShortUrl(String shortCode) {
        return bigtableRepository.getShortUrl(shortCode);
    }

    // Delete Short URL by code
    public void deleteShortUrl(String shortCode) {
        bigtableRepository.deleteShortUrl(shortCode);
    }
}
