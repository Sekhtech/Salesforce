package com.salesforce.nvisio.salesforce;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.salesforce.nvisio.salesforce.Model.Distance;
import com.salesforce.nvisio.salesforce.Model.Duration;
import com.salesforce.nvisio.salesforce.Model.Element;
import com.salesforce.nvisio.salesforce.Model.Row;
import com.salesforce.nvisio.salesforce.mapDataParser.DataParser;
import com.salesforce.nvisio.salesforce.network.RetrofitClient;
import com.salesforce.nvisio.salesforce.network.RetrofitInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by USER on 29-May-17.
 */

public class MapActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener, OnMapReadyCallback {
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private final static int REQUEST_CHECK_SETTINGS_GPS=0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS=0x2;
    private Marker mCurrLocationMarker;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private Map<String,String> nameKeyLatValue;
    private Map<Integer,String> numberKeyNameValue;
    private Map<String,String> latKeyNameValue;
    ArrayList<String> listContaingLatLng;
    private String origin;
    private int counted=0;
    private float kmToMeter;
    private float previousValue= 0;
    private ArrayList<String> valueThatWillGoToMap;
    private String currentSource;

    private OnMapReadyCallback readyCallback;
    private int flag=0;

    private ImageView mapToMain;
    private RelativeLayout progress, mapContainer;
    private int shortestMin=0;
    
    //rxjava and retrofit
    private RetrofitClient client;
    private CompositeDisposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }

        setContentView(R.layout.map_activity);
        nameKeyLatValue=new HashMap<>();
        listContaingLatLng=new ArrayList<>();
        numberKeyNameValue=new HashMap<>();
        valueThatWillGoToMap=new ArrayList<>();
        latKeyNameValue=new HashMap<>();
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapToMain=findViewById(R.id.mapToMain);
        mapToMain.setOnClickListener(v -> {
            startActivity(new Intent(MapActivity.this,MainActivity.class));
            finish();
        });
        //retrofit client
        client= RetrofitInstance.createService(RetrofitClient.class);
        disposable=new CompositeDisposable();
        
        progress=findViewById(R.id.progresContainer);
        mapContainer=findViewById(R.id.mapContainer);
        readyCallback= googleMap -> {
            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            MapStyleOptions mapStyleOptions=MapStyleOptions.loadRawResourceStyle(MapActivity.this,R.raw.ubermapstyle);
            mMap.setMapStyle(mapStyleOptions);
            mMap.setTrafficEnabled(false);
            MarkerOptions markerOptions = new MarkerOptions();
            for (int i = 0; i <valueThatWillGoToMap.size() ; i++) {
                String getValue=valueThatWillGoToMap.get(i);
                String[] splitValue=getValue.split(",");
                double lat=Double.parseDouble(splitValue[0]);
                double lng=Double.parseDouble(splitValue[1]);
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                if (i==0){
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    markerOptions.title("I am here");
                }
                else{
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    markerOptions.title(latKeyNameValue.get(getValue));
                }
                mCurrLocationMarker=mMap.addMarker(markerOptions);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                if (i==0){
                    currentSource=getValue;
                }
                else{
                      String url=getUrl(currentSource,getValue);
                    new FetchUrl().execute(url);
                }
            }
        };
        setUpGClient();
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
    private void getMyLocation(){
        if(googleApiClient!=null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MapActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation =                     LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(100000);
                    locationRequest.setFastestInterval(100000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(MapActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(MapActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS_GPS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getMyLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkPermissions(){
        int permissionLocation = ContextCompat.checkSelfPermission(MapActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        }else{
            getMyLocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;
        if (mylocation != null) {
            //current location
            Double latitude=mylocation.getLatitude();
            Double longitude=mylocation.getLongitude();
            //adding current position
            origin=latitude+","+longitude;
            valueThatWillGoToMap.add(origin);
            if (flag==0){
                test();
                flag=1;
            }

        }
    }

    // SHORTEST PATH ALGORITHM
    private void test(){
        String nsu="23.815089,90.425512";
        String ulab="23.741105,90.374481";
        String du="23.734160,90.393115";
        String IUB="23.815597,90.427788";
        String EWU="23.768648,90.425583";
        String Brac="23.780207,90.407320";//23.668620, 90.425935
        String[] loca={nsu,ulab,du,IUB,EWU,Brac};
        if (counted == 0) {
            if (listContaingLatLng.size()==0){
                for (int i = 0; i <6 ; i++) {
                    listContaingLatLng.add(loca[i]);
                }
            }
        }

        long unixTime = System.currentTimeMillis() / 1000L; // adding 5 minutes=5*60*1000
        String separator="|";
        String parameter="unit=metric&mode=driving&departure_time="+unixTime+"&traffic_model=pessimistic&";
        String baseUrl="https://maps.googleapis.com/maps/api/distancematrix/json?"+parameter;
        String apiKey=getResources().getString(R.string.distance_matrix_api_key);
        counted=listContaingLatLng.size();
        StringBuilder destination=new StringBuilder();
        for (int i = 0; i <listContaingLatLng.size() ; i++) {   //listSize = 6
            if (i==listContaingLatLng.size()-1){
                destination.append(listContaingLatLng.get(i)).append("&");
            }
            else{
                destination.append(listContaingLatLng.get(i)).append(separator);
            }

        }
        String url=baseUrl+"origins="+origin+"&destinations="+destination+"key="+apiKey;
        String rem="origins="+origin+"&destinations="+destination+"key="+apiKey;
        SendUrl(url,destination.toString());
        testRetrofit(url);
    }

    private void testRetrofit(String remaining){
    disposable.add(client.getD(remaining)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe((distanceApiResponse) -> {
        List<String> distanceList=distanceApiResponse.destinationAddresses;
        List<String> origin=distanceApiResponse.originAddresses;
        List<Row> data=distanceApiResponse.rows;

        for (int i = 0; i <distanceList.size() ; i++) {
            String dis=distanceList.get(i);
            Log.d("ret>>","destination: "+dis);
        }

        for (int i = 0; i <origin.size() ; i++) {
            String origins=origin.get(i);
            Log.d("ret>>","origin: "+origins);
        }

        for (int i = 0; i <data.size() ; i++) {
            List<Element> distanceData=data.get(i).elements;
            for (int j = 0; j <distanceData.size() ; j++) {
                String distance=distanceData.get(j).distance.text;
                String duration=distanceData.get(j).duration.text;
                Log.d("ret>>","distance: "+distance+" duration: "+duration);

            }
        }
    }));
    }


    private void SendUrl(String url, String destination) {
        Log.d("furl>>","sendUrl:"+url );
        Log.d("bug>>", "url: "+url+" destination: "+destination);
        if (!destination.equals("")){
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, jsonObject -> {
                try {
                    JSONArray destiText = jsonObject.getJSONArray("destination_addresses");
                    for (int i = 0; i < destiText.length(); i++) {
                        nameKeyLatValue.put(destiText.getString(i), listContaingLatLng.get(i));// key=nsu, value=23.815089,90.425512
                        numberKeyNameValue.put(i, destiText.getString(i));// key= 0, value =nsu
                        latKeyNameValue.put(listContaingLatLng.get(i),destiText.getString(i));
                    }

                    JSONArray rows = jsonObject.getJSONArray("rows");

                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject elementObject = rows.getJSONObject(i);
                        JSONArray elementArray = elementObject.getJSONArray("elements");
                        int loopCount = 0;
                        for (int j = 0; j < elementArray.length(); j++) {
                            JSONObject obj = elementArray.getJSONObject(j);
                            JSONObject dis = obj.getJSONObject("distance");
                            String text = dis.getString("text");
                            JSONObject duration=obj.getJSONObject("duration");
                            String durInText=duration.getString("text");
                            Log.d("duration>>",durInText+" distance: "+text);

                            //based on time
                            int totalMins;
                            if (durInText.contains("hour")){
                                String[] brkDurInText=durInText.split(" ");// 1 hour 25 min
                                int hour=Integer.parseInt(brkDurInText[0]);
                                int hourToMin=hour*60;
                                totalMins=Integer.parseInt(brkDurInText[2])+hourToMin;
                                Log.d("ttl>>", "actual: "+durInText+" after: "+totalMins);
                            }

                            else{
                                String[] brkDurInText=durInText.split(" "); //55 mins
                                totalMins=Integer.parseInt(brkDurInText[0]);
                            }
                            if (shortestMin==0){
                                shortestMin=totalMins;
                            }
                            else if (shortestMin>totalMins){
                                shortestMin=totalMins;
                                loopCount=j;
                            }



                            Log.d("short>>","Mins: "+shortestMin);

                          /*  //based on distance
                            if (text.contains("km")) {
                                String[] spltkm = text.split(" ");
                                kmToMeter = Float.parseFloat(spltkm[0]);
                                kmToMeter = kmToMeter * 1000;
                            } else {
                                String[] spltm = text.split(" ");
                                kmToMeter = Float.parseFloat(spltm[0]);
                            }
                            if (previousValue == 0) {
                                previousValue = kmToMeter;// say 2.3
                            }
                            //this is the desired CONDITION
                            else if (previousValue > kmToMeter) { //P=2.3 M=1.0
                                previousValue = kmToMeter;
                                loopCount = j;
                            } else {
                            }*/
                        }
                        shortestMin=0;
                        String latLng=nameKeyLatValue.get(numberKeyNameValue.get(loopCount));
                        origin=latLng;
                        listContaingLatLng.remove(latLng);
                        valueThatWillGoToMap.add(latLng);
                        Log.d("shrt>>","START");
                        for (int j = 0; j <valueThatWillGoToMap.size() ; j++) {
                            Log.d("shrt>>","i= "+j+": "+valueThatWillGoToMap.get(j));
                        }


                      /*  previousValue = 0;
                        String name = numberKeyNameValue.get(loopCount);
                        String LatLng = nameKeyLatValue.get(name);
                        origin = LatLng;
                        listContaingLatLng.remove(LatLng);
                        valueThatWillGoToMap.add(LatLng);

                        Log.d("bug>>","listContaingLatLng STARTS ");
                        for (int j = 0; j < listContaingLatLng.size(); j++) {
                            Log.d("bug>>","listContaingLatLng: "+j+" == "+listContaingLatLng.get(j));
                        }
                        Log.d("bug>>","listContaingLatLng ENDS ");*/
                    }
                    //Connect to the map from here
                    if (listContaingLatLng.size() == 0) {
                        mapFragment.getMapAsync(readyCallback);
                    } else {
                        counted = 1;
                        test();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, volleyError -> {

            });
            Volley.newRequestQueue(MapActivity.this).add(objectRequest);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
    //ENDS


    //DIRECTION API START
    private String getUrl(String source, String destination) {
        // Origin of route
        String str_origin = "origin=" + source;

        // Destination of route
        String str_dest = "destination=" + destination;

        currentSource=destination;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        long unixTime = System.currentTimeMillis() / 1000L;
        String parameters = str_origin + "&" + str_dest + "&" + sensor+"&traffic_model=optimistic"+"&departure_time="+unixTime+"&";

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+"key="+getResources().getString(R.string.distance_matrix_api_key);
        Log.d("furl>>",url);


        return url;
    }


    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    //ENDS

    //PARSING STARTS
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
            progress.setVisibility(View.GONE);
            mapContainer.setVisibility(View.VISIBLE);
        }
    }
    //ENDS


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MapActivity.this,MainActivity.class));
        finish();
    }
}
