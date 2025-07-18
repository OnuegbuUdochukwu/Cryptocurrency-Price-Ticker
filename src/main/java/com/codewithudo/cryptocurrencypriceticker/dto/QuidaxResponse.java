package com.codewithudo.cryptocurrencypriceticker.dto;

import lombok.Data;
import java.util.Map;

@Data
public class QuidaxResponse {
    private String status;
    private Map<String, MarketData> data;
}