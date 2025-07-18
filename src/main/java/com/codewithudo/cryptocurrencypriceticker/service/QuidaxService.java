package com.codewithudo.cryptocurrencypriceticker.service;

import com.codewithudo.cryptocurrencypriceticker.dto.MarketData;
import com.codewithudo.cryptocurrencypriceticker.dto.QuidaxResponse;
import com.codewithudo.cryptocurrencypriceticker.dto.SingleTickerResponse;
import com.codewithudo.cryptocurrencypriceticker.dto.Ticker;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class QuidaxService {

    private static final String QUIDAX_API_BASE_URL = "https://app.quidax.io";
    private final RestTemplate restTemplate;

    public QuidaxService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Ticker> getTickers() {
        String url = QUIDAX_API_BASE_URL + "/api/v1/markets/tickers";

        // 1. Tell RestTemplate to expect the full QuidaxResponse object
        ResponseEntity<QuidaxResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        QuidaxResponse body = response.getBody();

        Map<String, Ticker> unwrappedTickers = getStringTickerMap(body);

        return unwrappedTickers;
    }

    private static Map<String, Ticker> getStringTickerMap(QuidaxResponse body) {
        Map<String, Ticker> unwrappedTickers = new LinkedHashMap<>(); // Use LinkedHashMap to preserve order

        if (body != null && "success".equals(body.getStatus())) {
            // This is the new "unwrapping" logic
            for (Map.Entry<String, MarketData> entry : body.getData().entrySet()) {
                String marketName = entry.getKey();
                Ticker ticker = entry.getValue().getTicker(); // Get the innermost Ticker object
                if (ticker != null) {
                    unwrappedTickers.put(marketName, ticker);
                }
            }
        }
        return unwrappedTickers;
    }

    public Ticker getTicker(String market) {
        String url = QUIDAX_API_BASE_URL + "/api/v1/markets/tickers/" + market;
//        ResponseEntity<QuidaxResponse> response = restTemplate.exchange(
//                url,
//                HttpMethod.GET,
//                null,
//                new ParameterizedTypeReference<>() {}
//        );
//
//        QuidaxResponse body = response.getBody();
//
//        Map<String, Ticker> unwrappedTickers = getStringTickerMap(body);
//
//        for (Map.Entry<String, Ticker> entry : unwrappedTickers.entrySet()) {
//            Ticker ticker = entry.getValue();
//            if (ticker != null) {
//                return ticker;
//            }
//        }
//        return null;


        //Cleaner Code, More Efficient
        // 1. Tell RestTemplate to expect our new SingleTickerResponse object
        SingleTickerResponse response = restTemplate.getForObject(url, SingleTickerResponse.class);

        // 2. Unwrap the nested data to get the final Ticker object
        if (response != null && "success".equals(response.getStatus())) {
            MarketData marketData = response.getData();
            if (marketData != null) {
                return marketData.getTicker();
            }
        }

        return null;
    }
}
