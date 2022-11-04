package com.example.blocky.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey
    @NonNull
    public String username;
    @ColumnInfo
    @NonNull
    public String password;
    @ColumnInfo(name = "analog_clock")
    public int analogClock;
    @ColumnInfo(name = "digital_clock")
    public int digitalClock;
    @ColumnInfo
    public int season;
    @ColumnInfo
    public int day;
    @ColumnInfo
    public int month;
    @ColumnInfo
    public int digit;
    @ColumnInfo
    public int direction;
    @ColumnInfo
    public int word;
    @ColumnInfo
    public int multiply;
    @ColumnInfo
    public int picture;

    public User(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
    }
}
