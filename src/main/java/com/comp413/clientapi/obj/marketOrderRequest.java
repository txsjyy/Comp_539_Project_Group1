package com.comp413.clientapi.obj;

/**
 * Request object for any request to place a market order. All fields final.
 *
 * @param userId        unique identifier of requesting user
 * @param portfolioId   unique identifier of requesting user's portfolio
 * @param ticker        symbol associated with requested asset
 * @param quantity      amount of asset requested
 * @param side          either "BUY" or "SELL" for buy orders or sell orders, respectively.
 */
public record marketOrderRequest (
        long userId,
        long portfolioId,
        String ticker,
        int quantity,
        OrderType side
) {
    /**
     * Specifies the type of order placed: BUY or SELL. When passed in a JSON request, the equivalnet string must be
     * placed within double quotes.
     */
    public enum OrderType {
        /**
         * For Buy orders
         */
        BUY,
        /**
         * For Sell orders.
         */
        SELL
    }

    @Override
    public String toString() {
        return "{" +
                "\"userId\": " + userId +
                ", \"portfolioId\": " + portfolioId +
                ", \"ticker\": \"" + ticker + "\"" +
                ", \"quantity\": " + quantity +
                ", \"side\": \"" + side + "\"" +
                "}";
    }
}
