package com.salesforce.nvisio.salesforce;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.salesforce.nvisio.salesforce.Model.job_model;
import com.salesforce.nvisio.salesforce.RecyclerViewAdapter.ActivityAdapter;
import net.danlew.android.joda.JodaTimeAndroid;
import org.joda.time.DateTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by USER on 09-May-17.
 */

public class AfterLogout extends AppCompatActivity {

    private TextView name, designation,manager,region,date,time,totalTime;
    private ImageView image;
    private RecyclerView recyclerView;
    private ActivityAdapter adapter;
    private SharedPreferences settings;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,TestingQuery;
    private List<job_model> JobList;
    private int timeTotal=0;
    private View views;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }
        setContentView(R.layout.after_logout);
        JodaTimeAndroid.init(this);

        //Firebase Database
        Utils.getmDatabase();
        settings=getSharedPreferences("salesforce",MODE_PRIVATE);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("activities/details");
        TestingQuery=firebaseDatabase.getReference("UserLogin/"+settings.getString("phone","")+"-"+settings.getString("name","")+"/details/"+settings.getString("databaseKey",""));
        TestingQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                for (DataSnapshot single: dataSnapshot.getChildren()){
                    job_model model=single.getValue(job_model.class);
                    JobList.add(new job_model(model.getStart(),model.getEnd(),model.getJob(),model.getSubJob(),model.getDuration(),model.getDate(),model.getDurationInMins()));
                    timeTotal=timeTotal+model.getDurationInMins();
                    //Log.d("list>>",""+model.getStart()+" "+model.getEnd()+" "+model.getJob()+" "+model.getSubJob()+" "+model.getDuration()+" "+model.getDate()+" "+model.getDurationInMins());
                }

                adapter=new ActivityAdapter(JobList,AfterLogout.this);
                recyclerView.setAdapter(adapter);
                int hour=timeTotal/60;
                int min=timeTotal%60;

                //0 hour 0 min
                if (hour==0 && min ==0){
                    totalTime.setText(min+" min");
                }

                else if (hour==0 && min>0){
                    if (min>1){
                        totalTime.setText(min+" minutes");
                    }
                    else{
                        totalTime.setText(min+" minute");
                    }
                }

                else if (hour>0 && min ==0){
                    if (hour>1){
                        totalTime.setText(hour+" hours");
                    }
                    else{
                        totalTime.setText(hour+" hour");
                    }
                }

                else{
                    if (hour>1 && min>1){
                        totalTime.setText(hour+" hours"+ min+" minutes");
                    }
                    else if(hour>1 && min<2){
                        totalTime.setText(hour+" hours"+min+" minute");
                    }
                    else if (hour<2 && min>1){
                        totalTime.setText(hour+" hour"+min+" minutes");
                    }
                    else{
                        totalTime.setText(hour+" hour"+min+" minute");
                    }
                }

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

        //reference
        name= (TextView) findViewById(R.id.Name);
        designation= (TextView) findViewById(R.id.Designation);
        manager= (TextView) findViewById(R.id.Manager);
        region= (TextView) findViewById(R.id.Region);
        date= (TextView) findViewById(R.id.Date);
        time= (TextView) findViewById(R.id.Time);
        image= (ImageView) findViewById(R.id.propic);
        totalTime= (TextView) findViewById(R.id.totalTime);
        views=findViewById(R.id.horiLine);
        recyclerView= (RecyclerView) findViewById(R.id.activityRecycler);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        JobList=new ArrayList<>();

        String imageData=settings.getString("image_data","");
        if (!imageData.equalsIgnoreCase("")){
            byte[] b= Base64.decode(imageData,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(b,0,b.length);
            image.setImageBitmap(bitmap);
        }

        name.setText(settings.getString("name",""));
        currentDate();
        time.setText(currentTime());



    }
    //getting current time in 12 hours format
    private String currentTime(){
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+6:26"));
        Date date =new Date();
        String fdate= dateFormat.format(date);
        return fdate;
    }

    //getting current date
    private String currentDate(){
        DateTime crrntdate=new DateTime();
        String dayNumber=crrntdate.dayOfMonth().getAsText();//11
        String monthName=crrntdate.monthOfYear().getAsText();//May
        int year=crrntdate.getYear();///2017

        String dateLog=monthName+" "+dayNumber+", "+year;// November 17, 2017

        return dateLog;
    }

      /*private void getAllData(DataSnapshot dataSnapshot){
        for (DataSnapshot singleSnapShot: dataSnapshot.getChildren()){
            job_model model=singleSnapShot.getValue(job_model.class);
            Task.add(new job_model(model.getStart(),model.getEnd(),model.getJob(),model.getSubJob(),model.getDuration(),model.getDate(),model.getDurationInMins()));
        }

        for (int i = 0; i <Task.size() ; i++) {
            String start=Task.get(i).getStart();
            String end=Task.get(i).getEnd();
            String date=Task.get(i).getDate();
            String job=Task.get(i).getJob();
            String sub_job=Task.get(i).getSubJob();
            String duration=Task.get(i).getDuration();
            int min=Task.get(i).getDurationInMins();
        }
    }*/

}
