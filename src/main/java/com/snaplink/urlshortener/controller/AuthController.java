package com.snaplink.urlshortener.controller;

import com.snaplink.urlshortener.model.*;
import com.snaplink.urlshortener.repository.BigtableRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final BigtableRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;

    public AuthController(BigtableRepository repository,
                         PasswordEncoder encoder,
                         AuthenticationManager authManager) {
        this.repository = repository;
        this.encoder = encoder;
        this.authManager = authManager;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody AuthRequest.SignUp request) {
        // 检查用户名/邮箱是否已存在
        if (repository.existsByUsername(request.getUsername())) {
            return conflictResponse("Username already exists");
        }
        if (repository.existsByEmail(request.getEmail())) {
            return conflictResponse("Email already registered");
        }

        // 密码一致性验证
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return badRequestResponse("Passwords do not match");
        }

        // 创建用户对象
        User user = new User(
            UUID.randomUUID().toString(),
            request.getUsername(),
            request.getEmail(),
            encoder.encode(request.getPassword()),
            request.getSubscriptionPlan(),
            LocalDateTime.now()
        );

        // 存储到Bigtable
        repository.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest.Login request) {
        try {
            Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            
            User user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
                
            return ResponseEntity.ok(authResponse(user));
        } catch (Exception e) {
            return unauthorizedResponse("Invalid credentials");
        }
    }

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
        Map<String, Object> response = new HashMap<>(userResponse(user));
        response.put("accessToken", "generated-jwt-token");
        return response;
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