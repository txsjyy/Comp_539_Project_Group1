package com.comp413.clientapi.obj;

/**
 * Request object for any request to place a market order. All fields final.
 *
 * @param portfolioId   unique identifier of user executing order
 * @param ticker        symbol associated with requested asset
 * @param quantity      amount of asset requested
 * @param side          either "BUY" or "SELL" for buy orders or sell orders, respectively.
 * @param price         relevant price for a Limit Order or Stop Order
 */
public record finsimOrderRequest(
        String portfolioId,
        String ticker,
        int quantity,
        orderRequest.Side side,
        float price
) {

    @Override
    public String toString() {
        return "{" +
                "\"userId\":\"\"" +
                ",\"portfolioId\":\"" + portfolioId + "\"" +
                ",\"ticker\":\"" + ticker + "\"" +
                ",\"quantity\": " + quantity +
                ",\"side\":\"" + side + "\"" +
                ",\"limitPrice\":" + price +
                "}";
    }
}
