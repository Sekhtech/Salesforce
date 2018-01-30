package com.salesforce.nvisio.salesforce.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.salesforce.nvisio.salesforce.database.AppDataBase;
import com.salesforce.nvisio.salesforce.database.LocateSRData;

/**
 * Created by USER on 26-Dec-17.
 */

public class GPSService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private Context context=this;
    private com.google.android.gms.location.LocationListener locationListener;
    int i=0;
    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();

        locationListener=location -> {
            if (i==3){
                stopLocationUpdate();
                this.stopSelf();
            }
            else{
                String lat=location.getLatitude()+"";
                String lon=location.getLongitude()+"";
                i++;
                Log.d("se>>","LAT: "+lat+" LON: "+lon+" i= "+i);
            }

            /*Intent intent=new Intent(context,SendDataToServerService.class);
            String lat=location.getLatitude()+"";
            String lon=location.getLongitude()+"";
            intent.putExtra("latitude",lat);
            intent.putExtra("longitude",lon);
            intent.putExtra("srid",125);
            intent.putExtra("date","date");
            intent.putExtra("time","time");
            intent.putExtra("timestamp","timestamp");
            context.startService(intent);*/
        };
    }
    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);//5 mins=300000
        mLocationRequest.setFastestInterval(2000);//3 mins=1800000
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, locationListener);

    }

    private void stopLocationUpdate() {
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("serv>>","service onConnectionSuspended 68");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("serv>>","service onConnectionFailed 73");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("serv>>","service onLocationChanged 78");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("serv>>","service onStatusChanged 83");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("serv>>","service onProviderEnabled 88");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("serv>>","service onProviderDisabled 93");
    }


}
