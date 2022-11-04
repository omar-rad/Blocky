package com.example.blocky.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


@Dao
public interface UserDao {
    @Query("UPDATE user SET analog_clock = :score WHERE username = :username")
    void setAnalogClock(String username, int score);

    @Query("UPDATE user SET digital_clock = :score WHERE username = :username")
    void setDigitalClock(String username, int score);

    @Query("UPDATE user SET season = :score WHERE username = :username")
    void setSeason(String username, int score);

    @Query("UPDATE user SET day = :score WHERE username = :username")
    void setDay(String username, int score);

    @Query("UPDATE user SET month = :score WHERE username = :username")
    void setMonth(String username, int score);

    @Query("UPDATE user SET digit = :score WHERE username = :username")
    void setDigit(String username, int score);

    @Query("UPDATE user SET direction = :score WHERE username = :username")
    void setDirection(String username, int score);

    @Query("UPDATE user SET word = :score WHERE username = :username")
    void setWord(String username, int score);

    @Query("UPDATE user SET multiply = :score WHERE username = :username")
    void setMultiply(String username, int score);

    @Query("UPDATE user SET picture = :score WHERE username = :username")
    void setPicture(String username, int score);

    @Query("SELECT analog_clock from user WHERE username = :username")
    int getAnalogClock(String username);

    @Query("SELECT digital_clock from user WHERE username = :username")
    int getDigitalClock(String username);

    @Query("SELECT season from user WHERE username = :username")
    int getSeason(String username);

    @Query("SELECT day from user WHERE username = :username")
    int getDay(String username);

    @Query("SELECT month from user WHERE username = :username")
    int getMonth(String username);

    @Query("SELECT digit from user WHERE username = :username")
    int getDigit(String username);

    @Query("SELECT direction from user WHERE username = :username")
    int getDirection(String username);

    @Query("SELECT word from user WHERE username = :username")
    int getWord(String username);

    @Query("SELECT multiply from user WHERE username = :username")
    int getMultiply(String username);

    @Query("SELECT picture from user WHERE username = :username")
    int getPicture(String username);

    @Query("SELECT password from user WHERE username = :username")
    String getPassword(String username);

    @Insert
    void insert(User user);
}
