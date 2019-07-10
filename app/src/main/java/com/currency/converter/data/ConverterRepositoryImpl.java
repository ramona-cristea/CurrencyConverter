package com.currency.converter.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.currency.converter.model.CurrencyRates;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConverterRepositoryImpl implements ConverterRepository {
    private static final String BASE_URL = "https://revolut.duckdns.org";
    private CurrencyRatesService mCurrencyService;

    public ConverterRepositoryImpl() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        mCurrencyService = retrofit.create(CurrencyRatesService.class);
    }

    @Override
    public LiveData<CurrencyRates> getCurrencyRates(@NonNull String currency) {
        final MutableLiveData<CurrencyRates> currencyRatesLiveData = new MutableLiveData<>();
        Call<CurrencyRates> request = mCurrencyService.getCurrencyRates(currency);
        request.enqueue(new Callback<CurrencyRates>() {
            @Override
            public void onResponse(@NonNull Call<CurrencyRates> call, @NonNull Response<CurrencyRates> response) {
                if(response.body() != null) {
                    currencyRatesLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CurrencyRates> call, @NonNull Throwable error) {
                currencyRatesLiveData.postValue(null);
            }
        });

        return currencyRatesLiveData;
    }
}
