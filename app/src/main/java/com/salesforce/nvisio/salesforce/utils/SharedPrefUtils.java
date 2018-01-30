package com.salesforce.nvisio.salesforce.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.salesforce.nvisio.salesforce.R;

import java.util.List;

/**
 * Created by USER on 28-Dec-17.
 */

public class SharedPrefUtils {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefUtils(Context context) {
        this.context = context;
        sharedPreferences=context.getSharedPreferences(context.getResources().getString(R.string.pref_name),Context.MODE_PRIVATE);
    }

    public void setStringValues(String key,String value){
        editor=sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }

    public String getStringValue(String key){
        return sharedPreferences.getString(key,null);
    }

    public void setUser(String user){
        editor=sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.pref_whoIsTheUser),user).apply();
    }
    public boolean isAdminTheUser(){
        String user=sharedPreferences.getString(context.getResources().getString(R.string.pref_whoIsTheUser),null);
        if (user!=null){
            if (user.equals(context.getResources().getString(R.string.sha_value_admin))){
                return true;
            }
        }
        return false;
    }

    public void setLoginStatus(){
        sharedPreferences.edit().putBoolean(context.getResources().getString(R.string.pref_isLogged),true).apply();
    }

    public boolean getLoginStatus(){
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.pref_isLogged),false);
    }

    public void setBooleanValue(String key,boolean value){
        editor=sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public boolean getBooleanValue(String key){
        return sharedPreferences.getBoolean(key,false);
    }

    public void setIntValue(String key,int value){
        editor=sharedPreferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }

    public int getIntValue(String key){
        return sharedPreferences.getInt(key,0);
    }

    public void removeAll(){
        sharedPreferences.edit().clear().apply();
    }

    public void removeKey(String key){
        sharedPreferences.edit().remove(key).apply();
    }

    public void removeKeyFromList(List<String> keys){
        for (int i = 0; i <keys.size() ; i++) {
            sharedPreferences.edit().remove(keys.get(i)).apply();
        }
    }



}
