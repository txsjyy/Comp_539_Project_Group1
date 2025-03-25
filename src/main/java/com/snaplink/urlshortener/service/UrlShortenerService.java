package com.snaplink.urlshortener.service;

import com.snaplink.urlshortener.model.ShortUrl;
import com.snaplink.urlshortener.repository.BigtableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

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
        return createShortUrlWithCode(shortCode, longUrl, userId, oneTime, expirationDate);
    }

    // Create Short URL (custom code)
    public ShortUrl createCustomShortUrl(String customShortCode, String longUrl, String userId, boolean oneTime, String expirationDate) {
        // Check if the custom short code already exists
        if (bigtableRepository.existsByShortCode(customShortCode)) {
            throw new IllegalArgumentException("Short code '" + customShortCode + "' is already in use.");
        }
        // If not, create the Short URL with that code
        return createShortUrlWithCode(customShortCode, longUrl, userId, oneTime, expirationDate);
    }

    // Helper method to create and persist a ShortUrl object given a short code.
    private ShortUrl createShortUrlWithCode(String shortCode, String longUrl, String userId, boolean oneTime, String expirationDate) {
        String creationDate = Instant.now().toString();

        // Ensure expirationDate is not null
        String finalExpirationDate = (expirationDate != null && !expirationDate.isEmpty())
                ? expirationDate
                : "2030-01-01T00:00:00Z";  // Default expiration date if not provided

        // Create the ShortUrl object
        ShortUrl url = new ShortUrl(shortCode, longUrl, userId, creationDate, finalExpirationDate, oneTime, true,shortCode);

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

    // Retrieve all short URLs for a specific user by his ID
    public List<ShortUrl> getAllUrlsByUser(String userId) {
        return bigtableRepository.getAllUrlsByUserId(userId);
    }

    // Check if a short code already exists
    public boolean shortCodeExists(String shortCode) {
        return bigtableRepository.existsByShortCode(shortCode);
    }
}
