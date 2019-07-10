package com.currency.converter.model;

public class Rate {
    private String mCode;
    private String mName;
    private Double mComputedValue;

    public Rate(String code, String name, Double computedValue) {
        this.mCode = code;
        this.mName = name;
        this.mComputedValue = computedValue;
    }

    public String getCode() {
        return mCode;
    }

    public String getName() {
        return mName;
    }

    public Double getComputedValue() {
        return mComputedValue;
    }
}
