package com.salesforce.nvisio.salesforce.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;

/**
 * Created by USER on 28-Dec-17.
 */

public class UtilityClass {
    private Context context;

    public UtilityClass(Context context) {
        this.context = context;
    }

    //Image Utils
    public String encodeImage(String picturePath){
        Bitmap realImage = BitmapFactory.decodeFile(picturePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    public Bitmap decodeImage(String srImage){
        if( !srImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(srImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }


    //DateTime Utils
    public String currentTime(){
        DateTimeZone dt=DateTimeZone.forID("Africa/Lusaka"); //GMT +2 Zambia
        DateTime dateTime=new DateTime(dt);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM--yyyy HH:mm:ss");
        return fmt.print(dateTime);
    }
    public String timeDifference(String startTime,String endTime){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        DateTime start = formatter.parseDateTime(startTime);
        DateTime end=formatter.parseDateTime(endTime);
        String timeDifference=String.valueOf(Minutes.minutesBetween(start,end).getMinutes());
        return minutesToHour(timeDifference);
    }
    private String minutesToHour(String timeDifference){
        int totalMinutes=Integer.parseInt(timeDifference);
        int hour=totalMinutes/60;
        int min=totalMinutes%60;
        return String.valueOf(hour+":"+min);
    }

    public String getDayName(String date){
        LocalDate localDate = DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(date);
        return DateTimeFormat.forPattern("EEEE").print(localDate);
    }


    //Gps Utils
    private boolean checkIfGpsIsOn() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
