package com.codingwithrufat.hometask.api;

import com.codingwithrufat.hometask.models.ResponseModelItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IApi {

    /**
     * query is "base" and it is for sending base rate name to service
     * @param rate_name
     * @return
     */
    @GET("rates.php?")
    Call<ArrayList<ResponseModelItem>> getCurrencyInformations(@Query("base") String rate_name);

}
