package com.codewithudo.cryptocurrencypriceticker.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticker {
    private String low;
    private String high;

    @JsonProperty("vol")
    private String volume;

    @JsonProperty("last")
    private String price;

    @JsonProperty("sell")
    private String ask;

    @JsonProperty("buy")
    private String bid;

}
