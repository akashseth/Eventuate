package com.bhumca2017.eventuate;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class OrganiserAvailabilityActivity extends AppCompatActivity {

    private static final String LOG_TAG = OrganiserAvailabilityActivity.class.getSimpleName();
    private static String FETCH_AVAIL_URL ;
    private Integer mServiceId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organiser_availability);
        setIntentData();
        FETCH_AVAIL_URL = getString(R.string.ip_address)+"/Eventuate/fetchAvailabilityOrganiser.php";

        new FetchAvailOrganiser().execute();
    }

    public void setIntentData(){

        mServiceId = getIntent().getIntExtra("serviceId",0);
    }

    private ArrayList<AvailabilityItemsOrganiser> getExtractedDataFromJson(String jsonResponse)  {

        ArrayList<AvailabilityItemsOrganiser> availabilityList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for(int i =0 ;i < jsonArray.length(); i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String providerName = jsonObject.getString("fullName");
                Integer serviceAvailabilityId = jsonObject.getInt("id");
                String availabilityName = jsonObject.getString("availability_name");
                Integer serviceProviderId = jsonObject.getInt("service_provider_id");
                Integer availabilityId = jsonObject.getInt("availability_id");
                String price = jsonObject.getString("price");
                availabilityList.add(new AvailabilityItemsOrganiser(serviceAvailabilityId,serviceProviderId,availabilityId,availabilityName,
                        price,providerName));
            }
        }catch (JSONException e) {

            Log.e(LOG_TAG,"Error in parsing json",e);
        }

        return availabilityList;
    }

    private String mGetRequestGetUrl() {

        String requestUrl =  FETCH_AVAIL_URL+"?serviceId="+mServiceId;
        //Log.e(LOG_TAG,requestUrl);
        return requestUrl;
    }
    private class FetchAvailOrganiser extends AsyncTask<String,Void,ArrayList<AvailabilityItemsOrganiser>> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<AvailabilityItemsOrganiser> doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendGetRequest(mGetRequestGetUrl());
                return getExtractedDataFromJson(jsonResponse);

            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<AvailabilityItemsOrganiser> availabilityList) {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            ListView availabilityListView = (ListView)findViewById(R.id.availability_list_organiser);
            AvailabilityItemsOrganiserAdapter adapter = new AvailabilityItemsOrganiserAdapter(OrganiserAvailabilityActivity.this,availabilityList);
            availabilityListView.setAdapter(adapter);
            if(availabilityList.isEmpty()) {

                TextView noAvailabilityTextView =(TextView)findViewById(R.id.no_availability);
                noAvailabilityTextView.setVisibility(View.VISIBLE);

            }
        }
    }

}
