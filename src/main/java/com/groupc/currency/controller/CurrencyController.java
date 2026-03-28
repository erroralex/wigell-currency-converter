package com.groupc.currency.controller;

import com.groupc.currency.dto.ExchangeRateResponse;
import com.groupc.currency.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/convert")
    public ResponseEntity<Double> getExchangeRate(@RequestParam String base, @RequestParam String target) {

        ExchangeRateResponse response = currencyService.getExchangeRate(base, target);

        return ResponseEntity.ok(response.exchangeRate());
    }
}