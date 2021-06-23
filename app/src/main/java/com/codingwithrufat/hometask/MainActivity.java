package com.codingwithrufat.hometask;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.codingwithrufat.hometask.adapter.RecyclerViewAdapter;
import com.codingwithrufat.hometask.adapter.RecyclerViewAdapterWithoutWiFi;
import com.codingwithrufat.hometask.api.ApiClient;
import com.codingwithrufat.hometask.api.IApi;
import com.codingwithrufat.hometask.database.CurrencyDao;
import com.codingwithrufat.hometask.database.CurrencyDatabase;
import com.codingwithrufat.hometask.database.CurrencyDatabaseModel;
import com.codingwithrufat.hometask.models.ResponseModelItem;
import com.codingwithrufat.hometask.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnClickedCallBackListener,
RecyclerViewAdapterWithoutWiFi.OnClickedCallBackListener{

    RecyclerView recyclerView;
    ArrayList<ResponseModelItem> list;
    private CurrencyDatabase currencyDatabase;
    private static final String TAG = "MainActivity";
    private PreferenceManager preferenceManager;
    private String base_rate = "AZN";
    private EditText editRate;
    private TextView rate_name, rate_code;
    private RelativeLayout relativeLayout;
    private Context context;
    private double value = 1.0;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerViewAdapterWithoutWiFi recyclerViewAdapterWithoutWiFi;
    private CurrencyDao dao;
    private CurrencyDatabaseModel currencyDatabaseModel;
    private ProgressBar progressBar;
    private IApi api;
    private LinearLayoutManager llM;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        getWidgets();
        setupRecyclerView();
        instantiateClasses();
        putBaseRateToTheStorage();
        checkDatabase();
        editRateTextChanged(editRate);
        getInfoForFirstTime(dao, currencyDatabaseModel, preferenceManager.getString("base_rate"), value);
        setupAdapterWithoutWifi();
        updateListItems();


    }

    // widgets in constant layout item and intialize them
    private void getWidgets() {
        editRate = findViewById(R.id.etxtValueConst);
        rate_code = findViewById(R.id.rate_codeConst);
        rate_name = findViewById(R.id.rate_nameConst);
        relativeLayout = findViewById(R.id.relativeLayoutConst);
        progressBar = findViewById(R.id.progressBar);
    }

    // recycler setup
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(false);
        llM = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(llM);
    }

    // interfaces and classes
    private void instantiateClasses(){
        preferenceManager = new PreferenceManager(context);
        currencyDatabase = Room.databaseBuilder(getApplicationContext(), CurrencyDatabase.class, "db_currency").allowMainThreadQueries().build();
        dao = currencyDatabase.currencyDao();
        currencyDatabaseModel = new CurrencyDatabaseModel();
        api = new ApiClient().getRetrofit().create(IApi.class);
    }

    /**
     * when edit text changed then TextWatcher interface's onTextChanged get string this is not essential
     * because check it every 1 second
     * @param editText
     */
    private void editRateTextChanged(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String sValue = s.toString();
                if (sValue.isEmpty()) {
                    value = 1.0;
                } else {
                    try {
                        value = Double.parseDouble(sValue);
                        getInfoForFirstTime(dao, currencyDatabaseModel, preferenceManager.getString("base_rate"), value);
                    } catch (NumberFormatException e) {
                        Toast.makeText(context, "Please write right number format", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // when base rate null, put "AZN" to internal storage otherwise it is changed item click then put new one to it
    private void putBaseRateToTheStorage(){
        if (preferenceManager.getString("base_rate").equals("")){
            preferenceManager.putString("base_rate", "AZN");
            preferenceManager.putString("rate_name", "Azerbaijan Manat");
            preferenceManager.putString("rate_code", "AZN");
            rate_name.setText("Azerbaijan Manat");
            rate_code.setText("AZN");
        }else{
            rate_name.setText(preferenceManager.getString("rate_name"));
            rate_code.setText(preferenceManager.getString("rate_code"));
        }

        editRate.setText("1");
    }

    // if internet connnection is lost set adapter to recycler in this condition
    private void setupAdapterWithoutWifi(){
        if (!checkInternetConnection()){
            recyclerViewAdapterWithoutWiFi = new RecyclerViewAdapterWithoutWiFi(MainActivity.this, dao.getAllCurrency(), value, MainActivity.this, preferenceManager);
            recyclerView.setAdapter(recyclerViewAdapterWithoutWiFi);
        }
    }

    // database is checked(it is empty or not)
    private void checkDatabase(){
        if (dao.getAllCurrency().isEmpty()) {
            preferenceManager.putString("situation_db", "empty");
        } else {
            preferenceManager.putString("situation_db", "full");
        }
    }

    // update all items every 1 second and repeat this call
    private void updateListItems(){
        runnable = new Runnable() {

            @Override
            public void run() {
                new Handler().postDelayed(this, 1000);
                if (checkInternetConnection()) {
                    if (!editRate.getText().toString().isEmpty()) {
                        getDynamicCurrencyInformations(dao, currencyDatabaseModel, preferenceManager.getString("base_rate"), Double.parseDouble(editRate.getText().toString()));
                    } else {
                        getDynamicCurrencyInformations(dao, currencyDatabaseModel, preferenceManager.getString("base_rate"), 1.0);
                    }
                }
            }
        };
        runnable.run();
    }

    // check internet connection(on or off) and return boolean
    public boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connection = networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        return connection;
    }


    private void getAllCurrencyInformationWithEnqueue(ResponseModelItem responseModelItem, CurrencyDao dao, String base_rate, double eDouble) {

        editRate.setText("1");
        rate_name.setText(responseModelItem.getName());
        rate_code.setText(responseModelItem.getCode());

        CurrencyDatabaseModel currencyDatabaseModel = new CurrencyDatabaseModel();

        IApi api = new ApiClient().getRetrofit().create(IApi.class);

        Call<ArrayList<ResponseModelItem>> call = api.getCurrencyInformations(base_rate);

        call.enqueue(new Callback<ArrayList<ResponseModelItem>>() {
            @Override
            public void onResponse(Call<ArrayList<ResponseModelItem>> call, Response<ArrayList<ResponseModelItem>> response) {
                if (response.isSuccessful()) {
                    list = response.body();
                    for (int position = 1; position <= list.size(); position++) {
                        if (preferenceManager.getString("situation_db").equals("full")) {

                            currencyDatabaseModel.curID = position;
                            currencyDatabaseModel.curName = list.get(position - 1).getName();
                            currencyDatabaseModel.curCode = list.get(position - 1).getCode();
                            currencyDatabaseModel.curValue = String.format("%.3f", list.get(position - 1).getRate());
                            dao.updateCurrency(currencyDatabaseModel);

                        } else if (preferenceManager.getString("situation_db").equals("empty")) {

                            currencyDatabaseModel.curCode = list.get(position - 1).getCode();
                            currencyDatabaseModel.curName = list.get(position - 1).getName();
                            currencyDatabaseModel.curValue = String.format("%.3f", list.get(position - 1).getRate());
                            dao.insertAllCurrency(currencyDatabaseModel);

                        }
                    }

                    recyclerViewAdapter = new RecyclerViewAdapter(context, list, dao, preferenceManager, currencyDatabaseModel, MainActivity.this, value);
                    recyclerViewAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(recyclerViewAdapter);

                }
            }

            @Override
            public void onFailure(Call<ArrayList<ResponseModelItem>> call, Throwable t) {
                recyclerViewAdapterWithoutWiFi = new RecyclerViewAdapterWithoutWiFi(MainActivity.this, dao.getAllCurrency(), value, MainActivity.this, preferenceManager);
                recyclerViewAdapterWithoutWiFi.notifyDataSetChanged();
                recyclerView.setAdapter(recyclerViewAdapterWithoutWiFi);
            }
        });

    }

    private void getDynamicCurrencyInformations(CurrencyDao dao, CurrencyDatabaseModel currencyDatabaseModel, String base_rate, double value) {
        IApi api = new ApiClient().getRetrofit().create(IApi.class);

        Call<ArrayList<ResponseModelItem>> call = api.getCurrencyInformations(base_rate);

        call.enqueue(new Callback<ArrayList<ResponseModelItem>>() {
            @Override
            public void onResponse(Call<ArrayList<ResponseModelItem>> call, Response<ArrayList<ResponseModelItem>> response) {
                if (response.isSuccessful()) {
                    list = response.body();
                    for (int position = 1; position <= list.size(); position++) {
                        if (preferenceManager.getString("situation_db").equals("full")) {

                            currencyDatabaseModel.curID = position;
                            currencyDatabaseModel.curName = list.get(position - 1).getName();
                            currencyDatabaseModel.curCode = list.get(position - 1).getCode();
                            currencyDatabaseModel.curValue = String.format("%.3f", list.get(position - 1).getRate());
                            dao.updateCurrency(currencyDatabaseModel);

                        } else if (preferenceManager.getString("situation_db").equals("empty")) {

                            currencyDatabaseModel.curCode = list.get(position - 1).getCode();
                            currencyDatabaseModel.curName = list.get(position - 1).getName();
                            currencyDatabaseModel.curValue = String.format("%.3f", list.get(position - 1).getRate());
                            dao.insertAllCurrency(currencyDatabaseModel);

                        }
                    }

                    try {
                        recyclerViewAdapter.updateList(list, value);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }

                    preferenceManager.putString("situation_db", "full");

                }
            }

            @Override
            public void onFailure(Call<ArrayList<ResponseModelItem>> call, Throwable t) {
                try {
                    recyclerViewAdapterWithoutWiFi.updateList(dao.getAllCurrency(), value);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

    }

    private void getInfoForFirstTime(CurrencyDao dao, CurrencyDatabaseModel currencyDatabaseModel, String base_rate, double value){
        Call<ArrayList<ResponseModelItem>> call = api.getCurrencyInformations(base_rate);
        call.enqueue(new Callback<ArrayList<ResponseModelItem>>() {
            @Override
            public void onResponse(Call<ArrayList<ResponseModelItem>> call, Response<ArrayList<ResponseModelItem>> response) {
                if (response.isSuccessful()) {

                    progressBar.setVisibility(View.INVISIBLE);

                    list = response.body();
                    for (int position = 1; position <= list.size(); position++) {
                        if (preferenceManager.getString("situation_db").equals("full")) {

                            currencyDatabaseModel.curID = position;
                            currencyDatabaseModel.curName = list.get(position - 1).getName();
                            currencyDatabaseModel.curCode = list.get(position - 1).getCode();
                            currencyDatabaseModel.curValue = String.format("%.3f", list.get(position - 1).getRate());
                            dao.updateCurrency(currencyDatabaseModel);

                        } else if (preferenceManager.getString("situation_db").equals("empty")) {

                            currencyDatabaseModel.curCode = list.get(position - 1).getCode();
                            currencyDatabaseModel.curName = list.get(position - 1).getName();
                            currencyDatabaseModel.curValue = String.format("%.3f", list.get(position - 1).getRate());
                            dao.insertAllCurrency(currencyDatabaseModel);

                        }
                    }

                    relativeLayout.setVisibility(View.VISIBLE);

                    recyclerViewAdapter = new RecyclerViewAdapter(context, list, dao, preferenceManager, currencyDatabaseModel, MainActivity.this, value);
                    recyclerViewAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(recyclerViewAdapter);

                    preferenceManager.putString("situation_db", "full");

                }
            }

            @Override
            public void onFailure(Call<ArrayList<ResponseModelItem>> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                recyclerViewAdapterWithoutWiFi = new RecyclerViewAdapterWithoutWiFi(MainActivity.this, dao.getAllCurrency(), value, MainActivity.this, preferenceManager);
                recyclerViewAdapterWithoutWiFi.notifyDataSetChanged();
                recyclerView.setAdapter(recyclerViewAdapterWithoutWiFi);
            }
        });
    }


    @Override
    public void onChangeBaseRate(ResponseModelItem responseModelItem, String base_rate) {
        getAllCurrencyInformationWithEnqueue(responseModelItem, currencyDatabase.currencyDao(), base_rate, Double.parseDouble(editRate.getText().toString()));
    }

    @Override
    public void onChangeBaseRateWithoutWifi(CurrencyDatabaseModel currencyDatabaseModel, int position) {
        putBaseRateToTheStorage();
        List<CurrencyDatabaseModel> databaseList = dao.getAllCurrency();
        databaseList.remove(position);
        recyclerViewAdapterWithoutWiFi = new RecyclerViewAdapterWithoutWiFi(context, dao.getAllCurrency(), Double.parseDouble(String.valueOf(1/Double.parseDouble(currencyDatabaseModel.getCurValue()))),MainActivity.this,  preferenceManager);
        recyclerView.setAdapter(recyclerViewAdapterWithoutWiFi);
    }
}