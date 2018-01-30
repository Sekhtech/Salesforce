package com.salesforce.nvisio.salesforce.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by USER on 26-Dec-17.
 */
@Dao
public interface LocateSRDao {
    @Insert
    void insertLocatePosition(LocateSRData locateSRData);

    @Update
    void updateLocateSR(LocateSRData locateSRData);

    @Query("SELECT COUNT(*) FROM position")
    int positionTableCount();
}
