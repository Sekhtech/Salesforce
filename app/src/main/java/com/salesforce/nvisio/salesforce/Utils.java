package com.salesforce.nvisio.salesforce;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by USER on 14-May-17.
 */

public class Utils {
    private static FirebaseDatabase mDatabase;

    public static  FirebaseDatabase getmDatabase(){
        if (mDatabase==null){
            mDatabase=FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

  /*  public static getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;

    }*/
}
