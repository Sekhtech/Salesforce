package com.salesforce.nvisio.salesforce.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

public class UserDefinedActivity extends AppCompatActivity {

    private SharedPrefUtils sharedPrefUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.who_is_the_user);
        sharedPrefUtils=new SharedPrefUtils(this);

        /*if (sharedPrefUtils.getLoginStatus()){
            //go to corresponding activity
            if (sharedPrefUtils.isAdminTheUser()){
                //go to admin activity
            }
            else{
                // go to sr activity
            }
        }*/
    }

    public void SRClicked(View view) {
        sharedPrefUtils.setUser(getResources().getString(R.string.sha_value_sr));
        startActivity(new Intent(UserDefinedActivity.this,LoginActivityNew.class));
    }

    public void ManagerClicked(View view) {
        sharedPrefUtils.setUser(getResources().getString(R.string.sha_value_admin));
        startActivity(new Intent(UserDefinedActivity.this,LoginActivityNew.class));
    }
}
