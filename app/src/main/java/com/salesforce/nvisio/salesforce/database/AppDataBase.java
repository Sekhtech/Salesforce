package com.salesforce.nvisio.salesforce.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by USER on 26-Dec-17.
 */
@Database(entities = {LocateSRData.class},version = 1)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase INSTANCE;
    public abstract LocateSRDao locateSRDao();

    public static AppDataBase getAppDatabase(Context context){
        if (INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),AppDataBase.class,"salesforce").allowMainThreadQueries().build();

        }
        return INSTANCE;
    }
}
