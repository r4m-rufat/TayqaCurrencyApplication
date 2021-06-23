package com.codingwithrufat.hometask.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * in abstract class extends RoomDatabase and then create dao method without body
 */
@Database(entities = {CurrencyDatabaseModel.class}, version = 1)
public abstract class CurrencyDatabase extends RoomDatabase {
    public abstract CurrencyDao currencyDao();
}
