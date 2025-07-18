package com.codewithudo.cryptocurrencypriceticker.service;

import com.codewithudo.cryptocurrencypriceticker.dto.Ticker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class QuidaxService {

    private static final String QUIDAX_API_BASE_URL = "https://app.quidax.io";
    private final RestTemplate restTemplate;

    public QuidaxService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Ticker> getTickers() {
        String url = QUIDAX_API_BASE_URL + "/api/v1/tickers";

        // We use ParameterizedTypeReference to help Jackson understand the complex return type:
        // a Map of Strings to Tickers.
        ResponseEntity<Map<String, Ticker>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public Ticker getTicker(String market) {
        String url = QUIDAX_API_BASE_URL + "/api/v1/tickers/" + market;
        return restTemplate.getForObject(url, Ticker.class);
    }
}
