package com.groupc.currency.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupc.currency.dto.ExchangeRateResponse;
import com.groupc.currency.dto.FrankfurterResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrencyService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    private static final String FRANKFURTER_API_URL = "https://api.frankfurter.dev/v1/latest?base={base}&symbols={target}";

    public CurrencyService(ObjectMapper objectMapper) {
        this.restClient = RestClient.create();
        this.objectMapper = objectMapper;
    }

    public ExchangeRateResponse getExchangeRate(String baseCurrency, String targetCurrency) {

        if (baseCurrency.equalsIgnoreCase(targetCurrency)) {
            return new ExchangeRateResponse(baseCurrency, targetCurrency, 1.0);
        }

        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restClient.get()
                    .uri(FRANKFURTER_API_URL, baseCurrency.toUpperCase(), targetCurrency.toUpperCase())
                    .header("User-Agent", "WigellPadelBackend/1.0")
                    .retrieve()
                    .toEntity(String.class);

            logger.info("Frankfurter HTTP Status: {}", responseEntity.getStatusCode());
            logger.info("Frankfurter Body: {}", responseEntity.getBody());

        } catch (Exception e) {
            logger.error("Kunde inte nå Frankfurter API", e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Valutatjänsten är otillgänglig", e);
        }

        String rawJson = responseEntity.getBody();

        if (rawJson == null || rawJson.isBlank()) {
            logger.error("Fick ett tomt svar från Frankfurter. Statuskod: {}", responseEntity.getStatusCode());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Tomt svar från extern valutatjänst");
        }

        try {
            FrankfurterResponse response = objectMapper.readValue(rawJson, FrankfurterResponse.class);

            if (response == null || response.rates() == null || !response.rates().containsKey(targetCurrency.toUpperCase())) {
                logger.error("Kunde inte extrahera kurs för {}. JSON: {}", targetCurrency, rawJson);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kunde inte hämta valutakurs för " + targetCurrency);
            }

            double rate = response.rates().get(targetCurrency.toUpperCase());
            return new ExchangeRateResponse(baseCurrency.toUpperCase(), targetCurrency.toUpperCase(), rate);

        } catch (Exception e) {
            logger.error("Internt datafel vid konvertering av JSON", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Kunde inte tolka valutasvaret");
        }
    }
}