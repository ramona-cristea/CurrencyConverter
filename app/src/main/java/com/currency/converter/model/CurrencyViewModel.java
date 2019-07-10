package com.currency.converter.model;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.currency.converter.data.ConverterRepository;
import com.currency.converter.data.ConverterRepositoryImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;

public class CurrencyViewModel extends ViewModel {
    private MediatorLiveData<List<Rate>> mLiveDataPeriodicRates;
    private LiveData<CurrencyRates> mCurrencyRatesLiveData;
    private ConverterRepository mConverterRepository;
    private static final int ONE_SECOND = 1000;
    private String mBaseCurrency;
    private Double mBaseValue;
    private Handler handler;

    public CurrencyViewModel() {
        handler = new Handler();
        mLiveDataPeriodicRates = new MediatorLiveData<>();
        mCurrencyRatesLiveData = new MediatorLiveData<>();
        mConverterRepository = new ConverterRepositoryImpl();
    }

    private Runnable runnable = this::retrieveCurrencyRates;

    public void setBaseCurrency(@NonNull final String baseCurrency, Double baseValue, boolean recalculate) {
        mBaseCurrency = baseCurrency;
        mBaseValue = baseValue;
        if(recalculate) {
            handler.removeCallbacks(runnable);
            LiveData<List<Rate>> liveData = Transformations.map(mCurrencyRatesLiveData, this::performConversionForCurrencies);
            mLiveDataPeriodicRates.addSource(liveData, rates -> {
                mLiveDataPeriodicRates.setValue(rates);
                handler.postDelayed(runnable, ONE_SECOND);
            });
        }
    }

    @NonNull
    public LiveData<List<Rate>> getPeriodicRatesData() {
        return mLiveDataPeriodicRates;
    }

    public void retrieveCurrencyRates() {
        mCurrencyRatesLiveData = mConverterRepository.getCurrencyRates(mBaseCurrency);
        mCurrencyRatesLiveData.observeForever(currencyRates -> {
            mLiveDataPeriodicRates.setValue(performConversionForCurrencies(currencyRates));
            handler.postDelayed(runnable, ONE_SECOND);
        });

    }

    private List<Rate> performConversionForCurrencies(CurrencyRates currencyRates){
        List<Rate> listRates = new ArrayList<>();
        for (Map.Entry<String, Double> entry : currencyRates.getRates().entrySet()) {
            listRates.add(new Rate(entry.getKey(),
                    Currency.getInstance(entry.getKey()).getDisplayName(),
                    mBaseValue * entry.getValue()));

        }
        Collections.sort(listRates, (rate1, rate2) -> rate1.getCode().compareTo(rate2.getCode()));
        listRates.add(0, new Rate(mBaseCurrency,
                Currency.getInstance(mBaseCurrency).getDisplayName(),
                mBaseValue));
        return listRates;
    }
}
