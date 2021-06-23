package com.codingwithrufat.hometask.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingwithrufat.hometask.R;
import com.codingwithrufat.hometask.database.CurrencyDao;
import com.codingwithrufat.hometask.database.CurrencyDatabaseModel;
import com.codingwithrufat.hometask.models.ResponseModelItem;
import com.codingwithrufat.hometask.utils.PreferenceManager;

import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ResponseModelItem> list;
    private CurrencyDao currencyDao;
    private PreferenceManager preferenceManager;
    private CurrencyDatabaseModel currencyDatabaseModel;
    private OnClickedCallBackListener listener;
    private double eDouble;

    public RecyclerViewAdapter(Context context, ArrayList<ResponseModelItem> list, CurrencyDao currencyDao, PreferenceManager preferenceManager, CurrencyDatabaseModel currencyDatabaseModel, OnClickedCallBackListener listener, double eDouble) {
        this.context = context;
        this.list = list;
        this.currencyDao = currencyDao;
        this.preferenceManager = preferenceManager;
        this.currencyDatabaseModel = currencyDatabaseModel;
        this.listener = listener;
        this.eDouble = eDouble;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txt_code.setText(list.get(position).getCode() + "");
        holder.txt_value.setText(String.format("%.3f", list.get(position).getRate() * eDouble) + "");
        holder.txt_name.setText(list.get(position).getName());

        // ------------ relative layout belongs to whole recycler's item ------------
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onChangeBaseRate(list.get(position), list.get(position).getCode());
                preferenceManager.putString("rate_code", list.get(position).getCode());
                preferenceManager.putString("rate_name", list.get(position).getName());
                preferenceManager.putString("base_rate", list.get(position).getCode());

                for (int i = 1; i <= list.size(); i++) {

                    // local database items
                    currencyDatabaseModel.curID = i;  // id
                    currencyDatabaseModel.curName = list.get(i - 1).getName(); // currency name
                    currencyDatabaseModel.curValue = String.format("%.3f", list.get(i - 1).getRate()) + "";  // currency rate
                    currencyDatabaseModel.curCode = list.get(i - 1).getCode();  // currency code
                    currencyDao.updateCurrency(currencyDatabaseModel); // update database

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // initialize layout widgets in view holder
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

    /*
    this interface updates "base rate" when clicked recycler's item
     */
    public interface OnClickedCallBackListener {

        void onChangeBaseRate(ResponseModelItem responseModelItem, String base_rate);

    }

    public void updateList(ArrayList<ResponseModelItem> list2, double value) {

        this.list = list2;
        this.eDouble = value;
        notifyDataSetChanged();

    }

}
