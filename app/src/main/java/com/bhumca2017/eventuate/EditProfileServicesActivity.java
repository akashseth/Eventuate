package com.bhumca2017.eventuate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class EditProfileServicesActivity extends BaseActivity {

    private static  String UPLOAD_URL;
    private ArrayList<Integer> listOfServicesCheckedId = new ArrayList<>();
    private boolean isAtleastOneSevicesSelected=false;
    private HashMap<String, String>mFormData;
    private Integer userId;
    SessionServices sessionServices;
    String fullName,mobNo,address;
    TextView fullNameTextView,addressTextView,mobilNoTextView;
    CheckBox hotel,catering,lawns,decorator,tenting,travel,restaurants,photography,musical;
    ArrayList<CheckBox>serviceTypeCheckBoxes;
    Set<String> leavingServicesId;
    String leavingServiceMessage;
    Set<String> servicesOptedSet;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_profile_services);

        UPLOAD_URL=getString(R.string.ip_address)+"/Eventuate/Services/EditProfile.php";

        sessionServices = new SessionServices(this);
        userId = sessionServices.getUserId();

        fullNameTextView=(TextView)findViewById(R.id.fullName);
        addressTextView=(TextView)findViewById(R.id.address);
        mobilNoTextView=(TextView)findViewById(R.id.mobile_no);

         hotel = (CheckBox)findViewById(R.id.hotel);
         catering = (CheckBox)findViewById(R.id.catring);
         lawns = (CheckBox)findViewById(R.id.lawns);
         decorator = (CheckBox)findViewById(R.id.decorator);
         tenting = (CheckBox)findViewById(R.id.tenting);
         travel = (CheckBox)findViewById(R.id.travel);
         restaurants = (CheckBox)findViewById(R.id.restaurants);
         photography = (CheckBox)findViewById(R.id.photography);
         musical = (CheckBox)findViewById(R.id.musical);
        serviceTypeCheckBoxes = new ArrayList<>();


        fillDataInForm();

        Button submitButton = (Button)findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFormData=getFormData();


                if(fullName.length() == 0 || mobNo.length() == 0 || address.length() ==0){

                    Toast.makeText(EditProfileServicesActivity.this,"All fields are mandatory",Toast.LENGTH_LONG).show();
                    return ;
                }
                if(mobNo.length()!=10){
                    Toast.makeText(EditProfileServicesActivity.this,"Mobile no is invalid",Toast.LENGTH_LONG).show();
                    return ;
                }
                if(isAtleastOneSevicesSelected) {

                    if(!leavingServicesId.isEmpty()){

                        showLeavingServicesWarning(leavingServiceMessage.substring(0,leavingServiceMessage.length()-2));
                    }
                    else {
                        new EditProfileAsyncTask().execute();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),"Please select at least one service",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void fillDataInForm(){
        serviceTypeCheckBoxes.add(hotel);
        serviceTypeCheckBoxes.add(catering);
        serviceTypeCheckBoxes.add(lawns);
        serviceTypeCheckBoxes.add(decorator);
        serviceTypeCheckBoxes.add(tenting);
        serviceTypeCheckBoxes.add(travel);
        serviceTypeCheckBoxes.add(restaurants);
        serviceTypeCheckBoxes.add(photography);
        serviceTypeCheckBoxes.add(musical);

        Set<String> serviceOpted = sessionServices.getServicesOpted();

        for(String serviceType : serviceOpted){

            serviceTypeCheckBoxes.get(Integer.parseInt(serviceType)-1).setChecked(true);
        }

        fullNameTextView.setText(sessionServices.getFullName());
        mobilNoTextView.setText(sessionServices.getMobNo());
        addressTextView.setText(sessionServices.getAddress());

    }

    void setOptedServicesList(){

        isAtleastOneSevicesSelected=false;

        ArrayList<CheckBox> selectedBox = new ArrayList<>();
        selectedBox.add(hotel);
        selectedBox.add(catering);
        selectedBox.add(lawns);
        selectedBox.add(decorator);
        selectedBox.add(tenting);
        selectedBox.add(travel);
        selectedBox.add(restaurants);
        selectedBox.add(photography);
        selectedBox.add(musical);

        HashMap<String ,Integer> hashMap = new HashMap<>();
        hashMap.put("Hotel",1);
        hashMap.put("Catering",2);
        hashMap.put("Lawns & Banquettes",3);
        hashMap.put("Decorator",4);
        hashMap.put("Tenting",5);
        hashMap.put("Travel",6);
        hashMap.put("Restaurants",7);
        hashMap.put("Video/Photography",8);
        hashMap.put("Musical",9);
        listOfServicesCheckedId.clear();
        for(CheckBox box : selectedBox){
            if(box.isChecked()){

                isAtleastOneSevicesSelected=true;
                listOfServicesCheckedId.add(hashMap.get(box.getText().toString()));
                //Log.e("akash",hashMap.get(box.getText().toString())+"");

            }
        }

    }

    private HashMap getFormData(){

        setOptedServicesList();
        HashMap<String, String> postData = new HashMap<>();


        fullName = fullNameTextView.getText().toString();
        address = addressTextView.getText().toString();
        mobNo = mobilNoTextView.getText().toString();

        if(!isAtleastOneSevicesSelected) {
            return postData;
        }

        postData.put("fullName",fullName);

        postData.put("address",address);

        postData.put("mobileNo",mobNo);

        postData.put("userId",userId.toString());


        String servicesSelected="";
        ArrayList<String> temp = new ArrayList<>();
        for(Integer id : listOfServicesCheckedId){
            servicesSelected+=id+",";
            temp.add(id+"");
        }

        servicesOptedSet = new HashSet<>();
        servicesOptedSet.addAll(temp);

        Set<String> oldServicesOptedSet = sessionServices.getServicesOpted();
        leavingServicesId = new HashSet<>();
        leavingServiceMessage="";
        for(String serviceId : oldServicesOptedSet){
            if(!servicesOptedSet.contains(serviceId)){
                leavingServicesId.add(serviceId);
                leavingServiceMessage+=serviceTypeCheckBoxes.get(Integer.parseInt(serviceId)-1).getText().toString()+", ";
            }
        }
        String servicesLeaving="";
        if(!leavingServicesId.isEmpty()){

            for(String id : leavingServicesId){
                servicesLeaving+=id+",";
            }
            servicesLeaving = servicesLeaving.substring(0,servicesLeaving.length()-1);
        }
        servicesSelected=servicesSelected.substring(0,servicesSelected.length()-1);
        postData.put("servicesId",servicesSelected);
        postData.put("leavingServicesId",servicesLeaving);


      //  Log.e("fjkdjkfjklf",postData.toString());
        return postData;
    }

    private void showLeavingServicesWarning(String leavingServiceMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leaving services warning");
        builder.setMessage("Your are leaving "+leavingServiceMessage+" services. Your all data related to these services will be deleted.");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new EditProfileAsyncTask().execute();
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.show();
    }

    private class EditProfileAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {

            sessionServices.updateProfile(servicesOptedSet,fullName,mobNo,address);

            progressDialog = new ProgressDialog(EditProfileServicesActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            ServerRequestHandler requestHandler = new ServerRequestHandler();
            String jsonResponse="";
            try {
               jsonResponse = requestHandler.sendPostRequest(UPLOAD_URL,mFormData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String response) {
            progressDialog.hide();

            Toast.makeText(EditProfileServicesActivity.this,"Profile updated successfully",Toast.LENGTH_SHORT).show();

            Intent dashboardIntent = new Intent(EditProfileServicesActivity.this,DashboardActivity.class);
            startActivity(dashboardIntent);
            finish();
        }
    }

}

