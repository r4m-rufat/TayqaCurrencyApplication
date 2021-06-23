package com.codingwithrufat.hometask.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CurrencyDao {

    /**
     * code which inside of @Query gets all database object list
     * @return
     */
    @Query("SELECT * FROM currency_table")
    List<CurrencyDatabaseModel> getAllCurrency();

    /**
     * insert datas with model
     * @param currencyDatabaseModel
     */
    @Insert
    void insertAllCurrency(CurrencyDatabaseModel currencyDatabaseModel);

    /**
     * update datas with model
     * @param currencyDatabaseModel
     */
    @Update
    void updateCurrency(CurrencyDatabaseModel currencyDatabaseModel);

}
