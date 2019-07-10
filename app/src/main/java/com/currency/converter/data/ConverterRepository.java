package com.currency.converter.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.currency.converter.model.CurrencyRates;

public interface ConverterRepository {
    LiveData<CurrencyRates> getCurrencyRates(@NonNull final String currency);
}
