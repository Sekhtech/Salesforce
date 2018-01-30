package com.salesforce.nvisio.salesforce.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.salesforce.nvisio.salesforce.Login.LoginActivity;
import com.salesforce.nvisio.salesforce.R;

import java.io.IOException;
import java.util.List;

/**
 * Created by USER on 28-Dec-17.
 */

public class CreateLocationActivity extends AppCompatActivity {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private TextView resutText;
    private OnMapReadyCallback onMapReadyCallback;
    private LatLng latLng;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_location_map);
        Toolbar toolbar=findViewById(R.id.startLocationToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar();
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        resutText =findViewById(R.id.dragg_result);
        new Thread(() -> {
            try {
                mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                runOnUiThread(() -> mapFragment.getMapAsync(onMapReadyCallback));
            }
            catch (Exception ignored){}
        }).start();

        onMapReadyCallback= this::InitMap;
    }
    public void InitMap(GoogleMap googleMap) {
        mMap = googleMap;
        MapStyleOptions styleOptions=MapStyleOptions.loadRawResourceStyle(CreateLocationActivity.this,R.raw.ubermapstyle);
        mMap.setMapStyle(styleOptions);
        latLng=new LatLng(23.740300, 90.375753);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng).draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(markerOptions);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
               Double lat=marker.getPosition().latitude;
               Double lon=marker.getPosition().longitude;
                Log.d("drag>>","latitude: "+lat+" longitude: "+lon);

                /*Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                String city = addresses.get(0).getAddressLine(1);
                Toast.makeText(MapsActivity.this, city, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }*/

            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (latLng!=null){
            Intent intent =new Intent(CreateLocationActivity.this,LoginActivity.class);
            intent.putExtra(getString(R.string.LatitudeOfStartLocation),latLng.latitude);
            intent.putExtra(getString(R.string.LongitudeOfStartLocation),latLng.longitude);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right_enter,R.anim.left_to_right_exit);
        }
        else{
            Intent intent =new Intent(CreateLocationActivity.this,LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.left_to_right_enter,R.anim.left_to_right_exit);
        }
    }
}
