package com.bhumca2017.eventuate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BookingDetailsOrg extends AppCompatActivity {

    private static final String LOG_TAG = BookingDetailsOrg.class.getSimpleName();
    private static String FETCH_SERVICES_PROFILE_URL;
    private static String IMAGES_AVAIL_URL;

    private int mServiceProviderId;
    private int mPrice;
    private int mServiceId;
    private int mServiceAvailabilityId;
    private String mAvailabilityName;


    TextView priceTextView,availabilityNameTextView, availQuantityView;

    ProgressDialog progressDialog;
    SessionOrganiser sessionOrganiser;
    int budgetLeft;
    int eventBudget;
    Integer mQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability_details_organiser);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FETCH_SERVICES_PROFILE_URL = getString(R.string.ip_address)+"/Eventuate/Services/FetchProfile.php";

        IMAGES_AVAIL_URL = getString(R.string.ip_address)+"/Eventuate/Services/FetchAvailImagesPath.php";


        sessionOrganiser = new SessionOrganiser(this);
        budgetLeft = sessionOrganiser.getBudgetLeft();
        eventBudget = sessionOrganiser.getBudget();
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.booking_details);
        linearLayout.setVisibility(View.GONE);

        setIntentExtraData();
        priceTextView = (TextView)findViewById(R.id.rate);
        String rateType;
        if(mServiceId==6){
            rateType = "/km + 50rs";
        } else if(mServiceId == 2 || mServiceId ==7 ){
            rateType = "/serving";
        } else{
            rateType = "/day";
        }
        priceTextView.setText(mPrice+rateType);


        TextView quantityNameTextView = (TextView)findViewById(R.id.quantity_text);
        quantityNameTextView.setText("Quantity : ");

        availabilityNameTextView = (TextView)findViewById(R.id.availability_name);
        availabilityNameTextView.setText(mAvailabilityName);

        availQuantityView = (TextView)findViewById(R.id.quantity_available);
        availQuantityView.setText(mQuantity.toString());


        new FetchServicesProfile().execute();

    }


    void setIntentExtraData(){
        mServiceProviderId = getIntent().getIntExtra("serviceProviderId",0);
        mPrice = getIntent().getIntExtra("price",0);
        mQuantity = getIntent().getIntExtra("quantity",-1);
        mPrice /= mQuantity;
        mServiceId = getIntent().getIntExtra("serviceId",0);
        mServiceAvailabilityId = getIntent().getIntExtra("serviceAvailabilityId",0);
        mAvailabilityName = getIntent().getStringExtra("availabilityName");
    }
    private HashMap<String,String> getExtractedDataFromJson(String jsonResponse)  {

        HashMap<String, String> profileServices = new HashMap<>();
        try {


            JSONObject jsonObject = new JSONObject(jsonResponse);
            profileServices.put("name",jsonObject.getString("fullName"));
            profileServices.put("address",jsonObject.getString("address"));
            profileServices.put("mobileNo",jsonObject.getString("mobNo"));
            profileServices.put("emailId",jsonObject.getString("EMAIL_ID"));

        }catch (JSONException e) {

            Log.e(LOG_TAG,"Error in parsing json",e);
        }

        return profileServices;
    }
    private String mGetRequestGetUrl() {

        String requestUrl =  FETCH_SERVICES_PROFILE_URL+"?serviceProviderId="+mServiceProviderId;
        //Log.e(LOG_TAG,requestUrl);
        return requestUrl;
    }
    private class FetchServicesProfile extends AsyncTask<String,Void,HashMap<String,String>> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected HashMap<String, String> doInBackground(String... imgString) {

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
        protected void onPostExecute(HashMap<String, String> profile) {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            TextView nameTextView = (TextView)findViewById(R.id.service_provider_name_details);
            TextView addressTextView = (TextView)findViewById(R.id.service_provider_address_details);
            TextView mobileNoTextView = (TextView)findViewById(R.id.service_provider_mob_details);
            TextView emailTextView = (TextView)findViewById(R.id.service_provider_email_details);

            nameTextView.setText(profile.get("name"));
            addressTextView.setText(profile.get("address"));
            mobileNoTextView.setText(profile.get("mobileNo"));
            emailTextView.setText(profile.get("emailId"));

            new FetchAvailImagesPath().execute();



        }
    }


    private ArrayList<AvailabilityImages> getExtractedDataFromJsonForImages(String jsonResponse)  {

        ArrayList<AvailabilityImages> imagesPathList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for(int i =0 ;i < jsonArray.length(); i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String imagePath = getString(R.string.ip_address)+"/eventuate/availabilityImages/";
                String imageName =  jsonObject.getString("image_location");
                if(!imageName.equals("none")) {
                    imagesPathList.add(new AvailabilityImages(jsonObject.getInt("id"), imagePath + imageName));
                }
            }
        }catch (JSONException e) {

            Log.e(LOG_TAG,"Error in parsing json",e);
        }

        return imagesPathList;
    }

    private String mGetRequestGetUrlForImages() {

        String requestUrl =  IMAGES_AVAIL_URL+"?serviceAvailabilityId="+mServiceAvailabilityId;
        //Log.e(LOG_TAG,requestUrl);
        return requestUrl;
    }
    private class FetchAvailImagesPath extends AsyncTask<String,Void,ArrayList<AvailabilityImages>> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(BookingDetailsOrg.this);
            progressDialog.setMessage("Loading images. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<AvailabilityImages> doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendGetRequest(mGetRequestGetUrlForImages());
                return getExtractedDataFromJsonForImages(jsonResponse);

            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<AvailabilityImages> images) {
            progressDialog.hide();

            GridView imageGridView = (GridView)findViewById(R.id.avail_images_grid_list);
            if(images.isEmpty()) {

                TextView noImageView =(TextView)findViewById(R.id.no_image_avail);
                noImageView.setVisibility(View.VISIBLE);
                imageGridView.setEmptyView(noImageView);

            } else {

                AvailabilityImagesAdapter adapter = new AvailabilityImagesAdapter(getApplicationContext(), images,"organiser");
                imageGridView.setAdapter(adapter);

            }
        }
    }





    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent prevIntent = new Intent(this,MyBookings.class);
       // prevIntent.putExtra("serviceId",mServiceId);
        startActivity(prevIntent);
        finish();
    }
}
