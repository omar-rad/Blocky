package com.example.blocky.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase DB;

    public abstract UserDao userDao();

    public static AppDatabase getDatabase(final Context context) {
        if (DB == null) {
            DB = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    "app_database").build();
        }
        return DB;
    }
}
