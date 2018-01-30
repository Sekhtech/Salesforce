package com.salesforce.nvisio.salesforce.ui;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.salesforce.nvisio.salesforce.Model.Credential;
import com.salesforce.nvisio.salesforce.Model.OutletInformation;
import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.Utils;
import com.salesforce.nvisio.salesforce.utils.FirebaseReferenceUtils;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivityNew extends AppCompatActivity {
    @BindView(R.id.userid)EditText userid;
    @BindView(R.id.password)EditText password;
    @BindView(R.id.credentialContainer)RelativeLayout relativeLayout;
    @BindView(R.id.progress)ProgressBar progressBar;
    private FirebaseReferenceUtils firebaseReferenceUtils;
    private SharedPrefUtils sharedPrefUtils;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_new);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        Utils.getmDatabase();
        firebaseReferenceUtils=new FirebaseReferenceUtils(this);
        sharedPrefUtils=new SharedPrefUtils(this);
    }


    public void Login(View view) {
        LoadingStatus(true); //show progress bar
        if (!TextUtils.isEmpty(userid.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())){
            Query query = null;
            if (sharedPrefUtils.isAdminTheUser()){
                //user is admin
                checkCredential(firebaseReferenceUtils.ManagerCredentialRef().child(userid.getText().toString()));
            }
            else{
                //user is SR
                checkCredential(firebaseReferenceUtils.srCredentialRef().child(userid.getText().toString()));
            }
        }
        else{
            Toast.makeText(this, "Please provide all informations", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCredential(DatabaseReference databaseReference){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                            Credential credential=dataSnapshot.getValue(Credential.class);
                            if (password.getText().toString().equals(credential.getPassword())){
                                if (sharedPrefUtils.isAdminTheUser()){
                                    startActivity(new Intent(LoginActivityNew.this,ManagerMainActivity.class));
                                    finish();
                                }
                                Toast.makeText(LoginActivityNew.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(LoginActivityNew.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                            }


                    }
                    else{
                        Toast.makeText(LoginActivityNew.this, "Userid not matched", Toast.LENGTH_SHORT).show();
                    }

                    LoadingStatus(false); //hide progress bar

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    LoadingStatus(false); //hide progress bar
                }
            });
    }

    /*private void StartQuery(Query query){
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("res>>","data: "+dataSnapshot);
                if (dataSnapshot.exists()){
                    Credential credential=dataSnapshot.getValue(Credential.class);
                    Log.d("res>>","id: "+credential.getUserId()+" pass: "+credential.getPassword());
                    if (password.getText().toString().equals(credential.getPassword())){
                        Toast.makeText(LoginActivityNew.this, "Successfully Logged In", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(LoginActivityNew.this, "Password is Incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(LoginActivityNew.this, "Userid not matched", Toast.LENGTH_SHORT).show();
                }

                LoadingStatus(false); //hide progress bar
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginActivityNew.this, "databaseError: "+databaseError, Toast.LENGTH_SHORT).show();
                LoadingStatus(false); //hide progress bar
            }
        });
    }*/

    private void LoadingStatus(boolean status){
        if (status){
            progressBar.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.INVISIBLE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }

    //DEMO DATA STARTS
    private void setOutletName(){
        firebaseReferenceUtils.getRouteRef().push().setValue(new OutletInformation("Satmosjid Road"));
        firebaseReferenceUtils.getRouteRef().push().setValue(new OutletInformation("Gulshan 1 Road"));
    }
    
    private void setOutletInfoSat(){
        OutletInformation outletInformation=new OutletInformation();
        OutletInformation outletInformation2=new OutletInformation();
        for (int i = 0; i <6 ; i++) {
            switch (i){
                case 0:
                    outletInformation.setOutletName("ULAB");
                    outletInformation.setOutletLatitude(23.741039);
                    outletInformation.setOutletLongitude(90.374543);
                    outletInformation2.setOutletName("Robi Axiata Limited");
                    outletInformation2.setOutletLatitude(23.781307);
                    outletInformation2.setOutletLongitude(90.416474);
                    firebaseReferenceUtils.getOutletListDemo().child("Satmosjid Road").push().setValue(outletInformation);
                    firebaseReferenceUtils.getOutletListDemo().child("Gulshan 1").push().setValue(outletInformation2);
                    break;
                case 1:
                    outletInformation.setOutletName("Melt Down");
                    outletInformation.setOutletLatitude(23.741825);
                    outletInformation.setOutletLongitude(90.374071);
                    outletInformation2.setOutletName("United Bank");
                    outletInformation2.setOutletLatitude(23.783457);
                    outletInformation2.setOutletLongitude(90.417322);
                    firebaseReferenceUtils.getOutletListDemo().child("Satmosjid Road").push().setValue(outletInformation);
                    firebaseReferenceUtils.getOutletListDemo().child("Gulshan 1").push().setValue(outletInformation2);
                    break;
                case 2:
                    outletInformation.setOutletName("KFC");
                    outletInformation.setOutletLatitude(23.743769);
                    outletInformation.setOutletLongitude(90.373567);

                    outletInformation2.setOutletName("North End Coffee");
                    outletInformation2.setOutletLatitude(23.785892);
                    outletInformation2.setOutletLongitude(90.417204);
                    firebaseReferenceUtils.getOutletListDemo().child("Satmosjid Road").push().setValue(outletInformation);
                    firebaseReferenceUtils.getOutletListDemo().child("Gulshan 1").push().setValue(outletInformation2);
                    break;
                case 3:
                    outletInformation.setOutletName("Basmoti Kachci");
                    outletInformation.setOutletLatitude(23.739841);
                    outletInformation.setOutletLongitude(90.370735);

                    outletInformation2.setOutletName("Gadget and Gear");
                    outletInformation2.setOutletLatitude(23.788494);
                    outletInformation2.setOutletLongitude(90.416206);
                    firebaseReferenceUtils.getOutletListDemo().child("Satmosjid Road").push().setValue(outletInformation);
                    firebaseReferenceUtils.getOutletListDemo().child("Gulshan 1").push().setValue(outletInformation2);
                    break;
                case 4:
                    outletInformation.setOutletName("Guhaa");
                    outletInformation.setOutletLatitude(23.744791);
                    outletInformation.setOutletLongitude(90.372044);

                    outletInformation2.setOutletName("Baton Rouge");
                    outletInformation2.setOutletLatitude(23.792401);
                    outletInformation2.setOutletLongitude(90.415552);
                    firebaseReferenceUtils.getOutletListDemo().child("Satmosjid Road").push().setValue(outletInformation);
                    firebaseReferenceUtils.getOutletListDemo().child("Gulshan 1").push().setValue(outletInformation2);
                    break;
                case 5:
                    outletInformation.setOutletName("Sabroso");
                    outletInformation.setOutletLatitude(23.749917);
                    outletInformation.setOutletLongitude(90.368589);

                    outletInformation2.setOutletName("El Toro Mexican");
                    outletInformation2.setOutletLatitude(23.793226);
                    outletInformation2.setOutletLongitude(90.414211);
                    firebaseReferenceUtils.getOutletListDemo().child("Satmosjid Road").push().setValue(outletInformation);
                    firebaseReferenceUtils.getOutletListDemo().child("Gulshan 1").push().setValue(outletInformation2);
                    break;
            }
        }
    }

    //DEMO DATA ENDS

}
