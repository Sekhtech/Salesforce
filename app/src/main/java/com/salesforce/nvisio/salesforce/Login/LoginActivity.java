package com.salesforce.nvisio.salesforce.Login;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.salesforce.nvisio.salesforce.MainActivity;
import com.salesforce.nvisio.salesforce.MapActivity;
import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.Utils;
import com.salesforce.nvisio.salesforce.service.GPSService;
import com.salesforce.nvisio.salesforce.ui.CreateLocationActivity;

import com.salesforce.nvisio.salesforce.utils.PermissionUtils;

import net.danlew.android.joda.DateUtils;
import net.danlew.android.joda.JodaTimeAndroid;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by USER on 03-May-17.
 */

public class LoginActivity extends AppCompatActivity implements PermissionUtils.PermissionResultCallback,ActivityCompat.OnRequestPermissionsResultCallback {
    private EditText nameUser, phoneUser;
    private Button login, registry;
    private SharedPreferences settings;
    private CircleImageView loginImage, registryImage;
    private static final int RESULT_LOAD_IMAGE = 1;
    private String picturePath="null";
    private TextView name,different;
    private TextInputLayout textInputLayout;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Boolean canILogIn=false;

    private ProgressBar progress;
    private RelativeLayout hide;

    ArrayList<String> permissions=new ArrayList<>();
    PermissionUtils permissionUtils;
    private final static int REQUEST_CHECK_SETTINGS = 2000;
    boolean isPermissionGranted;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark) );
        }
        settings=getSharedPreferences("salesforce",MODE_PRIVATE);
        Utils.getmDatabase();

        //removable
        permissionUtils=new PermissionUtils(this);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionUtils.check_permission(permissions,"Need GPS permission for getting your location",1);
        callService();
        //removable


        //REGISTRATION START
        if (settings.getString("name","").equals("")){
            setContentView(R.layout.register_activity);

            registryImage= (CircleImageView) findViewById(R.id.accountImageRegister);
            //getting image from user
            registryImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},RESULT_LOAD_IMAGE);
                    }

                    else{
                        Intent i = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
                }
            });

            nameUser= (EditText) findViewById(R.id.nameUser);
            phoneUser= (EditText) findViewById(R.id.phoneuser);//changed to emaail
            hide= (RelativeLayout) findViewById(R.id.hide);
            progress= (ProgressBar) findViewById(R.id.progress);
            registry= (Button) findViewById(R.id.registry);
            registry.setOnClickListener(v -> {
                if (nameUser.getText().toString().equals("")||phoneUser.getText().toString().equals("")){
                        //Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, "status: "+isServiceRunning(), Toast.LENGTH_SHORT).show();
                   // Log.d("day>>","Day: "+dateTimeUtils.getDayName("28/12/2017"))
                    startActivity(new Intent(LoginActivity.this, MapActivity.class));
                    finish();
                }
                else {

                 if (picturePath.equals("")||picturePath.equals("null")){
                        SharedPreferences.Editor editor=settings.edit();
                        editor.putString("name",nameUser.getText().toString());
                        String email=phoneUser.getText().toString();
                        email=email.replace(".",",");
                        editor.putString("phone",email);
                        editor.apply();
                        Toast.makeText(LoginActivity.this, "Registration completed!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        finish();
                    }

                    else{
                     Log.d("picpath>>","path: "+picturePath);
                        Bitmap realImage = BitmapFactory.decodeFile(picturePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] b = baos.toByteArray();
                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                        SharedPreferences.Editor editor=settings.edit();
                        editor.putString("name",nameUser.getText().toString());
                        String email=phoneUser.getText().toString();
                        email=email.replace(".",",");
                        editor.putString("phone",email);
                        editor.putString("image_data",encodedImage);
                        editor.apply();
                        Toast.makeText(LoginActivity.this, "Registration completed!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        finish();


                        /*String mobileNumber="+88"+phoneUser.getText();
                        Intent intent=new Intent(LoginActivity.this,VerifyMobile.class);
                        intent.putExtra("app_id",getResources().getString(R.string.cognayls_app_id));
                        intent.putExtra("access_token",getResources().getString(R.string.access_token));
                        intent.putExtra("mobile",mobileNumber);
                        startActivityForResult(intent,VerifyMobile.REQUEST_CODE);*/
                    }

                }
            });

        }
        // REGISTRATION ENDS


        //LOGIN STARTS
        else{
            setContentView(R.layout.login_activity);
            JodaTimeAndroid.init(this);
            firebaseDatabase=FirebaseDatabase.getInstance();
            databaseReference=firebaseDatabase.getReference("credential");
            different= (TextView) findViewById(R.id.different);
            different.setPaintFlags(different.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
            different.setOnClickListener(v -> {
                SharedPreferences.Editor editor=settings.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                finish();
            });



                //FingerPrint
                // Check whether the device has a Fingerprint sensor.



                login= (Button) findViewById(R.id.login);
                login.setOnClickListener(v -> {
                    Query query=databaseReference.orderByChild("email").equalTo(settings.getString("phone",""));
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot single: dataSnapshot.getChildren()){
                                String data=String.valueOf(single);
                                Log.d("tag>>","data: "+data);
                                String[] breakSPace=data.split(" ");
                                String rplce=breakSPace[7].replaceAll("[{ ,]","");
                                Log.d("tag>>","data: "+rplce);
                                String[]fStatus=rplce.split("=");
                                String status=fStatus[1];
                                Log.d("sss>>",status);

                                if (status.equals("1")){
                                    canILogIn=true;
                                }
                            }

                            if (canILogIn){
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                finish();
                            }
                            else{
                                Log.d("sss>>", String.valueOf(canILogIn));
                                Toast.makeText(LoginActivity.this, "Sorry! Your can not log-in now", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                });
                String nameOfTheUser=settings.getString("name","");
                name.setText(nameOfTheUser);
                String image_data=settings.getString("image_data","");
                if( !image_data.equalsIgnoreCase("") ){
                    byte[] b = Base64.decode(image_data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    loginImage.setImageBitmap(bitmap); }
            }

            //Start Workday is there




    }
    private void checkIfGpsIsOn() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
        else{
            Intent intent=new Intent(LoginActivity.this, CreateLocationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.right_to_left_enter,R.anim.right_to_left_exit);
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case RESULT_LOAD_IMAGE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
                else{
                    Toast.makeText(LoginActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            registryImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
        else if (requestCode== Activity.RESULT_OK){
            callService();

        }
        else if (requestCode==Activity.RESULT_CANCELED){

        }
         /*else if (requestCode == VerifyMobile.REQUEST_CODE) {
            String message = data.getStringExtra("message");
            int result = data.getIntExtra("result", 0);

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT)
                    .show();
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
                    .show();

            switch (result){
                case 101:
                    Toast.makeText(this, "MISSING CREDENTIALS", Toast.LENGTH_SHORT).show();
                    break;
                case 102:
                    Toast.makeText(this, "MISSING REQUIRED VALUES", Toast.LENGTH_SHORT).show();
                    break;
                case 103:
                    Toast.makeText(this, "MISSING PROPER NUMBER", Toast.LENGTH_SHORT).show();
                    break;
                case 104:
                    Bitmap realImage = BitmapFactory.decodeFile(picturePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    realImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                    SharedPreferences.Editor editor=settings.edit();
                    editor.putString("name",nameUser.getText().toString());
                    editor.putString("phone",phoneUser.getText().toString());
                    editor.putString("image_data",encodedImage);
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Registration completed!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                    finish();
                    break;
                case 105:
                    Toast.makeText(this, "NUMBER IS NOT CORRECT", Toast.LENGTH_SHORT).show();
                    break;
                case 106:
                    Toast.makeText(this, "MOBILE NUMBER VERIFICATION CANCELED", Toast.LENGTH_SHORT).show();
                    break;
                case 107:
                    Toast.makeText(this, "NETWORK ERROR CANNOT BE VERIFIED", Toast.LENGTH_SHORT).show();
                    break;
                case 108:
                    Toast.makeText(this, "MOBILE NUMBER VERIFICATION FAILED, NO INTERNET", Toast.LENGTH_SHORT).show();
                    break;
                
            }

        }*/
    }

    private void callService(){
        Intent intent=new Intent(LoginActivity.this,GPSService.class);
        LoginActivity.this.startService(intent);
        //Toast.makeText(this, "count: "+ AppDataBase.getAppDatabase(this).locateSRDao().positionTableCount(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void PermissionGranted(int request_code) {
        isPermissionGranted=true;
        callService();
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {

    }

    @Override
    public void PermissionDenied(int request_code) {

    }

    @Override
    public void NeverAskAgain(int request_code) {

    }
}
