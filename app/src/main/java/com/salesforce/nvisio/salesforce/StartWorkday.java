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
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.salesforce.nvisio.salesforce.Model.login_data;
import com.salesforce.nvisio.salesforce.Sectioned.ItemClickListener;
import com.salesforce.nvisio.salesforce.Sectioned.Section;
import com.salesforce.nvisio.salesforce.Sectioned.SectionedExpandableLayoutHelper;
import com.salesforce.nvisio.salesforce.Sectioned.section_item;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by USER on 07-May-17.
 */

public class StartWorkday extends AppCompatActivity implements ItemClickListener {

    private TextView name,designation,region,manager,date,time;
    private SharedPreferences settings;
    private ImageView propic,startToMain;
    private Button facing, widoutFacing, nonSales,travelling,other,stop,ongoing;
    private String dateStart,nameStart,imageData,timeIn12start, timeIn24start, timeIn24End, timeIn12End,timein24;
    private StringBuilder dateBuilder, timeBuilder;
    private long diffSeconds,diffMinutes,diffInHours;
    private ArrayList<String> buttonHeader;
    private RecyclerView activityRecycler;
    private SectionedExpandableLayoutHelper sectionedExpandableLayoutHelper;
    private String[] list={"F2F Customer Visit - Job Site", "F2F Customer Visit - Prospect","Complaint Managment","Phone Calls","Informal Relationships"};
    private DatabaseReference LoginInfoDatabase;
    private FirebaseDatabase firebase;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }
        setContentView(R.layout.start_workday);
        JodaTimeAndroid.init(this);
       Utils.getmDatabase();
        firebase=FirebaseDatabase.getInstance();
        // get Reference
        settings=getSharedPreferences("salesforce",MODE_PRIVATE);
        LoginInfoDatabase= firebase.getReference("UserLogin/"+settings.getString("phone","")+"-"+settings.getString("name","")+"/details");
        name= (TextView) findViewById(R.id.proNameStart);
        designation= (TextView) findViewById(R.id.proDesignationStart);
        region= (TextView) findViewById(R.id.proRegionStart);
        manager= (TextView) findViewById(R.id.proLineManagerStart);
        date= (TextView) findViewById(R.id.StartDate);
        time= (TextView) findViewById(R.id.proTimeStart);
        propic= (ImageView) findViewById(R.id.profileImageStart);
        startToMain= (ImageView) findViewById(R.id.startToMain);
        startToMain.setOnClickListener(v -> {
            Intent intent=new Intent(StartWorkday.this,MainActivity.class);
            intent.putExtra("from",1);
            startActivity(intent);
            finish();
        });

        ongoing= (Button) findViewById(R.id.running);
        ongoing.setOnClickListener(v -> {
            if (settings.getString("activityStart","").equals("")){
                Toast.makeText(StartWorkday.this, "You have no ongoing job running now!", Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent=new Intent(StartWorkday.this,ActivityTimeStamp.class);
                intent.putExtra("date",date.getText().toString());
                intent.putExtra("time",time.getText().toString());
                intent.putExtra("name",name.getText().toString());
                startActivity(intent);
                finish();
            }
        });

        //Toggle Mechanism
        activityRecycler= (RecyclerView) findViewById(R.id.activityRecycler);
        sectionedExpandableLayoutHelper=new SectionedExpandableLayoutHelper(this,activityRecycler,this,1);

        //get SharedPreference Values
        nameStart=settings.getString("name","");
        imageData=settings.getString("image_data","");
        timeIn12start=settings.getString("loginTime","");
        dateStart=settings.getString("loginDateWithDayName","");

        //decoding image
        if( !imageData.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(imageData, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            propic.setImageBitmap(bitmap); }

        //set values to TextView
        name.setText(nameStart);

        //getting diffrence
        String diff=String.valueOf(timeDifferenceInHours());

        //StringBuilder
        dateBuilder=new StringBuilder();
        timeBuilder=new StringBuilder();
        //date
        dateBuilder.append("Logging ").append(dateStart).toString();
        date.setText(dateBuilder);

        //time
        timeBuilder.append("Started from ").append(timeIn12start).append(", ").append(timeDifferenceInHours()).append(" hours logged").toString();
        time.setText(timeBuilder);

        //sectionedRecyclerView
        //header data entry
        buttonHeader=new ArrayList<>();
        buttonHeader.add("Project");
        buttonHeader.add("Research");
        buttonHeader.add("Meeting");
        buttonHeader.add("Lunch");
        buttonHeader.add("Other");

        setHeader();


        stop= (Button) findViewById(R.id.stop);
        stop.setOnClickListener(v -> {
            login_data data=new login_data();
            //data.setLoginDate(settings.getString("loginTime",""));
            data.setLogoutTime(getCurrentTime());
            //data.setLoginDate(settings.getString("loginDate",""));
            data.setDuration(totalSpentTime());
            //LoginInfoDatabase.child(settings.getString("databaseKey","")).setValue(data);
            Map<String,Object> task=new HashMap<>();
            task.put("logoutTime",getCurrentTime());
            task.put("duration",totalSpentTime());
            LoginInfoDatabase.child(settings.getString("databaseKey","")).updateChildren(task);
            Toast.makeText(StartWorkday.this, "Data Added", Toast.LENGTH_SHORT).show();

            SharedPreferences.Editor editor=settings.edit();
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
            startActivity(new Intent(StartWorkday.this, AfterLogout.class));
            finish();
        });
    }

    @Override
    public void itemClicked(section_item item) {
        //no job is running
        if (settings.getString("activityStart","").equals("")){
            if (item.getName().equals("Please specify")){
                final Dialog dialog=new Dialog(StartWorkday.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                EditText custom= (EditText) dialog.findViewById(R.id.custom_edit);
                dialog.show();
                Button cancel= (Button) dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(v -> dialog.dismiss());

                Button submit= (Button) dialog.findViewById(R.id.startJob);
                submit.setOnClickListener(v -> {
                    Intent intent=new Intent(StartWorkday.this,ActivityTimeStamp.class);
                    SharedPreferences.Editor editor=settings.edit();
                    editor.putString("activityName",custom.getText().toString());
                    editor.apply();
                    intent.putExtra("date",date.getText().toString());
                    intent.putExtra("time",time.getText().toString());
                    intent.putExtra("name",name.getText().toString());
                    startActivity(intent);
                    finish();
                });
            }
            else{
                Intent intent=new Intent(StartWorkday.this,ActivityTimeStamp.class);
                SharedPreferences.Editor editor=settings.edit();
                editor.putString("activityName",item.getName());
                editor.apply();
                intent.putExtra("date",date.getText().toString());
                intent.putExtra("time",time.getText().toString());
                intent.putExtra("name",name.getText().toString());
                startActivity(intent);
                finish();
            }

        }
        else {
            Toast.makeText(this, "You already have a sub-job running!", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(StartWorkday.this,ActivityTimeStamp.class);
            intent.putExtra("date",date.getText().toString());
            intent.putExtra("time",time.getText().toString());
            intent.putExtra("name",name.getText().toString());
            startActivity(intent);
            finish();
        }


    }

    @Override
    public void itemClicked(Section section) {
        if (settings.getString("job","").equals("")){
            SharedPreferences.Editor editor=settings.edit();
            editor.putString("job",section.getName());
            editor.apply();
        }

    }




    //This function helps to show the amount of hours the user has logged in
    private long timeDifferenceInHours(){

        SimpleDateFormat format=new SimpleDateFormat("hh:mm aa");
        Date date1=null;
        Date date2=null;

        try{
            date1=format.parse(settings.getString("loginTime",""));
            date2=format.parse(getCurrentTime());

            DateTime d1=new DateTime(date1);
            DateTime d2=new DateTime(date2);

            diffInHours= Hours.hoursBetween(d1,d2).getHours()%24;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return diffInHours;
    }

    //value found from this function will be inserted into DATABASE
    private String totalSpentTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date start = null;
        Date end = null;
        try{
            start=format.parse(settings.getString("loginTime",""));
            end=format.parse(getCurrentTime());

            DateTime d1=new DateTime(start);
            DateTime d2=new DateTime(end);

            diffInHours= Hours.hoursBetween(d1,d2).getHours()%24;
            diffMinutes= Minutes.minutesBetween(d1,d2).getMinutes()%60;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  String.valueOf(diffInHours)+" hours "+diffMinutes+" minutes";
    }

    //getting start time (login time) in 24 hours format. It will be useful for finding the difference between starting and ending time
    private String getCurrentTime(){

        //24 hours format starts
        DateTime dateTime=DateTime.now(DateTimeZone.forOffsetHoursMinutes(6,00));
        DateTimeFormatter formatter= DateTimeFormat.forPattern("hh:mm aa");
        timein24=formatter.print(dateTime);
        return timein24;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(StartWorkday.this,MainActivity.class));
        finish();
    }
    //setting header to recyclerView
    private void setHeader(){
        ArrayList<section_item>Item;
        for (int i = 0; i <buttonHeader.size() ; i++) {
            Item=new ArrayList<>();
            switch (i){
                case 0:
                    /*for (int j = 0; j <5 ; j++) {
                       Item.add(new section_item(list[j],j));
                        }
                        sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);*/
                    Item.add(new section_item("Please specify",0));
                    sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);

                    break;
                case 1:
                    /*for (int j = 0; j <5 ; j++) {
                        Item.add(new section_item(list[j],j));
                    }
                        sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);*/

                    Item.add(new section_item("Please specify",0));
                    sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);

                    break;
                case 2:
                    /*for (int j = 0; j <5 ; j++) {
                        Item.add(new section_item(list[j],j));
                    }
                        sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);*/
                    Item.add(new section_item("Please specify",0));
                    sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);

                    break;
                case 3:
                   /* for (int j = 0; j <5 ; j++) {
                        Item.add(new section_item(list[j],j));
                    }
                        sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);*/
                    Item.add(new section_item("Please specify",0));
                    sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);

                    break;
                case 4:
                    /*for (int j = 0; j <5 ; j++) {
                        Item.add(new section_item("Please specify",0));
                    }*/
                    Item.add(new section_item("Please specify",0));
                        sectionedExpandableLayoutHelper.addSection(buttonHeader.get(i),Item);

                    break;
            }
        }
        sectionedExpandableLayoutHelper.notifyDataSetChanged();
    }

}
