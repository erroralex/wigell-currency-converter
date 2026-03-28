package com.groupc.currency.dto;

public record ExchangeRateResponse(
        String baseCurrency,
        String targetCurrency,
        double exchangeRate
) {
}