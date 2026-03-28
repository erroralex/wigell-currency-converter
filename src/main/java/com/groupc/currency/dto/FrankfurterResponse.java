package com.groupc.currency.dto;

import java.util.Map;

public record FrankfurterResponse(
        double amount,
        String base,
        String date,
        Map<String, Double> rates
) {
}
