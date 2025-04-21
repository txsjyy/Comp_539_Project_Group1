package com.snaplink.urlshortener.repository;

import com.snaplink.urlshortener.model.ShortUrl;
import com.snaplink.urlshortener.model.User;
import com.google.cloud.bigtable.data.v2.BigtableDataClient;
import com.google.cloud.bigtable.data.v2.models.Query;
import com.google.cloud.bigtable.data.v2.models.Row;
import com.google.cloud.bigtable.data.v2.models.RowMutation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;

@Repository
public class BigtableRepository {

    private final BigtableDataClient client;

    public BigtableRepository(
            @Value("${gcp.project-id}") String projectId,
            @Value("${gcp.instance-id}") String instanceId
    ) throws IOException {
        this.client = BigtableDataClient.create(projectId, instanceId);
    }

    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // ---- User Operations ----
    public void createUser(User user) {
        RowMutation mutation = RowMutation.create("user_profiles", "user#" + user.getId())
                .setCell("personal", "username", user.getUsername())
                .setCell("personal", "email", user.getEmail())
                .setCell("security", "password", user.getPassword()) // 移动到security列族
                .setCell("subscription", "plan", user.getSubscriptionPlan())
                .setCell("metadata", "created_at", user.getCreatedAt().toString()); // 添加创建时间
        client.mutateRow(mutation);
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        client.readRows(Query.create("user_profiles")).forEach(row -> {
            String[] keyParts = row.getKey().toStringUtf8().split("#");
            String id = keyParts.length > 1 ? keyParts[1] : "";
            
            // 解析时间戳
            String createdAtStr = getCellValue(row, "metadata", "created_at");
            LocalDateTime createdAt = null;
            if (createdAtStr != null && !createdAtStr.isEmpty()) {
                createdAt = LocalDateTime.parse(createdAtStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            // optional reset‐token columns
            String token     = getCellValue(row, "security",     "reset_token");
            String expiryStr = getCellValue(row, "security",     "reset_token_expiry");
            LocalDateTime createdAtDt = createdAt == null ? null 
                : createdAt;
            LocalDateTime expiryDt    = expiryStr == null  ? null
                : LocalDateTime.parse(expiryStr, ISO_FMT);

            // 使用完整构造函数
            User user = new User(
                id,
                getCellValue(row, "personal", "username"),
                getCellValue(row, "personal", "email"),
                getCellValue(row, "security", "password"),
                getCellValue(row, "subscription", "plan"),
                createdAt  // 作为第6个参数
            );
            user.setResetToken(token);
            user.setResetTokenExpiry(expiryDt);
            users.add(user);
        });
        return users;
    }

    public void updateUser(User user) {
        RowMutation mut = RowMutation.create("user_profiles", "user#" + user.getId());

        if (user.getResetToken() != null) {
            mut.setCell("security", "reset_token", user.getResetToken());
        } else {
            mut.deleteCells("security", "reset_token");
        }

        if (user.getResetTokenExpiry() != null) {
            mut.setCell("security", "reset_token_expiry", user.getResetTokenExpiry().format(ISO_FMT));
        } else {
            mut.deleteCells("security", "reset_token_expiry");
        }

        client.mutateRow(mut);
    }
    public void deleteUserByRowKey(String email) {
        String rowKey = "user#" + email;
        client.mutateRow(RowMutation.create("user_profiles", rowKey).deleteRow());
    }


    public Optional<User> findByEmail(String email) {
        List<User> users = getUsers();
        return users.stream()
            .filter(user -> email.equals(user.getEmail()))
            .findFirst();
    }
    
    public boolean existsByUsername(String username) {
        return getUsers().stream()
            .anyMatch(user -> username.equals(user.getUsername()));
    }
    
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
    // 新增辅助方法
    private String getCellValue(Row row, String family, String qualifier) {
        return row.getCells(family, qualifier).isEmpty() ? 
            null : 
            row.getCells(family, qualifier).get(0).getValue().toStringUtf8();
    }
    public Optional<User> findByResetToken(String token) {
        return getUsers().stream()
                         .filter(u -> token.equals(u.getResetToken()))
                         .findFirst();
    }
    // ---- URL Operations ----
    public void createShortUrl(ShortUrl url) {
        RowMutation mutation = RowMutation.create("url_tracking", "url#" + url.getShortCode())
                .setCell("url_info", "long_url", url.getLongUrl())
                .setCell("url_info", "creation_date", url.getCreationDate())
                .setCell("url_info", "expiration_date", url.getExpirationDate())
                .setCell("url_info", "one_time", String.valueOf(url.isOneTime()))
                .setCell("url_info", "is_active", String.valueOf(url.isActive()))
                .setCell("user_info", "user_id", url.getUserId());
        client.mutateRow(mutation);
    }

    public ShortUrl getShortUrl(String shortCode) {
        Row row = client.readRow("url_tracking", "url#" + shortCode);
        if (row == null) return null;

        return mapRowToShortUrl(row);
    }

    public void deleteShortUrl(String shortCode) {
        client.mutateRow(RowMutation.create("url_tracking", "url#" + shortCode).deleteRow());
    }

    public List<ShortUrl> getAllUrlsByUserId(String userId) {
        List<ShortUrl> shortUrls = new ArrayList<>();

        // 1) Read ALL rows from the "url_tracking" table (no filters)
        client.readRows(Query.create("url_tracking")).forEach(row -> {
            // 2) Convert each row into a ShortUrl object
            ShortUrl shortUrl = mapRowToShortUrl(row);

            // 3) Check if the ShortUrl's userId matches
            if (shortUrl.getUserId().equals(userId)) {
                shortUrls.add(shortUrl);
            }
        });

        return shortUrls;
    }

    public boolean existsByShortCode(String shortCode) {
        Row row = client.readRow("url_tracking", "url#" + shortCode);
        return (row != null);
    }

    private ShortUrl mapRowToShortUrl(Row row) {
        // Row key: "url#<shortCode>"
        String shortCode = row.getKey().toStringUtf8().split("#")[1];

        String longUrl = row.getCells("url_info", "long_url").get(0).getValue().toStringUtf8();
        String userId = row.getCells("user_info", "user_id").get(0).getValue().toStringUtf8();
        String creationDate = row.getCells("url_info", "creation_date").get(0).getValue().toStringUtf8();
        String expirationDate = row.getCells("url_info", "expiration_date").get(0).getValue().toStringUtf8();
        boolean oneTime = Boolean.parseBoolean(row.getCells("url_info", "one_time").get(0).getValue().toStringUtf8());
        boolean isActive = Boolean.parseBoolean(row.getCells("url_info", "is_active").get(0).getValue().toStringUtf8());

        return new ShortUrl(shortCode, longUrl, userId, creationDate, expirationDate, oneTime, isActive,shortCode);
    }

    // ---- URL Analytics ----
    public List<Map<String, String>> getClickDetails(String shortCode) {
        List<Map<String, String>> clickRecords = new ArrayList<>();

        client.readRows(Query.create("url_analytics").prefix("click#" + shortCode + "#"))
                .forEach(row -> {
                    Map<String, String> record = new HashMap<>();
                    record.put("timestamp", row.getKey().toStringUtf8().split("#")[2]);
                    record.put("ip_address", getCellValue(row, "click_info", "ip_address"));
                    record.put("referrer", getCellValue(row, "click_info", "referrer"));
                    record.put("userAgent", getCellValue(row, "click_info", "userAgent"));
                    record.put("geo_location", getCellValue(row, "click_info", "geo_location"));
                    clickRecords.add(record);
                });

        return clickRecords;
    }

    public void recordClick(String shortCode, String ipAddress, String referrer, String geoLocation, String userAgent) {
        String timestamp = LocalDateTime.now().toString(); // e.g., 2025-03-25T20:45:00
        String rowKey = "click#" + shortCode + "#" + timestamp;

        RowMutation mutation = RowMutation.create("url_analytics", rowKey)
                .setCell("click_info", "click_count", "1")
                .setCell("click_info", "ip_address", ipAddress)
                .setCell("click_info", "referrer", referrer != null ? referrer : "")
                .setCell("click_info", "geo_location", geoLocation)
                .setCell("click_info", "userAgent", userAgent != null ? userAgent : "");

        client.mutateRow(mutation);
    }

    public List<ShortUrl> searchShortUrls(String query) {
        List<ShortUrl> matches = new ArrayList<>();
        client.readRows(Query.create("url_tracking")).forEach(row -> {
            ShortUrl url = mapRowToShortUrl(row);
    
            if (url.getShortCode().contains(query) ||
                url.getCustomAlias().contains(query) ||
                url.getLongUrl().contains(query)) {
                matches.add(url);
            }
        });
        return matches;
    }
    public int getClickCount(String shortCode) {
        int[] count = {0}; // Use array to mutate inside lambda
        client.readRows(Query.create("url_analytics").prefix("click#" + shortCode + "#"))
                .forEach(row -> count[0]++);
        return count[0];
    }

}
