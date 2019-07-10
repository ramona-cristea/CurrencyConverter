package com.currency.converter.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.currency.converter.R;
import com.currency.converter.model.CurrencyViewModel;
import com.currency.converter.model.Rate;

import java.util.List;

public class ConverterActivity extends AppCompatActivity implements RatesAdapter.RatesAdapterHandler{
    CurrencyViewModel mCurrencyViewModel;
    private static final String BASE_CURRENCY = "EUR";
    private static final Double BASE_VALUE = 1.0;
    private RatesAdapter mRatesAdapter;
    private String mSelectedCurrency = BASE_CURRENCY;
    private Double mSelectedValue = BASE_VALUE;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        mCurrencyViewModel = ViewModelProviders.of(this).get(CurrencyViewModel.class);
        recyclerView = findViewById(R.id.recyclerview_rates);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        mRatesAdapter = new RatesAdapter(null, this);
        mRatesAdapter.setSelectedCurrency(mSelectedCurrency);
        recyclerView.setAdapter(mRatesAdapter);
        mCurrencyViewModel.setBaseCurrency(BASE_CURRENCY, BASE_VALUE, false);
        mCurrencyViewModel.getPeriodicRatesData().observe(this, observerRates);
    }

    Observer<List<Rate>> observerRates = this::handleResponse;

    private void handleResponse(List<Rate> rates) {
        if (rates == null) {
            return;
        }
        mRatesAdapter.setData(rates);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrencyViewModel.retrieveCurrencyRates();
    }

    @Override
    public void onItemSelected(Rate currency, Double value) {
        mCurrencyViewModel.getPeriodicRatesData().removeObserver(observerRates);
        recyclerView.scrollToPosition(0);
        mSelectedCurrency = currency.getCode();
        mSelectedValue = value;
        mCurrencyViewModel.setBaseCurrency(mSelectedCurrency, mSelectedValue, false);
        mCurrencyViewModel.getPeriodicRatesData().observe(this, observerRates);
    }

    @Override
    public void onCurrencyValueUpdated(String currency, Double value) {
        mSelectedCurrency = currency;
        mSelectedValue = value;
        mCurrencyViewModel.setBaseCurrency(mSelectedCurrency, mSelectedValue, true);
        mRatesAdapter.setSelectedCurrency(mSelectedCurrency);
    }
}
