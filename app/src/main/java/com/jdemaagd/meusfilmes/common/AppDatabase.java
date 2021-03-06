package com.jdemaagd.meusfilmes.common;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.jdemaagd.meusfilmes.R;
import com.jdemaagd.meusfilmes.data.DateConverter;
import com.jdemaagd.meusfilmes.data.MovieDao;
import com.jdemaagd.meusfilmes.models.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {

    private static final String LOG_TAG = AppDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static AppDatabase sInstance;

    public static AppDatabase getInstance(Context context) {

        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(LOG_TAG, "Creating new database instance.");
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, context.getString(R.string.database_name))
                        .build();
            }
        }
        Log.d(LOG_TAG, "Getting database instance.");

        return sInstance;
    }

    public abstract MovieDao movieDao();
}
