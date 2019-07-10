package com.currency.converter.views;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.currency.converter.R;
import com.currency.converter.model.Rate;

import java.text.DecimalFormat;
import java.util.List;

public class RatesAdapter extends RecyclerView.Adapter<RatesAdapter.RatesViewHolder> {
    private List<Rate> items;
    private final RatesAdapterHandler mListener;
    private String mSelectedCurrency;
    private DecimalFormat mDecimalFormat = new DecimalFormat("#.##");

    RatesAdapter(List<Rate> rates, RatesAdapterHandler listener) {
        items = rates;
        mListener = listener;
    }

    void setSelectedCurrency(final String currency) {
        mSelectedCurrency = currency;
    }

    @NonNull
    @Override
    public RatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_rate, parent, false);

        return new RatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatesViewHolder holder, int position) {
        Rate itemRate = items.get(position);
        holder.bind(itemRate);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    void setData(List<Rate> rates) {
        items = rates;
        notifyDataSetChanged();
    }

    public interface RatesAdapterHandler {
        void onItemSelected(Rate currency, Double input);
        void onCurrencyValueUpdated(String currency, Double value);
    }

    public class RatesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTextRateKey;
        TextView mTextRateName;
        EditText mEditValue;
        RelativeLayout mLayoutRateItem;

        private Rate mItemRate;

        RatesViewHolder(View itemView) {
            super(itemView);
            mTextRateKey = itemView.findViewById(R.id.textview_rate_key);
            mTextRateName = itemView.findViewById(R.id.textview_rate_name);
            mEditValue = itemView.findViewById(R.id.edit_value);
            mLayoutRateItem = itemView.findViewById(R.id.layout_item);
            mLayoutRateItem.setOnClickListener(this);
        }

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing to do here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //nothing to do here
            }

            @Override
            public void afterTextChanged(Editable editable) {

                int position = getAdapterPosition();
                if(position != 0) {
                    return;
                }
                String newValue = editable.toString();
                Double value = TextUtils.isEmpty(newValue) ? 0d :
                        Double.valueOf(newValue);
                mListener.onCurrencyValueUpdated(mSelectedCurrency, value);
            }
        };

        void bind(Rate item) {
            mItemRate = item;
            mTextRateKey.setText(mItemRate.getCode());
            mTextRateName.setText(mItemRate.getName());
            mEditValue.removeTextChangedListener(watcher);
            if(item.getComputedValue() == 0) {
                mEditValue.setText("");
            } else {
                String text = mDecimalFormat.format(item.getComputedValue());
                mEditValue.setText(text);
                mEditValue.setSelection(text.length());
            }
            mEditValue.addTextChangedListener(watcher);
        }

        @Override
        public void onClick(View view) {
            Double input = TextUtils.isEmpty(mEditValue.getText().toString()) ? 0d :
                    Double.valueOf(mEditValue.getText().toString());
            swapItems(getAdapterPosition(), 0);
            mListener.onItemSelected(mItemRate, input);
            mSelectedCurrency = mItemRate.getCode();
        }
    }

    private void swapItems(int fromPosition, int toPosition){
        Rate movingItem = this.items.remove(fromPosition);
        if (fromPosition < toPosition) {
            this.items.add(toPosition - 1, movingItem);
        } else {
            this.items.add(toPosition, movingItem);
        }

        notifyItemMoved(fromPosition, toPosition);
    }
}