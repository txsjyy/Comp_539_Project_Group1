package com.comp413.clientapi.obj;

public record marketOrderRequest (
        int userId,
        int portfolioId,
        String ticker,
        int quantity,
        OrderType side
) {
    public enum OrderType {
        BUY,
        SELL;
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
