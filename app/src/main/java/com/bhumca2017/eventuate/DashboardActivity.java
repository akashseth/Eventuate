package com.bhumca2017.eventuate;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class DashboardActivity extends BaseActivity {

    SessionServices sessionServices;
    private static final String LOG_TAG = DashboardActivity.class.getSimpleName();
    private static String FETCH_SERVICES_URL;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        FETCH_SERVICES_URL = getString(R.string.ip_address)+"/Eventuate/Services/FetchServicesProviding.php";
        sessionServices = new SessionServices(this);
        userId = sessionServices.getUserId();

       /* servicesItem.add(new Services("Hotel",1,R.drawable.hotel));
        servicesItem.add(new Services("Catering",2,R.drawable.catering));
        servicesItem.add(new Services("Lawns & Banquettes",3,R.drawable.lawns));
        servicesItem.add(new Services("Decorator",4,R.drawable.decorator));
        servicesItem.add(new Services("Tenting",5,R.drawable.tenting));
        servicesItem.add(new Services("Travel",6,R.drawable.travel));
        servicesItem.add(new Services("Restaurants",7,R.drawable.restaurants));
        servicesItem.add(new Services("Video/Photograph",8,R.drawable.video));
        servicesItem.add(new Services("Musical",9,R.drawable.musical));*/

       new FetchServicesAsyncTask().execute();


    }


    private ArrayList<Services> getExtractedDataFromJson(String jsonResponse)  {

        ArrayList<Services> servicesList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for(int i =0 ;i < jsonArray.length(); i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int serviceId= jsonObject.getInt("serviceId");
                String serviceName = jsonObject.getString("ServiceName");

                StringTokenizer stringTokenizer = new StringTokenizer(serviceName,"/|&");
                String resourceName = stringTokenizer.nextToken().trim().toLowerCase();
                int imageResourceId = getResId(resourceName, R.drawable.class);

                servicesList.add(new Services(serviceName,serviceId,imageResourceId));
                Log.e(LOG_TAG,resourceName+" "+imageResourceId);

            }
        }catch (JSONException e) {

            Log.e(LOG_TAG,"Error in parsing json",e);
        }

        return servicesList;
    }
    private String mGetRequestGetUrl() {

        String requestUrl =  FETCH_SERVICES_URL+"?userId="+userId;
        //Log.e(LOG_TAG,requestUrl);
        return requestUrl;
    }
    private class FetchServicesAsyncTask extends AsyncTask<String,Void, ArrayList<Services>> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected  ArrayList<Services> doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendGetRequest(mGetRequestGetUrl());
               return getExtractedDataFromJson(jsonResponse);

            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return new ArrayList<>();
        }

        @Override
        protected void onPostExecute( ArrayList<Services> servicesItem) {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            GridView  servicesGridView=(GridView)findViewById(R.id.dashbord_item);
            final ServicesItemAdapter adapter=new ServicesItemAdapter(DashboardActivity.this,servicesItem);
            servicesGridView.setAdapter(adapter);

            servicesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent=new Intent(getApplicationContext(),ServicesAvailabilitiesActivity.class);
                    intent.putExtra("serviceId",adapter.getItem(position).getServiceId());
                    // Log.e("id","this "+adapter.getItem(position).getServiceId());
                    startActivity(intent);
                }
            });

        }
    }
    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}


