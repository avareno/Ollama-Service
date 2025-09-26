package com.AIve.consumer.dto;

import java.util.List;

public record StockMapping(
        List<Stock> stocks
) {
    public StockMapping {
        if (stocks == null || stocks.isEmpty()) {
            throw new IllegalArgumentException("Stocks list cannot be null or empty");
        }
        for (Stock stock : stocks) {
            if (stock.stockName() == null) {
                throw new IllegalArgumentException("Stock symbol cannot be null or empty");
            }
            if (stock.quantity() == null || stock.quantity() < 0) {
                throw new IllegalArgumentException("Stock quantity cannot be null or negative");
            }
        }
    }
}

