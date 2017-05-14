package com.bhumca2017.eventuate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ServicesAvailabilitiesActivity extends BaseActivity {

    private static final String LOG_TAG = ServicesAvailabilitiesActivity.class.getSimpleName();
    private static String  FETCH_Avail_LIST_URL;
    private int mServiceId;
    private int mServiceProviderId ;
    ArrayList<ServiceAvailability> mServiceAvailabilitiesList;
    SessionServices sessionServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_availibities);

        sessionServices = new SessionServices(this);
        mServiceProviderId = sessionServices.getUserId();

        FETCH_Avail_LIST_URL = getString(R.string.ip_address)+"/Eventuate/Services/FetchServiceAvailabilities.php";

        setServiceId();

        Button addAvailabilitiesButton=(Button)findViewById(R.id.add_availabilities);
        addAvailabilitiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addServicesIntent=new Intent(getApplicationContext(),AddAvailabilitiesActivity.class);
                addServicesIntent.putExtra("serviceId",mServiceId);
                startActivity(addServicesIntent);
            }
        });


        new FetchAvailabilityListAsyncTask().execute();




    }

    private void setServiceId()
    {
        Intent serviceActivity= getIntent();
        mServiceId=serviceActivity.getIntExtra("serviceId",0);
    }

    private ArrayList<ServiceAvailability> getExtractedDataFromJson(String jsonResponse) {
        ArrayList<ServiceAvailability> serviceAvailabilitiesList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for (int i = 0; i < jsonArray.length() ;i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String price = jsonObject.getString("price");
                String availabilityName = jsonObject.getString("availability_name");
                Integer serviceAvailabilityId = jsonObject.getInt("id");
                Integer availabilityId = jsonObject.getInt("availability_id");
                String quantity = jsonObject.getString("quantity");
                ServiceAvailability serviceAvailability = new ServiceAvailability(availabilityName
                        ,price,availabilityId,serviceAvailabilityId,quantity,mServiceId);
                serviceAvailabilitiesList.add(serviceAvailability);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG,"Error in parsing json array",e);
        }

        return  serviceAvailabilitiesList;
    }
    private String mGetRequestGetUrl() {

        String requestUrl =  FETCH_Avail_LIST_URL+"?serviceId="+mServiceId+"&serviceProviderId="+mServiceProviderId;
        //Log.e(LOG_TAG,requestUrl);
        return requestUrl;
    }

    private class FetchAvailabilityListAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendGetRequest(mGetRequestGetUrl());
                mServiceAvailabilitiesList = getExtractedDataFromJson(jsonResponse);

            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            ListView listView=(ListView)findViewById(R.id.availability_list);
            ServiceAvailabilityAdapter adapter=new ServiceAvailabilityAdapter(getApplicationContext(),mServiceAvailabilitiesList);
            listView.setAdapter(adapter);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FetchAvailabilityListAsyncTask().execute();
    }
}
