package com.currency.converter.data;

import com.currency.converter.model.CurrencyRates;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface CurrencyRatesService {
    @Headers({"Accept: application/json"})
    @GET("/latest")
    Call<CurrencyRates> getCurrencyRates(@Query("base") String baseCurrency);
}



