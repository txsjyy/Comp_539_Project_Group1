package com.comp413.clientapi.server;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a mock server to hold minimal state for now. NOT USED CURRENTLY.
 */
public class Server {

    /**
     * Map of SessionId (cookie) to portfolioId (unique users). Maintains data on logged-in users.
     */
    public final Map<String, String> ONLINE_MAP = new HashMap<>();

    /**
     * No-arg server constructor.
     */
    public Server() {}

    /**
     * Get the portfolioId associated with a given cookie.
     * @param sessionId cookie
     * @return portfolioId
     */
    public String getCookieValue(String sessionId) {
        return ONLINE_MAP.get(sessionId);
    }

    /**
     * Make a cookie to associate a portfolioId with it if it is not already present.
     *
     * @param portfolioId unique identifier of a user's portfolio
     * @return the new cookie assigned to hold this portfolioId.
     */
    public String makeCookie(String portfolioId) {
        if (ONLINE_MAP.containsValue(portfolioId))
                return null;
        else {
            String cookie = generateCookie();
            ONLINE_MAP.put(cookie, portfolioId);
            return cookie;
        }
    }

    /**
     * Remove a cookie. This should occur when the server is notified that the cookie expired.
     * @param sessionId cookie
     */
    public void removeCookie(String sessionId) {
        ONLINE_MAP.remove(sessionId);
    }

    /**
     * Generate a cookie
     *
     * @return A String cookie
     */
    private String generateCookie() {
        return "cookie";
    }

}
