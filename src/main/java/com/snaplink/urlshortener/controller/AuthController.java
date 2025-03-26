package com.snaplink.urlshortener.controller;

import com.snaplink.urlshortener.Security.UserDetailsImpl;
import com.snaplink.urlshortener.model.*;
import com.snaplink.urlshortener.repository.BigtableRepository;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
// Removed misplaced @Autowired annotation
public class AuthController {

    private final BigtableRepository repository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    @Autowired
    public AuthController(
        AuthenticationManager authManager,
        BigtableRepository repository,
        PasswordEncoder encoder
    ) {
        this.authManager = authManager;
        this.repository = repository;
        this.encoder = encoder;
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
                // 手动查询用户
                User user = repository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("用户不存在"));
                    
                // 手动验证密码
                boolean isPasswordMatch = encoder.matches(request.getPassword(), user.getPassword());
                System.out.println("[DEBUG] 密码比对结果: " + isPasswordMatch);

                if (!isPasswordMatch) {
                    throw new BadCredentialsException("密码错误");
                }
        
                // 生成认证令牌（可选）
                Authentication auth = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    Collections.emptyList()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
        
                return ResponseEntity.ok(authResponse(user));
            } catch (BadCredentialsException e) {
                return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
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