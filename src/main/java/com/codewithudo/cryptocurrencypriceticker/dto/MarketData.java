package com.codewithudo.cryptocurrencypriceticker.dto;

import lombok.Data;

@Data
public class MarketData {
    private long at;
    private Ticker ticker;
}