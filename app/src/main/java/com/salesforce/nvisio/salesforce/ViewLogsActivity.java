package com.salesforce.nvisio.salesforce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salesforce.nvisio.salesforce.Model.login_data;
import com.salesforce.nvisio.salesforce.RecyclerViewAdapter.LogAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 18-May-17.
 */

public class ViewLogsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView viewToMain;
    private SharedPreferences settings;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<login_data> firstList,secondList;
    private LogAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_logs);
        //
        settings=getSharedPreferences("salesforce",MODE_PRIVATE);
        firstList=new ArrayList<>();
        secondList=new ArrayList<>();
        //firebase database
        Utils.getmDatabase();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("UserLogin/"+settings.getString("phone","")+"-"+settings.getString("name",""));
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                firstList.clear();
                for (DataSnapshot single: dataSnapshot.getChildren()){
                    login_data data=single.getValue(login_data.class);
                    firstList.add(new login_data(data.getDuration(),data.getLogoutTime(),data.getLoginDate(),data.getLoginTime()));
                }
                for (int i = firstList.size();i>0 ; i--) {
                    secondList.add(firstList.get(i-1));
                }
                adapter=new LogAdapter(secondList,ViewLogsActivity.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //recyclerview
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView= (RecyclerView) findViewById(R.id.viewfullLogs);
        recyclerView.setLayoutManager(linearLayoutManager);
        viewToMain= (ImageView) findViewById(R.id.viewToMain);
        viewToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewLogsActivity.this,MainActivity.class));
                finish();
            }
        });


    }
}
