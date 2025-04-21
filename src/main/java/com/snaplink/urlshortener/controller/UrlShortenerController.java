package com.snaplink.urlshortener.controller;

import com.snaplink.urlshortener.model.ShortUrl;
import com.snaplink.urlshortener.model.ShortUrlDto;
import com.snaplink.urlshortener.service.UrlShortenerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/")
@CrossOrigin(origins = "*")
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @Autowired
    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    // Shorten URL - Accept JSON Body
    @PostMapping("/shorten")
    public ResponseEntity<ShortUrl> shortenUrl(@RequestBody ShortUrl request) {
        if (request.getLongUrl() == null || request.getUserId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        ShortUrl shortUrl;

        try {
            String customAlias = request.getCustomAlias();

            if (customAlias != null && !customAlias.isEmpty()) {
                // Custom alias logic
                return ResponseEntity.ok(
                        urlShortenerService.createCustomShortUrl(
                                customAlias,
                                request.getLongUrl(),
                                request.getUserId(),
                                request.isOneTime(),
                                request.getExpirationDate()
                        )
                );
            } else {
                // Auto-generated alias logic
                return ResponseEntity.ok(
                        urlShortenerService.createShortUrl(
                                request.getLongUrl(),
                                request.getUserId(),
                                request.isOneTime(),
                                request.getExpirationDate()
                        )
                );
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // Optionally return error message
        }
    }



    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirectToLongUrl(@PathVariable String shortCode, HttpServletRequest request) {
        ShortUrl url = urlShortenerService.getShortUrl(shortCode);

        if (url == null || !url.isActive()) {
            return ResponseEntity.status(404).body("This link is no longer active.");
        }

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime creation = LocalDateTime.parse(url.getCreationDate(), DateTimeFormatter.ISO_DATE_TIME);
            LocalDateTime expiration = LocalDateTime.parse(url.getExpirationDate(), DateTimeFormatter.ISO_DATE_TIME);

            // Special 24-hour rule for user123
            if ("user123".equalsIgnoreCase(url.getUserId())) {
                expiration = creation.plusHours(24);
            }

            if (now.isAfter(expiration)) {
                // You can optionally mark the link inactive in the DB here
                return ResponseEntity.ok("Your link has expired.");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Skip check on parse failure
        }

        // Log click before redirect
        urlShortenerService.recordClick(shortCode, request);

        String destination = url.getLongUrl();
        if (!destination.startsWith("http://") && !destination.startsWith("https://")) {
            destination = "https://" + destination;
        }

        return ResponseEntity.status(302)
                .header("Location", destination)
                .build();
    }

//    @GetMapping("/search")
//    public ResponseEntity<List<ShortUrl>> searchShortUrls(@RequestParam String query) {
//        List<ShortUrl> results = urlShortenerService.getAllUrlsByUser(query);
//        return ResponseEntity.ok(results);}
    @GetMapping("/search")
    public ResponseEntity<List<ShortUrlDto>> searchShortUrls(@RequestParam String query) {
        List<ShortUrlDto> result = urlShortenerService.getShortUrlsWithClickCounts(query);
        return ResponseEntity.ok(result);
    }






//    @GetMapping("/user/{userId}/urls")
//    public ResponseEntity<List<ShortUrl>> getUrlsByUser(@PathVariable String userId) {
//        List<ShortUrl> urls = urlShortenerService.getAllUrlsByUser(userId);
//        return ResponseEntity.ok(urls);
//    }


    // Delete Shortened URL
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<String> deleteShortUrl(@PathVariable String shortCode) {
        urlShortenerService.deleteShortUrl(shortCode);
        return ResponseEntity.ok("Short URL deleted successfully.");
    }

    @PostMapping("/analytics/details")
    public ResponseEntity<List<Map<String, String>>> getClickDetails(@RequestBody Map<String, String> body) {
        String shortCode = body.get("shortCode");

        if (shortCode == null || shortCode.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Map<String, String>> details = urlShortenerService.getClickDetails(shortCode);
        return ResponseEntity.ok(details);
    }
    @PutMapping("/update-shortcode")
    public ResponseEntity<String> updateShortCode(@RequestBody Map<String, String> body) {
        String oldCode = body.get("oldCode");
        String newCode = body.get("newCode");
    
        if (oldCode == null || newCode == null || oldCode.isEmpty() || newCode.isEmpty()) {
            return ResponseEntity.badRequest().body("Both oldCode and newCode must be provided.");
        }
    
        try {
            urlShortenerService.updateShortCode(oldCode, newCode);
            return ResponseEntity.ok("Short code updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    

}
