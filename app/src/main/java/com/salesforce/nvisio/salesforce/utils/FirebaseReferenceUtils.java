package com.salesforce.nvisio.salesforce.utils;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salesforce.nvisio.salesforce.R;

/**
 * Created by USER on 30-Jan-18.
 */

public class FirebaseReferenceUtils {
    private Context context;
    private DatabaseReference root;

    public FirebaseReferenceUtils(Context context) {
        this.context = context;
        root= FirebaseDatabase.getInstance().getReference();
    }

    //Path reference
    public DatabaseReference ManagerCredentialRef(){
        return root.child(context.getResources().getString(R.string.firebase_managerCredential));
    }

    public DatabaseReference srCredentialRef(){
        return root.child(context.getResources().getString(R.string.firebase_srCredential));
    }

    public DatabaseReference getSrProfileRef(String SID){
        DatabaseReference profile=root.child(context.getResources().getString(R.string.firebase_srProfile));
        return profile.child(SID);
    }

    public DatabaseReference getWorktimeRef(){
        return root.child(context.getResources().getString(R.string.firebase_srWorktime));
    }

    public DatabaseReference getDailyTask(String date){
        DatabaseReference dailyTask=root.child(context.getResources().getString(R.string.firebase_srDailyTask));
        return dailyTask.child(date);
    }

    public DatabaseReference getSRListRef(){
        return root.child(context.getResources().getString(R.string.firebase_SrList));
    }

    public DatabaseReference getOutletListRef(String route){
        DatabaseReference routeRef=root.child(context.getResources().getString(R.string.firebase_outletList));
        return routeRef.child(route);
    }

    public DatabaseReference getOutletListDemo(){
        return root.child(context.getResources().getString(R.string.firebase_outletList));
    }

    public DatabaseReference getRouteRef(){
        return root.child(context.getResources().getString(R.string.firebase_route));
    }

}
