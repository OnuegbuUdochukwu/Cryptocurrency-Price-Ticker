package com.codewithudo.cryptocurrencypriceticker.dto;

import lombok.Data;

@Data
public class SingleTickerResponse {
    private String status;
    private String message;
    private MarketData data; // We can reuse our MarketData DTO here
}