package com.comp413.clientapi.obj;

/**
 * Request object for any request to place a market order. All fields final.
 *
 * @param userId        unique identifier of requesting user
 * @param ticker        symbol associated with requested asset
 * @param quantity      amount of asset requested
 * @param side          either "BUY" or "SELL" for buy orders or sell orders, respectively.
 */
public record marketOrderRequest (
        int userId,
        String ticker,
        int quantity,
        Side side,
        OrderType type
) {
    /**
     * Specifies the type of order placed: BUY or SELL. When passed in a JSON request, the equivalent string must be
     * placed within double quotes.
     */
    public enum Side {
        /**
         * For Buy orders
         */
        BUY,
        /**
         * For Sell orders.
         */
        SELL
    }

    public enum OrderType {
        STOP,
        LIMIT,
        MARKET
    }

    @Override
    public String toString() {
        return "{" +
                "\"userId\": " + userId +
                ", \"ticker\": \"" + ticker + "\"" +
                ", \"quantity\": " + quantity +
                ", \"side\": \"" + side + "\"" +
                ", \"orderType\": \"" + type + "\"" +
                "}";
    }
}
