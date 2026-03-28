package com.groupc.currency.service;

import com.groupc.currency.dto.ExchangeRateResponse;
import com.groupc.currency.dto.FrankfurterResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class CurrencyService {

    private final RestClient restClient;
    private static final String FRANKFURTER_API_URL = "https://api.frankfurter.app/latest?from={from}&to={to}";

    public CurrencyService() {
        this.restClient = RestClient.create();
    }

    public ExchangeRateResponse getExchangeRate(String baseCurrency, String targetCurrency) {

        if (baseCurrency.equalsIgnoreCase(targetCurrency)) {
            return new ExchangeRateResponse(baseCurrency, targetCurrency, 1.0);
        }

        try {
            FrankfurterResponse response = restClient.get()
                    .uri(FRANKFURTER_API_URL, baseCurrency, targetCurrency)
                    .retrieve()
                    .body(FrankfurterResponse.class);

            if (response == null || response.rates() == null || !response.rates().containsKey(targetCurrency.toUpperCase())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kunde inte hämta valutakurs för " + targetCurrency);
            }

            double rate = response.rates().get(targetCurrency.toUpperCase());
            return new ExchangeRateResponse(baseCurrency.toUpperCase(), targetCurrency.toUpperCase(), rate);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Valutatjänsten är för närvarande otillgänglig", e);
        }
    }
}