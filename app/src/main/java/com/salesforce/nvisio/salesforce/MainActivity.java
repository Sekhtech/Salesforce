package com.salesforce.nvisio.salesforce;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salesforce.nvisio.salesforce.Model.login_data;
import com.salesforce.nvisio.salesforce.RecyclerViewAdapter.LogAdapter;
import com.salesforce.nvisio.salesforce.service.GPSService;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Instant;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private CircleImageView profileImage;
    private TextView proName, proDesignation, proRegion, proLineManager, proDate, proTime,viewlog,prelog,noData;
    private Button startWorkday,startActivity,endWorkday,gotoMap;
    private SharedPreferences settings;
    private RecyclerView logRecycler;
    private LogAdapter logAdapter;
    private String logout,duration;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference,forIteration,forIterationUserLogin;
    private List<login_data> dataList;
    private List<login_data> fireDataFinal;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }
        setContentView(R.layout.activity_main);

        settings=getSharedPreferences("salesforce",MODE_PRIVATE);
        Intent intentFrom=getIntent();
        int from=intentFrom.getIntExtra("from",0);
        gotoMap= (Button) findViewById(R.id.mapactivity);
        gotoMap.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this,MapActivity.class));
            finish();
        });

        //check if any activity process is running or not
        // if an activity is running, direct the user to that process
        if(!settings.getString("activityStart","").equals("")&& from==0){
            Intent intent=new Intent(MainActivity.this,ActivityTimeStamp.class);
            intent.putExtra("date",settings.getString("loginDate",""));
            intent.putExtra("time",settings.getString("loginTime",""));
            intent.putExtra("name",settings.getString("name",""));
            startActivity(intent);
            finish();
        }
        else{
            JodaTimeAndroid.init(this);
            fireDataFinal=new ArrayList<>();
            dataList=new ArrayList<>();

            Utils.getmDatabase();
            firebaseDatabase=FirebaseDatabase.getInstance();
            databaseReference=firebaseDatabase.getReference("UserLogin/"+settings.getString("phone","")+"-"+settings.getString("name","")+"/details");//for saving

            forIterationUserLogin=firebaseDatabase.getReference("UserLogin/"+settings.getString("phone","")+"-"+settings.getString("name",""));//Query
            forIterationUserLogin.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getAllData(dataSnapshot);
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


            profileImage= (CircleImageView) findViewById(R.id.profileImage);
            proName= (TextView) findViewById(R.id.proName);
            proDesignation= (TextView) findViewById(R.id.proDesignation);
            proRegion= (TextView) findViewById(R.id.proRegion);
            proLineManager= (TextView) findViewById(R.id.proLineManager);
            proDate= (TextView) findViewById(R.id.proDate);
            proTime= (TextView) findViewById(R.id.proTime);
            noData= (TextView) findViewById(R.id.noData);
            viewlog= (TextView) findViewById(R.id.viewLogs);
            viewlog.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this,ViewLogsActivity.class));
                finish();
            });
            prelog= (TextView) findViewById(R.id.preHeader);

            logRecycler= (RecyclerView) findViewById(R.id.logRecycler);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            logRecycler.setLayoutManager(linearLayoutManager);

            startWorkday= (Button) findViewById(R.id.startWorkday);
            startActivity= (Button) findViewById(R.id.startActivity);

            endWorkday= (Button) findViewById(R.id.endWorkday);
            if (!settings.getString("loginTime","").equals("")){
                startActivity.setVisibility(View.VISIBLE);
                endWorkday.setVisibility(View.VISIBLE);
                startWorkday.setVisibility(View.GONE);
            }
            //get current date
            currentDate();

            //getting profile image from shared preference and decoding it
            String image_data=settings.getString("image_data","");
            if( !image_data.equalsIgnoreCase("") ){
                byte[] b = Base64.decode(image_data, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                profileImage.setImageBitmap(bitmap); }

            proName.setText(settings.getString("name",""));
            proTime.setText(currentTime());

        }
        //test();


    }


    private void getAllData(DataSnapshot dataSnapshot){
        dataList.clear();
        for (DataSnapshot single: dataSnapshot.getChildren()){
            login_data data=single.getValue(login_data.class);
            dataList.add(new login_data(data.getDuration(),data.getLogoutTime(),data.getLoginDate(),data.getLoginTime()));
        }

        if (dataList.size()>0 && dataList.size()<15){
            viewlog.setVisibility(View.INVISIBLE);
            prelog.setText("Logs");
        }

        else if (dataList.size()>15){
            viewlog.setVisibility(View.VISIBLE);
        }
        else {
            viewlog.setVisibility(View.GONE);
            prelog.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }

            fireDataFinal.clear();
        for (int i = dataList.size();i>0 ; i--) {
            fireDataFinal.add(dataList.get(i-1));
        }
        logAdapter=new LogAdapter(fireDataFinal,MainActivity.this);
        logRecycler.setAdapter(logAdapter);
    }
    public void startWork(View v){
        getLoginTime("login");
        Toast.makeText(MainActivity.this, "Your workday has started!", Toast.LENGTH_SHORT).show();
        startActivity.setVisibility(View.VISIBLE);
        endWorkday.setVisibility(View.VISIBLE);

        YoYo.with(Techniques.SlideInLeft)
                .duration(1500)
                .playOn(startActivity);
        YoYo.with(Techniques.SlideInRight)
                .duration(1500)
                .playOn(endWorkday);

        startWorkday.setVisibility(View.GONE);
    }

    public void endWork(View v){
        Toast.makeText(MainActivity.this, "Your workday has ended!", Toast.LENGTH_SHORT).show();
        getLoginTime("logout");
    }
    public void startActiv(View v){
        Intent intent=new Intent(MainActivity.this,StartWorkday.class);
        startActivity(intent);
        finish();
    }
  //will be used to get login/logout Time
    private void getLoginTime(String type){
        //current time in 12 hour format
        DateTime currentTime=DateTime.now(DateTimeZone.forOffsetHoursMinutes(6,00));
        DateTimeFormatter timeFormatter=DateTimeFormat.forPattern("hh:mm aa");
        String actualTime12=timeFormatter.print(currentTime);

        if (type.equals("login")){
            SharedPreferences.Editor editor=settings.edit();
            editor.putString("loginTime",actualTime12);
            editor.apply();
            insertPartialData();
        }
        else{
            logout=actualTime12;
            SharedPreferences.Editor editor=settings.edit();
            editor.putString("logoutTime",actualTime12);
            editor.apply();
            insertIntoDatabase();

        }
    }

    private void insertPartialData(){
        String id=databaseReference.push().getKey()+"-"+settings.getString("loginDate","");
        login_data data=new login_data();
        data.setLoginTime(settings.getString("loginTime",""));
        data.setLoginDate(settings.getString("loginDate",""));
        databaseReference.child(id).setValue(data);

        SharedPreferences.Editor editor=settings.edit();
        editor.putString("databaseKey",id);
        editor.apply();
    }

    //when pressed "End Workday", insert data into Database and clear the SharedPreference
    private void insertIntoDatabase(){

        login_data data=new login_data();
        data.setLoginTime(settings.getString("loginTime",""));
        data.setLogoutTime(logout);
        data.setLoginDate(settings.getString("loginDate",""));
        data.setDuration(getTimeDifference());
        databaseReference.child(settings.getString("databaseKey","")).setValue(data);
        SharedPreferences.Editor editor=settings.edit();


        //REMOVING
        editor.remove("loginTime");
        editor.remove("loginDate");
        editor.remove("loginDateWithDayName");
        //activity
        editor.remove("activityStart");
        editor.remove("activityEnd");
        editor.remove("starthour");
        editor.remove("startmin");
        editor.remove("startampm");
        editor.remove("endhour");
        editor.remove("endmin");
        editor.remove("endampm");
        editor.remove("activityName");
        editor.apply();

        startActivity(new Intent(MainActivity.this, AfterLogout.class));
        finish();
    }


    //getting current time in 12 hours format
    private String currentTime(){
       DateTime dateTime=DateTime.now(DateTimeZone.forOffsetHoursMinutes(6,00));
        DateTimeFormatter formatter=DateTimeFormat.forPattern("hh:mm aa");

        return formatter.print(dateTime);
    }

    //getting current date
    private void currentDate(){

        DateTime crrntdate=new DateTime();
        String dayName=crrntdate.dayOfWeek().getAsText();//Tuesday
        String dayNumber=crrntdate.dayOfMonth().getAsText();//11
        String monthName=crrntdate.monthOfYear().getAsText();//May
        int year=crrntdate.getYear();///2017

        String dateLog=monthName+" "+dayNumber+", "+year;// November 17, 2017
        String dateLogWithName=dayName+", "+monthName+" "+dayNumber+", "+year;//Tuesday, November 17, 2017
        proDate.setText(dateLogWithName);


        if (settings.getString("loginDate","").equals("")){
            SharedPreferences.Editor editor=settings.edit();
            editor.putString("loginDate",dateLog); // November 17, 2017
            editor.putString("loginDateWithDayName",dateLogWithName);//Tuesday, November 17, 2017
            editor.apply();
        }
    }

    //getting time duration
    private String getTimeDifference(){
        String start=settings.getString("loginTime","");
        String end=settings.getString("logoutTime","");
        SimpleDateFormat format=new SimpleDateFormat("hh:mm aa");
        Date date1=null;
        Date date2=null;
        try{
            date1=format.parse(start);
            date2=format.parse(end);

            DateTime d1=new DateTime(date1);
            DateTime d2=new DateTime(date2);

            int  diffInHours=Hours.hoursBetween(d1,d2).getHours() % 24;
            int diffInMins=Minutes.minutesBetween(d1,d2).getMinutes() % 60;

            //hour=0, min=10+
            if (diffInHours==0 && diffInMins!=0){
                //min=10
                if (diffInMins>1){
                    duration=String.valueOf(diffInMins)+" minutes";
                }
                else{
                    //min=0-1
                    duration=String.valueOf(diffInMins)+" minute";
                }
            }
            //hour=1+, min=0
            else if (diffInHours!=0 && diffInMins==0){
                //hour=2
                if (diffInHours>1){
                    duration=String.valueOf(diffInHours)+" hours";
                }
                else{
                    //hour=0-1
                    duration=String.valueOf(diffInHours)+" hour";
                }
            }
            else{
                //hour=2+, min=2+
                if (diffInHours>1 && diffInMins>1){
                    duration=String.valueOf(diffInHours)+" hours "+diffInMins+" minutes";
                }
                else if (diffInHours>1 && diffInMins<2){
                    //hour=2+, min=1
                    duration=String.valueOf(diffInHours)+" hours "+diffInMins+" minute";
                }
                else if (diffInHours<2 && diffInMins>1){
                    //hour=1, min=2+
                    duration=String.valueOf(diffInHours)+" hour "+diffInMins+" minutes";
                }
                else{
                    //hour=1, min=1
                    duration=String.valueOf(diffInHours)+" hour "+diffInMins+" minute";
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return duration;
    }

}

