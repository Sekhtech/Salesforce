package com.salesforce.nvisio.salesforce.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by USER on 26-Dec-17.
 */
@Entity(tableName = "position")
public class LocateSRData {
    @PrimaryKey(autoGenerate = true)
    public int rowid;

    public int SR_id;
    public String latitude;
    public String longitude;
    public String date;
    public String timeLocate;
    public String timestamp;

    public int getRowid() {
        return rowid;
    }

    public void setRowid(int rowid) {
        this.rowid = rowid;
    }

    public int getSR_id() {
        return SR_id;
    }

    public void setSR_id(int SR_id) {
        this.SR_id = SR_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeLocate() {
        return timeLocate;
    }

    public void setTimeLocate(String timeLocate) {
        this.timeLocate = timeLocate;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public LocateSRData() {

    }
}
