// src/test/java/com/snaplink/urlshortener/repository/MockBigtableRepository.java
package com.snaplink.urlshortener.repository;

import com.snaplink.urlshortener.model.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MockBigtableRepository {
    // 使用内存存储模拟 Bigtable
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, String> emailToIdIndex = new ConcurrentHashMap<>();

    // 用户操作
    public User createUser(User user) {
        String userId = UUID.randomUUID().toString();
        user.setId(userId);
        
        users.put(userId, user);
        emailToIdIndex.put(user.getEmail().toLowerCase(), userId);
        return user;
    }

    public Optional<User> findByEmail(String email) {
        String userId = emailToIdIndex.get(email.toLowerCase());
        return Optional.ofNullable(users.get(userId));
    }

    public boolean existsByEmail(String email) {
        return emailToIdIndex.containsKey(email.toLowerCase());
    }

    public boolean existsByUsername(String username) {
        return users.values().stream()
            .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    // 清空测试数据
    public void clear() {
        users.clear();
        emailToIdIndex.clear();
    }
}