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
import java.util.List;

@Repository
public class BigtableRepository {

    private final BigtableDataClient client;

    public BigtableRepository(
            @Value("${gcp.project-id}") String projectId,
            @Value("${gcp.instance-id}") String instanceId
    ) throws IOException {
        this.client = BigtableDataClient.create(projectId, instanceId);
    }

    // ---- User Operations ----
    public void createUser(User user) {
        RowMutation mutation = RowMutation.create("user_profiles", "user#" + user.getId())
                .setCell("personal", "username", user.getUsername())
                .setCell("personal", "email", user.getEmail())
                .setCell("personal", "password", user.getPassword())
                .setCell("subscription", "plan", user.getSubscriptionPlan());
        client.mutateRow(mutation);
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        client.readRows(Query.create("user_profiles")).forEach(row -> {
            String id = row.getKey().toStringUtf8().split("#")[1];
            String username = row.getCells("personal", "username").get(0).getValue().toStringUtf8();
            String email = row.getCells("personal", "email").get(0).getValue().toStringUtf8();
            String password = row.getCells("personal", "password").get(0).getValue().toStringUtf8();
            String plan = row.getCells("subscription", "plan").get(0).getValue().toStringUtf8();
            users.add(new User(id, username, email, password, plan));
        });
        return users;
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

        return new ShortUrl(shortCode, longUrl, userId, creationDate, expirationDate, oneTime, isActive);
    }
}
