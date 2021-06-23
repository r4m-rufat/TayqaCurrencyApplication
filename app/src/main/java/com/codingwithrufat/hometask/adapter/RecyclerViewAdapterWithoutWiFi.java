package com.codingwithrufat.hometask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingwithrufat.hometask.R;
import com.codingwithrufat.hometask.database.CurrencyDao;
import com.codingwithrufat.hometask.database.CurrencyDatabaseModel;
import com.codingwithrufat.hometask.models.ResponseModelItem;
import com.codingwithrufat.hometask.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerViewAdapterWithoutWiFi adapter reads data list from local database and set data
 */
public class RecyclerViewAdapterWithoutWiFi extends RecyclerView.Adapter<RecyclerViewAdapterWithoutWiFi.ViewHolder>{

    private Context context;
    private List<CurrencyDatabaseModel> list;
    private double eDouble;
    private OnClickedCallBackListener listener;
    private PreferenceManager preferenceManager;

    public RecyclerViewAdapterWithoutWiFi(Context context, List<CurrencyDatabaseModel> list, double eDouble, OnClickedCallBackListener listener, PreferenceManager preferenceManager) {
        this.context = context;
        this.list = list;
        this.eDouble = eDouble;
        this.listener = listener;
        this.preferenceManager = preferenceManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_code.setText(list.get(position).getCurCode() + "");
        holder.txt_value.setText(String.format("%.3f", Double.parseDouble(list.get(position).getCurValue()) * eDouble));
        holder.txt_name.setText(list.get(position).getCurName());

        /**
         * In this adapter, item can't be clicked because it can't be "base rate" without internet
         */

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                preferenceManager.putString("rate_code", list.get(position).getCurCode());
                preferenceManager.putString("rate_name", list.get(position).getCurName());
                preferenceManager.putString("base_rate", list.get(position).getCurCode());
                listener.onChangeBaseRateWithoutWifi(list.get(position), position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_code, txt_name, txt_value;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_code = itemView.findViewById(R.id.rate_code);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            txt_name = itemView.findViewById(R.id.rate_name);
            txt_value = itemView.findViewById(R.id.txtValue);
        }
    }

    public void updateList(List<CurrencyDatabaseModel> list2, double value){

        this.list = list2;
        this.eDouble = value;
        notifyDataSetChanged();

    }

    /*
    this interface updates "base rate" when clicked recycler's item
     */
    public interface OnClickedCallBackListener {

        void onChangeBaseRateWithoutWifi(CurrencyDatabaseModel currencyDatabaseModel, int position);

    }

}
