package com.codingwithrufat.hometask.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Preference Manager class is for internal storage
 */
public class PreferenceManager {

    private SharedPreferences sharedPreferences;

    public PreferenceManager(Context context){
        sharedPreferences = context.getSharedPreferences("name",Context.MODE_PRIVATE);
    }

    // create put string method which string is written storage
    public void putString(String key, String value){

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(key, value);
        editor.apply();

    }

    // get string from internal storage
    public String getString(String key){

        return sharedPreferences.getString(key, "");

    }

}
