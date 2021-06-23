package com.codingwithrufat.hometask.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * is built table items model
 */
@Entity(tableName = "currency_table")
public class CurrencyDatabaseModel {
    @PrimaryKey(autoGenerate = true)
    public int curID;
    public String curName;
    public String curCode;
    public String curValue;

    public int getCurID() {
        return curID;
    }

    public void setCurID(int curID) {
        this.curID = curID;
    }

    public String getCurName() {
        return curName;
    }

    public void setCurName(String curName) {
        this.curName = curName;
    }

    public String getCurCode() {
        return curCode;
    }

    public void setCurCode(String curCode) {
        this.curCode = curCode;
    }

    public String getCurValue() {
        return curValue;
    }

    public void setCurValue(String curValue) {
        this.curValue = curValue;
    }

}
