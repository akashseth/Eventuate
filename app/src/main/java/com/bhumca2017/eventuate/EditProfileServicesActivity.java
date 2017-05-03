package com.bhumca2017.eventuate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class EditProfileServicesActivity extends BaseActivity {

    private static final String UPLOAD_URL="http://10.0.2.2/Eventuate/Services/EditProfile.php";
    private ArrayList<Integer> listOfServicesCheckedId = new ArrayList<>();
    private boolean isAtleastOneSevicesSelected=false;
    private HashMap<String, String>mFormData;
    private Integer userId=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite_profile_services);

        Button submitButton = (Button)findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFormData=getFormData();
                if(isAtleastOneSevicesSelected) {
                    new EditProfileAsyncTask().execute();

                } else {
                    Toast.makeText(getApplicationContext(),"Please select at least one service",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void setOptedServicesList(){

        isAtleastOneSevicesSelected=false;

        CheckBox hotel = (CheckBox)findViewById(R.id.hotel);
        CheckBox catering = (CheckBox)findViewById(R.id.catring);
        CheckBox lawns = (CheckBox)findViewById(R.id.lawns);
        CheckBox decorator = (CheckBox)findViewById(R.id.decorator);
        CheckBox tenting = (CheckBox)findViewById(R.id.tenting);
        CheckBox travel = (CheckBox)findViewById(R.id.travel);
        CheckBox restaurants = (CheckBox)findViewById(R.id.restaurants);
        CheckBox photography = (CheckBox)findViewById(R.id.photography);
        CheckBox musical = (CheckBox)findViewById(R.id.musical);

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
        if(!isAtleastOneSevicesSelected) {
            return postData;
        }

        TextView fullName=(TextView)findViewById(R.id.fullName);
        postData.put("fullName",fullName.getText().toString());

        TextView address=(TextView)findViewById(R.id.address);
        postData.put("address",address.getText().toString());

        TextView mobilNo=(TextView)findViewById(R.id.mobile_no);
        postData.put("mobileNo",mobilNo.getText().toString());

        postData.put("userId",userId.toString());

        String servicesSelected="";
        for(Integer id : listOfServicesCheckedId){
            servicesSelected+=id+",";
        }
        servicesSelected=servicesSelected.substring(0,servicesSelected.length()-1);
        postData.put("servicesId",servicesSelected);

        return postData;
    }

    private class EditProfileAsyncTask extends AsyncTask<Void, Void, String> {

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

            Intent dashboardIntent = new Intent(EditProfileServicesActivity.this,DashboardActivity.class);
            startActivity(dashboardIntent);
            finish();
        }
    }

}

