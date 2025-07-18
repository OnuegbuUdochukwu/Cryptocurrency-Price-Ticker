package com.codewithudo.cryptocurrencypriceticker.controller;

import com.codewithudo.cryptocurrencypriceticker.dto.Ticker;
import com.codewithudo.cryptocurrencypriceticker.service.QuidaxService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/markets")
public class TickerController {

    private final QuidaxService quidaxService;

    public TickerController(QuidaxService quidaxService) {
        this.quidaxService = quidaxService;
    }

    @GetMapping("/tickers")
    public Map<String, Ticker> getAllTickers() {
        return quidaxService.getTickers();
    }

    @GetMapping("/tickers/{market}")
    public Ticker getTickerByMarket(@PathVariable String market) {
        return quidaxService.getTicker(market);
    }
}
