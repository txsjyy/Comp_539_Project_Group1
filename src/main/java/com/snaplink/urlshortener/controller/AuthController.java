package com.snaplink.urlshortener.controller;

import com.snaplink.urlshortener.model.*;
import com.snaplink.urlshortener.repository.BigtableRepository;
import com.snaplink.urlshortener.service.EmailService;
import com.snaplink.urlshortener.service.ResetPasswordRequest;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling user authentication and account management.
 * Provides endpoints for user registration, login, password reset, and account deletion.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final BigtableRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final EmailService emailService;

    @Value("${app.security.reset-token-expiration-hours}")
    private long expHours;

    @Autowired
    public AuthController(AuthenticationManager authManager,
                          BigtableRepository repository,
                          PasswordEncoder encoder,
                          EmailService emailService) {
        this.authManager  = authManager;
        this.repository   = repository;
        this.encoder      = encoder;
        this.emailService = emailService;
    }

    /**
     * Registers a new user account.
     * 
     * @param request User registration details
     * @return ResponseEntity with success message or error
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody AuthRequest.SignUp request) {
        // Check if username exists
        if (repository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email already exists"));
        }

        // Check if email exists
        if (repository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email already registered"));
        }

        // Check password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Passwords do not match"));
        }

        // Create user object
        User user = new User(
            request.getUsername(),
            request.getUsername(),
            request.getEmail(),
            encoder.encode(request.getPassword()),
            request.getSubscriptionPlan(),
            LocalDateTime.now()
        );

        // Save to Bigtable
        repository.createUser(user);

        // Send welcome email
        try {
            emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());
        } catch (Exception ex) {
            logger.warn("Failed to send welcome email to {}", user.getEmail(), ex);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(userResponse(user));
    }

    /**
     * Authenticates a user and generates a JWT token.
     * 
     * @param request User credentials (email and password)
     * @return ResponseEntity with JWT token or error message
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest.Login request) {
        try {
            // Query user manually
            User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));
                
            // Verify password manually
            boolean isPasswordMatch = encoder.matches(request.getPassword(), user.getPassword());

            if (!isPasswordMatch) {
                throw new BadCredentialsException("Invalid password");
            }
    
            return ResponseEntity.ok(authResponse(user));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Initiates the password reset process by sending a reset email.
     * 
     * @param body Contains the user's email address
     * @param request HttpServletRequest
     * @return ResponseEntity with success message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String,String> body,
            HttpServletRequest request
    ) {
        String email = body.get("email");
        Optional<User> opt = repository.findByEmail(email);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("error","User not found"));
        }

        User user = opt.get();
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
        repository.updateUser(user);

        String resetLink = "https://snaplink.com/reset-password?token=" + token;

        // Parse user agent header
        String ua      = request.getHeader("User-Agent");
        String os      = parseOperatingSystem(ua);
        String browser = parseBrowserName(ua);

        try {
            emailService.sendPasswordResetEmail(
                email,
                user.getUsername(),
                resetLink,
                os,
                browser
            );
        } catch (Exception ignored) {
            // Log silently
        }

        return ResponseEntity.ok(
            Map.of("message","If that email is registered, a reset link has been sent.")
        );
    }

    // Helper methods for parsing user agent
    private String parseOperatingSystem(String ua) {
        if (ua == null || ua.isEmpty()) {
            return "Unknown OS";
        }
        if (ua.contains("Windows")) {
            return "Windows";
        } else if (ua.contains("Mac")) {
            return "MacOS";
        } else if (ua.contains("Linux")) {
            return "Linux";
        } else {
            return "Other";
        }
    }

    private String parseBrowserName(String ua) {
        if (ua == null || ua.isEmpty()) {
            return "Unknown Browser";
        }
        if (ua.contains("Chrome")) {
            return "Chrome";
        } else if (ua.contains("Firefox")) {
            return "Firefox";
        } else if (ua.contains("Safari")) {
            return "Safari";
        } else if (ua.contains("Edge")) {
            return "Edge";
        } else {
            return "Other";
        }
    }

    /**
     * Resets a user's password using a valid reset token.
     * 
     * @param req Contains the reset token and new password
     * @return ResponseEntity with success message or error
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        Optional<User> opt = repository.findByResetToken(req.getToken());
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest()
                                 .body(Map.of("error", "Invalid reset token"));
        }

        User user = opt.get();
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest()
                                 .body(Map.of("error", "Reset token expired"));
        }

        user.setPassword(encoder.encode(req.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        repository.updateUser(user);

        return ResponseEntity.ok(Map.of("message", "Password has been reset"));
    }

    /**
     * Deletes a user account and associated data.
     * 
     * @param email ID of the user to delete
     * @return ResponseEntity with success message
     */
    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser(@RequestParam String email) {
        if (!repository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        try {
            repository.deleteUserByRowKey(email);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user"));
        }
    }

    // Helper methods for response formatting
    private Map<String, Object> userResponse(User user) {
        return Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail(),
            "subscriptionPlan", user.getSubscriptionPlan(),
            "createdAt", user.getCreatedAt()
        );
    }

    private Map<String, Object> authResponse(User user) {
        return userResponse(user);
    }

    private ResponseEntity<?> badRequestResponse(String message) {
        return ResponseEntity.badRequest().body(Map.of("error", message));
    }

    private ResponseEntity<?> conflictResponse(String message) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", message));
    }

    private ResponseEntity<?> unauthorizedResponse(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", message));
    }
}