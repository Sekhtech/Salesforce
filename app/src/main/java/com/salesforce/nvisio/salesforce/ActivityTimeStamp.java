package com.salesforce.nvisio.salesforce;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salesforce.nvisio.salesforce.Model.job_model;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by USER on 08-May-17.
 */

public class ActivityTimeStamp extends AppCompatActivity {

    private TextView name,manager,region, designation, date, time, activityName, startHour, startMin, finishHour, finishMin, timeSpent,startam, finishpm,clockHeading,cancel,set;
    private Button startFinish,manual;
    private ImageView profileImage,activityToStart;
    private SharedPreferences settings;
    private long diffInHours, diffInMin;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private int totalInMinutes=0;
    private List<job_model> Task;
    private EditText hourEdit, minEdit, ampmEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }
        setContentView(R.layout.activity_time_stamp);
        JodaTimeAndroid.init(this);
        //Firebase Database
        Utils.getmDatabase();
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        firebaseDatabase=FirebaseDatabase.getInstance();
        settings=getSharedPreferences("salesforce",MODE_PRIVATE);
        Log.d("null>>","Phone: "+settings.getString("phone",""));
        Log.d("null>>","Phone: "+settings.getString("name",""));
        Log.d("null>>","Phone: "+settings.getString("databaseKey",""));
        databaseReference=firebaseDatabase.getReference("UserLogin/"+settings.getString("phone","")+"-"+settings.getString("name","")+"/details/"+settings.getString("databaseKey","")+"/Activities");
        //taking reference
        name= (TextView) findViewById(R.id.proNameStamp);
        manager= (TextView) findViewById(R.id.proLineManagerStamp);
        region= (TextView) findViewById(R.id.proRegionStamp);
        designation= (TextView) findViewById(R.id.proDesignationStamp);
        date= (TextView) findViewById(R.id.StartDateStamp);
        time= (TextView) findViewById(R.id.proTimeStamp);
        activityName= (TextView) findViewById(R.id.activityName);
        startHour= (TextView) findViewById(R.id.startHour);
        startMin= (TextView) findViewById(R.id.startMin);
        finishHour= (TextView) findViewById(R.id.finishHour);
        finishMin= (TextView) findViewById(R.id.finishMin);
        timeSpent= (TextView) findViewById(R.id.timeSpent);
        startam= (TextView) findViewById(R.id.am);
        finishpm= (TextView) findViewById(R.id.pm);
        profileImage= (ImageView) findViewById(R.id.profileImageStamp);
        activityToStart= (ImageView) findViewById(R.id.activityToStart);
        activityToStart.setOnClickListener(v -> {
            Intent intent=new Intent(ActivityTimeStamp.this,StartWorkday.class);
            startActivity(intent);
            finish();
        });
        startFinish= (Button) findViewById(R.id.startFinish);
        startFinish.setOnClickListener(v -> {
            String type=startFinish.getText().toString();
            switch (type){
                case "Start":
                    start();
                    break;
                case "Finish":
                    finishPressed();
                    break;
                default:
                    save();
                    break;
            }
        });
        manual= (Button) findViewById(R.id.manual);
        manual.setOnClickListener(v -> {
            if (settings.getString("activityStart","").equals("")){
                //means starting time is not given
                final Dialog dialog=new Dialog(ActivityTimeStamp.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.manual_custom_dialog);
                hourEdit= (EditText) dialog.findViewById(R.id.hourEdit);
                minEdit= (EditText) dialog.findViewById(R.id.minEdit);
                ampmEdit= (EditText) dialog.findViewById(R.id.ampmEdit);
                clockHeading= (TextView) dialog.findViewById(R.id.clockHeading);
                clockHeading.setText("Start Time");
                dialog.show();
                cancel= (TextView) dialog.findViewById(R.id.cancelManual);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                set= (TextView) dialog.findViewById(R.id.set);
                set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!hourEdit.getText().toString().equals("")||hourEdit.getText().toString().equals("null")||minEdit.getText().toString().equals("")||minEdit.getText().toString().equals("null")||ampmEdit.getText().toString().equals("")||ampmEdit.getText().toString().equals("null"))
                            YoYo.with(Techniques.ZoomInDown)
                                    .duration(1500)
                                    .playOn(startHour);
                        YoYo.with(Techniques.ZoomInDown)
                                .duration(1500)
                                .playOn(startMin);
                        YoYo.with(Techniques.ZoomInDown)
                                .duration(1500)
                                .playOn(startam);
                        startFinish.setText("Finish");
                        startHour.setText(hourEdit.getText().toString());
                        startMin.setText(minEdit.getText().toString());
                        startam.setText(ampmEdit.getText().toString());
                        String TimeLength=hourEdit.getText().toString();

                        if (TimeLength.length()==1){
                            TimeLength="0"+TimeLength;
                            if (!ampmEdit.getText().toString().equals("AM")){
                                int timeAfterAdding=Integer.parseInt(TimeLength);
                                if (timeAfterAdding!=12){
                                    timeAfterAdding=timeAfterAdding+12;
                                }
                                TimeLength=timeAfterAdding+":"+minEdit.getText().toString();
                            }
                            else{
                                TimeLength=TimeLength+":"+minEdit.getText().toString();
                            }

                        }

                        else{
                            if (!ampmEdit.getText().toString().equals("AM")){
                                int timeAfterAdding=Integer.parseInt(TimeLength);
                                if (timeAfterAdding!=12){
                                    timeAfterAdding=timeAfterAdding+12;
                                }
                                TimeLength=timeAfterAdding+":"+minEdit.getText().toString();

                            }
                            else{
                                TimeLength=TimeLength+":"+minEdit.getText().toString();
                            }
                        }
                        SharedPreferences.Editor editor=settings.edit();
                        editor.putString("activityStart", TimeLength);
                        editor.putString("starthour",hourEdit.getText().toString());
                        editor.putString("startmin",minEdit.getText().toString());
                        editor.putString("startampm",ampmEdit.getText().toString());
                        editor.apply();
                        dialog.dismiss();

                    }
                });
            }
            else{
                final Dialog dialog=new Dialog(ActivityTimeStamp.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.manual_custom_dialog);
                hourEdit= (EditText) dialog.findViewById(R.id.hourEdit);
                minEdit= (EditText) dialog.findViewById(R.id.minEdit);
                ampmEdit= (EditText) dialog.findViewById(R.id.ampmEdit);
                clockHeading= (TextView) dialog.findViewById(R.id.clockHeading);
                clockHeading.setText("Finish Time");
                dialog.show();
                cancel= (TextView) dialog.findViewById(R.id.cancelManual);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                set= (TextView) dialog.findViewById(R.id.set);
                set.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!hourEdit.getText().toString().equals("")||hourEdit.getText().toString().equals("null")||minEdit.getText().toString().equals("")||minEdit.getText().toString().equals("null")||ampmEdit.getText().toString().equals("")||ampmEdit.getText().toString().equals("null"))
                            YoYo.with(Techniques.ZoomInDown)
                                    .duration(1500)
                                    .playOn(finishHour);
                        YoYo.with(Techniques.ZoomInDown)
                                .duration(1500)
                                .playOn(finishMin);
                        YoYo.with(Techniques.ZoomInDown)
                                .duration(1500)
                                .playOn(finishpm);
                        startFinish.setText("Save and Return");
                        finishHour.setText(hourEdit.getText().toString());
                        finishMin.setText(minEdit.getText().toString());
                        finishpm.setText(ampmEdit.getText().toString());
                        String TimeLength=hourEdit.getText().toString();
                        if (TimeLength.length()==1){
                            TimeLength="0"+TimeLength;
                            if (!ampmEdit.getText().toString().equals("AM")){
                                int timeAfterAdding=Integer.parseInt(TimeLength);
                                timeAfterAdding=timeAfterAdding+12;
                                TimeLength=timeAfterAdding+":"+minEdit.getText().toString();
                            }
                            else{
                                TimeLength=TimeLength+":"+minEdit.getText().toString();
                            }

                        }

                        else{
                            if (!ampmEdit.getText().toString().equals("AM")){
                                int timeAfterAdding=Integer.parseInt(TimeLength);
                                timeAfterAdding=timeAfterAdding+12;
                                TimeLength=timeAfterAdding+":"+minEdit.getText().toString();

                            }
                            else{
                                TimeLength=TimeLength+":"+minEdit.getText().toString();
                            }
                        }
                        SharedPreferences.Editor editor=settings.edit();
                        editor.putString("activityEnd",TimeLength);
                        editor.putString("endhour",hourEdit.getText().toString());
                        editor.putString("endmin",minEdit.getText().toString());
                        editor.putString("endampm",ampmEdit.getText().toString());
                        editor.apply();
                        totalSpentTime();
                        dialog.dismiss();

                    }
                });
            }
        });

        //initialising
        settings=getSharedPreferences("salesforce",MODE_PRIVATE);
        Intent intent=getIntent();

        //getting image from sharedPreference
        String image_date=settings.getString("image_data","");
        if (!image_date.equalsIgnoreCase("")){
            byte[] bytes=Base64.decode(image_date,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            profileImage.setImageBitmap(bitmap);
        }

        //getting value from intent
        String nameShared=intent.getStringExtra("name");
        String loggingDate=intent.getStringExtra("date");
        String loggedTime=intent.getStringExtra("time");


        //setting the values to views
        name.setText(nameShared);
        date.setText(loggingDate);
        time.setText(loggedTime);
        activityName.setText(settings.getString("activityName",""));

        if (!settings.getString("activityStart","").equals("")){
            startFinish.setText("Finish");
            startHour.setText(settings.getString("starthour",""));
            startMin.setText(settings.getString("startmin",""));
            startam.setText(settings.getString("startampm",""));
        }
        if (!settings.getString("activityEnd","").equals("")){
            startFinish.setText("Save and Return");
            finishHour.setText(settings.getString("endhour",""));
            finishMin.setText(settings.getString("endmin",""));
            finishpm.setText(settings.getString("endampm",""));
            totalSpentTime();
        }
    }



    //function to be called when user presses "START"
    private void start(){
        startFinish.setText("Finish");
        YoYo.with(Techniques.FlipInX)
                .duration(2000)
                .playOn(startFinish);
        currentStartTimeIn24Format();
    }
    private void finishPressed(){
        startFinish.setText("Save and Return");
        YoYo.with(Techniques.FlipInX)
                .duration(2000)
                .playOn(startFinish);
        currentStartTimeIn24Format();
    }

    private void save(){
        //Firebase Database Integration
        job_model model=new job_model();
        model.setStart(settings.getString("activityStart",""));
        model.setEnd(settings.getString("activityEnd",""));
        model.setJob(settings.getString("job",""));
        model.setSubJob(activityName.getText().toString());
        model.setDuration(totalSpentTime());
        model.setDate(settings.getString("loginDate",""));
        model.setDurationInMins(totalInMinutes);
        String id=databaseReference.push().getKey();
        databaseReference.child(id+"-"+settings.getString("job","")).setValue(model);
/*
        String id=databaseReference.push().getKey();
        databaseReference.child(id).setValue(data);*/

        startFinish.setText("Start");
        startHour.setText("00");
        startMin.setText("00");
        finishHour.setText("00");
        finishMin.setText("00");
        startam.setText("");
        finishpm.setText("");
        timeSpent.setText("");
        Toast.makeText(this, "saved", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor=settings.edit();

        editor.remove("job");
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
        startActivity(new Intent(ActivityTimeStamp.this,StartWorkday.class));
        finish();
    }

    //function to get current time in both 24 and 12 Hours format
    private void currentStartTimeIn24Format(){
        //24 hours format starts
       // DateFormat dateFormatin24=new SimpleDateFormat("HH:mm:ss");

        DateTime dateTime=DateTime.now(DateTimeZone.forOffsetHoursMinutes(6,00));
        DateTimeFormatter formatter= DateTimeFormat.forPattern("HH:mm");

        DateTime dateTime1=DateTime.now(DateTimeZone.forOffsetHoursMinutes(6,00));
        DateTimeFormatter formatter1=DateTimeFormat.forPattern("hh:mm:aa");
        String[] breakdatetime1=formatter1.print(dateTime1).split(":");
        String hour=breakdatetime1[0];
        String min=breakdatetime1[1];
        String ampm=breakdatetime1[2];


       //putting the values into sharedPreference

        if (settings.getString("activityStart","").equals("")){
            //START TIME
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(startHour);
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(startMin);
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(startam);
            startHour.setText(hour);
            startMin.setText(min);
            startam.setText(ampm);
            SharedPreferences.Editor editor=settings.edit();
            editor.putString("activityStart",formatter.print(dateTime));
            editor.putString("starthour",hour);
            editor.putString("startmin",min);
            editor.putString("startampm",ampm);
            editor.apply();
        }
        else
        {
            //FINISH TIME
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(finishHour);
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(finishMin);
            YoYo.with(Techniques.ZoomInDown)
                    .duration(1500)
                    .playOn(finishpm);
            finishHour.setText(hour);
            finishMin.setText(min);
            finishpm.setText(ampm);
            SharedPreferences.Editor editor=settings.edit();
            editor.putString("activityEnd",formatter.print(dateTime));
            editor.putString("endhour",hour);
            editor.putString("endmin",min);
            editor.putString("endampm",ampm);
            editor.apply();
            totalSpentTime();
        }

    }

    //function to get the difference between two time stamps

    private String totalSpentTime(){
        //SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date start = null;
        Date end = null;
        try{
            start=format.parse(settings.getString("activityStart",""));
            end=format.parse(settings.getString("activityEnd",""));

            Log.d("ok>>","start: "+start+" end: "+end);

            DateTime d1=new DateTime(start);
            DateTime d2=new DateTime(end);

            diffInHours= Hours.hoursBetween(d1,d2).getHours()%24;
            //long diffSeconds = diffrence / 1000 % 60;
            diffInMin = Minutes.minutesBetween(d1,d2).getMinutes()%60;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        //check if if hours or min has a zero value or not
        if (diffInHours==0||diffInMin==0){
            if (diffInHours==0){
                //Only minutes, No hour
                YoYo.with(Techniques.SlideInLeft)
                        .duration(1500)
                        .playOn(timeSpent);
                timeSpent.setText("Time Spent: "+diffInMin+" Minutes");
                totalInMinutes= (int) diffInMin;
                return String.valueOf(diffInMin+" min");
            }
            else{
                //Only Hour, no Minutes
                if (diffInHours>1){
                    //when hour has a value that is greater than 1
                    YoYo.with(Techniques.SlideInLeft)
                            .duration(1500)
                            .playOn(timeSpent);
                    timeSpent.setText("Time Spent: "+diffInHours+" Hours");
                    totalInMinutes= (int) (diffInHours*60);
                    return String.valueOf(diffInHours+" hours");
                }
                else{
                    //when hour is 1
                    YoYo.with(Techniques.SlideInLeft)
                            .duration(1500)
                            .playOn(timeSpent);
                    timeSpent.setText("Time Spent: "+diffInHours+" Hour");
                    totalInMinutes= (int) (diffInHours*60);
                    return String.valueOf(diffInHours+" hour");
                }
            }
        }

        totalInMinutes= (int) (diffInHours*60);
        totalInMinutes= (int) (totalInMinutes+diffInMin);
        YoYo.with(Techniques.SlideInLeft)
                .duration(1500)
                .playOn(timeSpent);
        timeSpent.setText(String.valueOf(diffInHours)+" hours "+diffInMin+" minutes");
        return  String.valueOf(diffInHours)+" hours "+diffInMin+" minutes";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ActivityTimeStamp.this,MainActivity.class));
        finish();
    }
}
