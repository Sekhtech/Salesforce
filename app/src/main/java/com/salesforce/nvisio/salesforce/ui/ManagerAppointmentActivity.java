package com.salesforce.nvisio.salesforce.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.salesforce.nvisio.salesforce.Model.OutletInformation;
import com.salesforce.nvisio.salesforce.Model.Route;
import com.salesforce.nvisio.salesforce.Model.SalesRepInfo;
import com.salesforce.nvisio.salesforce.R;
import com.salesforce.nvisio.salesforce.utils.FirebaseReferenceUtils;
import com.salesforce.nvisio.salesforce.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagerAppointmentActivity extends AppCompatActivity {
    @BindView(R.id.progressInRela)RelativeLayout progressBar;
    @BindView(R.id.containerApp)RelativeLayout container;
    @BindView(R.id.srSpinner)Spinner spinner;
    @BindView(R.id.routeSpinner)Spinner routeSpinner;
    @BindView(R.id.mainRelative)RelativeLayout rootRelative;

    private FirebaseReferenceUtils firebaseReferenceUtils;
    private SharedPrefUtils sharedPrefUtils;
    private List<String> SalesRepList;
    private List<String> outletList;
    private Map<String,String> SalesRepMapList;
    private Map<String,OutletInformation>outletMapList;
    private List<String> SelectedOutletList;
    private String SelectedUserId=null;
    private List<String> routeList;
    private String selectedRoute=null;
    private AdapterView.OnItemSelectedListener SrListItemListener;
    private AdapterView.OnItemSelectedListener RouteListItemListener;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_layout);
        ButterKnife.bind(this);
        init();
        getSalesRepList();
        SrListItemListener=new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item=parent.getItemAtPosition(position).toString();
                if (SelectedUserId!=null){
                    if (SelectedUserId.equals(SalesRepMapList.get(item))){
                        //same Sales rep is selected
                    }
                    else{
                        //difference Sales Rep
                        //remove all the appointments
                    }
                }
                else{
                    SelectedUserId=SalesRepMapList.get(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        RouteListItemListener=new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRoute=parent.getItemAtPosition(position).toString();
                getOutletList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        spinner.setOnItemSelectedListener(SrListItemListener);
        routeSpinner.setOnItemSelectedListener(RouteListItemListener);
    }

    private void init(){
        firebaseReferenceUtils=new FirebaseReferenceUtils(this);
        sharedPrefUtils=new SharedPrefUtils(this);
        SalesRepList=new ArrayList<>();
        outletList=new ArrayList<>();
        SalesRepMapList=new HashMap<>();
        outletMapList=new HashMap<>();
        SelectedOutletList=new ArrayList<>();
        routeList=new ArrayList<>();
        setLoading(true);
    }

    public void AddAppointment(View view) {
        if (SelectedUserId==null){
            Toast.makeText(this, "Please selec a sales representative first", Toast.LENGTH_SHORT).show();
        }
        else{
            setAlertDialogForToAddAppointment();
        }
    }



    private void getSalesRepList(){
        firebaseReferenceUtils.getSRListRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ClearList("sales");
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        SalesRepInfo salesRepInfo=snapshot.getValue(SalesRepInfo.class);
                        SalesRepList.add(salesRepInfo.getName());
                        SalesRepMapList.put(salesRepInfo.getName(),salesRepInfo.getUserId());
                    }
                    setDataIntoSRListSpinner();
                    getRouteList();
                }
                else{
                    Toast.makeText(ManagerAppointmentActivity.this, "No data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ManagerAppointmentActivity.this, "No data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setDataIntoSRListSpinner(){
        ArrayAdapter<String> dataAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,SalesRepList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void setDataToRouteListSpinner(){
        ArrayAdapter<String> dataAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,routeList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        routeSpinner.setAdapter(dataAdapter);
    }

    private void getRouteList(){
        firebaseReferenceUtils.getRouteRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ClearList("route");
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Route route=snapshot.getValue(Route.class);
                        routeList.add(route.getRouteName());
                    }
                    setDataToRouteListSpinner();
                    setLoading(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setLoading(false);
            }
        });
    }
    private void getOutletList(){
        firebaseReferenceUtils.getOutletListRef(selectedRoute).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    ClearList("outlet");
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        OutletInformation outletInformation=snapshot.getValue(OutletInformation.class);
                        outletList.add(outletInformation.getOutletName());
                        outletMapList.put(outletInformation.getOutletName(),outletInformation);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ;
            }
        });
    }

    private void setAlertDialogForToAddAppointment(){

        String[] outletNames=new String[outletList.size()];
        outletNames=outletList.toArray(outletNames);

        /*List<Boolean> checked=new ArrayList<>();
        for (int i = 0; i <outletList.size() ; i++) {
            if (SelectedOutletList.size()!=0){
                for (int j = 0; j <SelectedOutletList.size() ; j++) {
                    if (outletList.get(i).equals(SelectedOutletList.get(j))){
                        checked.add(true);
                    }
                    else {
                        checked.add(false);
                    }
                }
            }
            else{
                checked.add(false);}

        }*/
        boolean[] is_Checked=new boolean[outletNames.length];
        for (int i = 0; i <outletNames.length ; i++) {
            if (SelectedOutletList.size()!=0){
                for (int j = 0; j <SelectedOutletList.size() ; j++) {
                    if (outletList.get(i).equals(SelectedOutletList.get(j))){
                        is_Checked[i]=true;
                    }
                    else{
                        is_Checked[i]=false;
                    }
                }
            }
            else{
                is_Checked[i]=false;
            }
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(ManagerAppointmentActivity.this);
        builder.setTitle("Add appointments");
        builder.setMultiChoiceItems(outletNames, is_Checked, (dialog, which, isChecked) -> {
            is_Checked[which]=isChecked;
            String currentItem=outletList.get(which);
            Toast.makeText(ManagerAppointmentActivity.this, "item: "+currentItem, Toast.LENGTH_SHORT).show();

        });
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i <is_Checked.length ; i++) {
                    boolean checked=is_Checked[i];
                    if (checked){
                        SelectedOutletList.add(outletList.get(i));
                        Log.d("cc>>","data: "+outletList.get(i));
                    }

                }
                //call to add dynamic row
            }
        });
        builder.setCancelable(false);
        AlertDialog dialog=builder.create();
        dialog.show();

    }

    private void setLoading(boolean status){
        if (status){
            progressBar.setVisibility(View.VISIBLE);
            container.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            container.setVisibility(View.VISIBLE);
        }
    }

    private void ClearList(String type){
        if (type.equals("sales")){
            SalesRepMapList.clear();
            SalesRepList.clear();
        }
        else if (type.equals("route")){
            routeList.clear();
        }
        else{
            outletList.clear();
            outletMapList.clear();
        }

    }

    private void addTextView(){
        if (SelectedOutletList.size()>0){
            for (int i = 0; i <SelectedOutletList.size() ; i++) {
                RelativeLayout.LayoutParams lprams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (i==0){

                    TextView textView=new TextView(ManagerAppointmentActivity.this);
                    textView.setText(SelectedOutletList.get(i));
                    lprams.addRule(RelativeLayout.BELOW,routeSpinner.getId());
                    textView.setLayoutParams(lprams);
                    textView.setId(i);
                    textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_btn));
                    rootRelative.addView(textView);
                }
                else{
                    TextView textView=new TextView(ManagerAppointmentActivity.this);
                    textView.setText(SelectedOutletList.get(i));
                    lprams.addRule(RelativeLayout.BELOW,i);
                    textView.setLayoutParams(lprams);
                    textView.setId(i);
                    textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_btn));
                    rootRelative.addView(textView);
                }

            }
        }
    }

/*// first Button
    RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.rlayout);
    RelativeLayout.LayoutParams lprams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
    Button tv1 = new Button(this);
    tv1.setText("Hello");
    tv1.setLayoutParams(lprams);
    tv1.setId(1);
    rLayout.addView(tv1);

    // second Button
    RelativeLayout.LayoutParams newParams = new RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT);
    Button tv2 = new Button(this);
    tv1.setText("Hello2");
    newParams.addRule(RelativeLayout.RIGHT_OF, 1);
    tv2.setLayoutParams(newParams);
    tv2.setId(2);
    rLayout.addView(tv2);*/

}
