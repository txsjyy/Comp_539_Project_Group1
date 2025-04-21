package com.snaplink.urlshortener.service;

import com.snaplink.urlshortener.model.ShortUrl;
import com.snaplink.urlshortener.repository.BigtableRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    // Retrieve all short URLs for a specific user by his ID (Sorted by creationDate descending)
    public List<ShortUrl> getAllUrlsByUser(String userId) {
        return bigtableRepository.getAllUrlsByUserId(userId).stream()
        .sorted((a, b) -> b.getCreationDate().compareTo(a.getCreationDate()))
        .toList();
    }

    // Retrieve all short URLs for a specific user by query (Sorted by creationDate descending)
    public List<ShortUrl> searchShortUrls(String query) {
        return bigtableRepository.searchShortUrls(query).stream()
        .sorted((a, b) -> b.getCreationDate().compareTo(a.getCreationDate()))
        .toList();
    }
    

    // Check if a short code already exists
    public boolean shortCodeExists(String shortCode) {
        return bigtableRepository.existsByShortCode(shortCode);
    }
    public List<Map<String, String>> getClickDetails(String shortCode) {
        return bigtableRepository.getClickDetails(shortCode);
    }

    public void recordClick(String shortCode, HttpServletRequest request) {
        String ipAddress = extractClientIp(request);
        String userAgent = Optional.ofNullable(request.getHeader("User-Agent")).orElse("Unknown");
        String referrer = Optional.ofNullable(request.getHeader("Referer")).orElse("Direct");
        LocalDateTime timestamp = LocalDateTime.now();
        String geoLocation = ""; // TODO: Use GeoIP service like MaxMind or ip-api.com


        bigtableRepository.recordClick(shortCode, ipAddress, referrer, geoLocation, userAgent);
    }
    public String extractClientIp(HttpServletRequest request) {
        String[] headerNames = {
                "CF-Connecting-IP", // Cloudflare
                "X-Forwarded-For",  // Proxies/load balancers
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP"
        };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // If multiple IPs are present, take the first one
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr(); // Fallback
    }



}
