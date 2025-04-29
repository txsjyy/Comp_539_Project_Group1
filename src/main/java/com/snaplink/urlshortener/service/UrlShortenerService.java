package com.snaplink.urlshortener.service;

import com.snaplink.urlshortener.model.ShortUrl;
import com.snaplink.urlshortener.model.ShortUrlDto;
import com.snaplink.urlshortener.repository.BigtableRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing URL shortening operations.
 * Handles creation, retrieval, and analytics of shortened URLs.
 */
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
        String shortCode = generateShortCode(longUrl);
        return createShortUrlWithCode(shortCode, longUrl, userId, oneTime, expirationDate);
    }

    // Create Short URL (custom code)
    public ShortUrl createCustomShortUrl(String customShortCode, String longUrl, String userId, boolean oneTime, String expirationDate) {
        if (bigtableRepository.existsByShortCode(customShortCode)) {
            throw new IllegalArgumentException("Short code '" + customShortCode + "' is already in use.");
        }
        return createShortUrlWithCode(customShortCode, longUrl, userId, oneTime, expirationDate);
    }

    // Helper method to create and persist a ShortUrl object given a short code.
    private ShortUrl createShortUrlWithCode(String shortCode, String longUrl, String userId, boolean oneTime, String expirationDate) {
        String creationDate = Instant.now().toString();
        String finalExpirationDate = (expirationDate != null && !expirationDate.isEmpty())
                ? expirationDate
                : "2030-01-01T00:00:00Z";  // Default expiration date if not provided

        // Create the ShortUrl object
        ShortUrl url = new ShortUrl(shortCode, longUrl, userId, creationDate, finalExpirationDate, oneTime, true, shortCode);

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
        .collect(Collectors.toList());
    }

    // Retrieve all short URLs for a specific user by query (Sorted by creationDate descending)
    public List<ShortUrl> searchShortUrls(String query) {
        return bigtableRepository.searchShortUrls(query).stream()
        .sorted((a, b) -> b.getCreationDate().compareTo(a.getCreationDate()))
        .collect(Collectors.toList());
    }

    public List<ShortUrlDto> getShortUrlsWithClickCounts(String userId) {
        List<ShortUrl> shortUrls = getAllUrlsByUser(userId);
        return shortUrls.stream()
                .map(url -> {
                    int clicks = bigtableRepository.getClickCount(url.getShortCode());
                    return new ShortUrlDto(url, clicks);
                })
                .collect(Collectors.toList());
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
        String geoLocation = ""; // TODO: Implement GeoIP service

        bigtableRepository.recordClick(shortCode, ipAddress, referrer, geoLocation, userAgent);
    }

    /**
     * Extracts client IP address from request headers.
     * Checks various proxy headers before falling back to remote address.
     */
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

    public void updateShortCode(String oldCode, String newCode) {
        if (shortCodeExists(newCode)) {
            throw new IllegalArgumentException("The new short code is already in use.");
        }
    
        ShortUrl existing = getShortUrl(oldCode);
        if (existing == null) {
            throw new IllegalArgumentException("Old short code does not exist.");
        }
    
        // Update the shortCode and customAlias
        existing.setShortCode(newCode);
        existing.setCustomAlias(newCode);
    
        // Recreate using all original info
        bigtableRepository.createShortUrl(existing); // directly inserts with current values
        bigtableRepository.deleteShortUrl(oldCode);
    }
}
