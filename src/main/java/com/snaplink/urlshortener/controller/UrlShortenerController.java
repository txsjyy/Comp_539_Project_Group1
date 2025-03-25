package com.snaplink.urlshortener.controller;

import com.snaplink.urlshortener.model.ShortUrl;
import com.snaplink.urlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable String shortCode) {
        ShortUrl url = urlShortenerService.getShortUrl(shortCode);

        if (url == null || !url.isActive()) {
            return ResponseEntity.notFound().build();
        }

        String destination = url.getLongUrl();
        if (!destination.startsWith("http://") && !destination.startsWith("https://")) {
            destination = "https://" + destination;
        }

        return ResponseEntity.status(302)
                .header("Location", destination)
                .build();
    }



    // Delete Shortened URL
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<String> deleteShortUrl(@PathVariable String shortCode) {
        urlShortenerService.deleteShortUrl(shortCode);
        return ResponseEntity.ok("Short URL deleted successfully.");
    }
}
