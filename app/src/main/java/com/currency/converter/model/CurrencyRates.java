package com.currency.converter.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class CurrencyRates {

    @SerializedName("base")
    private String base;

    @SerializedName("rates")
    private Map<String, Double> rates;

    Map<String, Double> getRates() {
        return rates;
    }

    void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }
}
