package com.bhumca2017.eventuate;

import android.app.Notification;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AvailabilityDetailsOrganiserActivity extends AppCompatActivity {

    private static final String LOG_TAG = AvailabilityDetailsOrganiserActivity.class.getSimpleName();
    private static String FETCH_SERVICES_PROFILE_URL;
    private static String IMAGES_AVAIL_URL;
    private static String FETCH_AVAIL_QUANTITY_URL;
    private static String BOOK_AVAIL_URL;
    private int mServiceProviderId;
    private String mPrice;
    private int mServiceId;
    private int mServiceAvailabilityId;
    private String mAvailabilityName;
    private int quantityAvailable;

    TextView priceTextView,availabilityNameTextView, availQuantityView;
    RadioGroup paymentRadioGroup;
    RadioButton cashRadioButton,upiRadioButton;
    EditText quantityEditText;
    String paymentMethod;
    Button bookButton;

    ProgressDialog progressDialog;
    SessionOrganiser sessionOrganiser;
    int budgetLeft;
    int eventBudget;
    EditText editDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability_details_organiser);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FETCH_SERVICES_PROFILE_URL = getString(R.string.ip_address)+"/Eventuate/Services/FetchProfile.php";
        FETCH_AVAIL_QUANTITY_URL = getString(R.string.ip_address)+"/Eventuate/FetchAvailabilityQuantity.php";
        IMAGES_AVAIL_URL = getString(R.string.ip_address)+"/Eventuate/Services/FetchAvailImagesPath.php";
        BOOK_AVAIL_URL = getString(R.string.ip_address)+"/Eventuate/BookAvailability.php";

        sessionOrganiser = new SessionOrganiser(this);
        budgetLeft = sessionOrganiser.getBudgetLeft();
        eventBudget = sessionOrganiser.getBudget();

        setIntentExtraData();
        priceTextView = (TextView)findViewById(R.id.rate);
        editDistance = (EditText)findViewById(R.id.edit_distance);
        String rateType;
        if(mServiceId==6){
            rateType = "/km + 50rs";
            editDistance.setVisibility(View.VISIBLE);
        } else if(mServiceId == 2 || mServiceId ==7 ){
            rateType = "/serving";
        } else{
            rateType = "/day";
        }
        priceTextView.setText(mPrice+rateType);

        availabilityNameTextView = (TextView)findViewById(R.id.availability_name);
        availabilityNameTextView.setText(mAvailabilityName);

        availQuantityView = (TextView)findViewById(R.id.quantity_available);

        paymentRadioGroup = (RadioGroup)findViewById(R.id.payment_method);
        cashRadioButton = (RadioButton)findViewById(R.id.cash);
        upiRadioButton = (RadioButton)findViewById(R.id.upi);
        paymentRadioGroup.check(R.id.cash);

        new FetchServicesProfile().execute();

        quantityEditText = (EditText)findViewById(R.id.edit_quantity);

        bookButton = (Button)findViewById(R.id.book_availability);
        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkDataFilled();
            }
        });


    }

    void updateAvailabilityQuantity(int quantity){

        quantityAvailable = quantity;
        if(quantity == 0){

            bookButton.setEnabled(false);
            bookButton.setBackgroundColor(getResources().getColor(R.color.button_pressed));
        }
        availQuantityView.setText(quantity+"");
    }

    void checkDataFilled(){

        if(mServiceId==6){

            String distance = editDistance.getText().toString();
            if(distance.length()==0){

                Toast.makeText(getApplicationContext(),"Please enter distance",Toast.LENGTH_LONG).show();
                return;
            }
            if(Integer.parseInt(distance) <= 1) {

                Toast.makeText(getApplicationContext(),"Please enter distance greater than 1 km",Toast.LENGTH_LONG).show();
                return;
            }

        }

        String quantity = quantityEditText.getText().toString();
        if(quantity.length()==0){

            Toast.makeText(getApplicationContext(),"Please enter quantity",Toast.LENGTH_LONG).show();
            return;
        }
        if(Integer.parseInt(quantity) == 0) {

            Toast.makeText(getApplicationContext(),"Please enter quantity greater than zero",Toast.LENGTH_LONG).show();
            return;
        }
        int totalAmount = Integer.parseInt(mPrice)*(Integer.parseInt(quantityEditText.getText().toString()));
        if(mServiceId==6){

            totalAmount *= Integer.parseInt(editDistance.getText().toString());
            totalAmount += 50;
        }
        if(Integer.parseInt(quantity) > this.quantityAvailable){

            Toast.makeText(getApplicationContext(),"Please choose quantity less than or equal to available quantity",Toast.LENGTH_LONG).show();
        } else if(sessionOrganiser.getEventType().length()==0){

            Toast.makeText(this,"Set your event first",Toast.LENGTH_LONG).show();
        }
        else if(totalAmount > budgetLeft) {

            showExceedingAmountAlert(totalAmount-budgetLeft);
        }
        else {

            showAlertWhileBooking();
        }
    }

    void showExceedingAmountAlert(int exceedingAmount){

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Event Budget Exceeding");

        builder.setMessage("You have "+budgetLeft+" rs left in your budget. Your event budget will increase by "+exceedingAmount+" rs .");

        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
               showAlertWhileBooking();
            }
        });

        builder.setNegativeButton("cancel",null);
        builder.show();
    }

    void showAlertWhileBooking(){
        paymentMethod =  paymentRadioGroup.getCheckedRadioButtonId()==R.id.cash ?  "cash" : "upi";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Booking Confirmation");
        if(paymentMethod.equals("cash")) {
            builder.setMessage("You have chosen cash as payment method. You have to pay cash directly to service provider.");
        } else {
            builder.setMessage("You have chosen upi as payment method. Do you want to book it?");
        }
        builder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new BookAvailabilityAsyncTask().execute();
            }
        });

        builder.setNegativeButton("cancel",null);
        builder.show();
    }

    void setIntentExtraData(){
        mServiceProviderId = getIntent().getIntExtra("serviceProviderId",0);
        mPrice = getIntent().getStringExtra("price");
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

            new FetchAvailQuantity().execute();



        }
    }
    private HashMap<String,Integer> getExtractedDataFromJsonForAvailQuantity(String jsonResponse)  {

        HashMap<String, Integer> availQuantity = new HashMap<>();
        try {


            JSONObject jsonObject = new JSONObject(jsonResponse);
            availQuantity.put("quantityAvailable",jsonObject.getInt("quantity_available"));

        }catch (JSONException e) {

            Log.e(LOG_TAG,"Error in parsing json",e);
        }

        return availQuantity;
    }
    private String mGetRequestGetUrlForAvailQuantity() {

        String requestUrl =  FETCH_AVAIL_QUANTITY_URL+"?serviceAvailabilityId="+mServiceAvailabilityId +
                "&emailId="+new SessionOrganiser(this).getOrganiserEmail();
        Log.e(LOG_TAG,requestUrl);
        return requestUrl;
    }
    private class FetchAvailQuantity extends AsyncTask<String,Void,HashMap<String,Integer>> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected HashMap<String, Integer> doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendGetRequest(mGetRequestGetUrlForAvailQuantity());
                return getExtractedDataFromJsonForAvailQuantity(jsonResponse);

            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, Integer> availQuantity) {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            updateAvailabilityQuantity(availQuantity.get("quantityAvailable"));

            new FetchAvailImagesPath().execute();

        }
    }

    private ArrayList<AvailabilityImages> getExtractedDataFromJsonForImages(String jsonResponse)  {

        ArrayList<AvailabilityImages> imagesPathList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for(int i =0 ;i < jsonArray.length(); i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String imagePath = getString(R.string.ip_address)+"/Eventuate/availabilityImages/";
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
            progressDialog = new ProgressDialog(AvailabilityDetailsOrganiserActivity.this);
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

                AvailabilityImagesAdapter adapter = new AvailabilityImagesAdapter(AvailabilityDetailsOrganiserActivity.this, images,"organiser");
                imageGridView.setAdapter(adapter);

            }
        }
    }


    private HashMap<String,Integer> getExtractedDataFromJsonForBookingAvail(String jsonResponse)  {

        HashMap<String, Integer> bookingStatus = new HashMap<>();
        try {


            JSONObject jsonObject = new JSONObject(jsonResponse);
            bookingStatus.put("result",jsonObject.getInt("result"));
            if(bookingStatus.get("result") != 2) {
                bookingStatus.put("quantityAvailable", jsonObject.getInt("quantityAvailable"));
            }

        }catch (JSONException e) {

            Log.e(LOG_TAG,"Error in parsing json",e);
        }

        return bookingStatus;
    }

    private HashMap<String, String> mGetPostDataForAddAvailabilityName(){

        int totalAmount = Integer.parseInt(mPrice)*(Integer.parseInt(quantityEditText.getText().toString()));
        if(mServiceId==6){

            totalAmount *= Integer.parseInt(editDistance.getText().toString());
            totalAmount += 50;
        }
        int amountPaid=0,amountDue=0;
        budgetLeft = budgetLeft - totalAmount;
        if(budgetLeft<0){
            eventBudget = eventBudget + Math.abs(budgetLeft);
            budgetLeft = 0;
        }
        sessionOrganiser.updateBudget(eventBudget);
        sessionOrganiser.updateBudgetLeft(budgetLeft);
        if(paymentMethod.equals("cash")){

            amountDue = totalAmount;
        } else {
            amountPaid = totalAmount;
        }

        HashMap<String ,String> postData = new HashMap<>();
        postData.put("serviceAvailabilityId", mServiceAvailabilityId+"");
        postData.put("serviceProviderId",mServiceProviderId+"");
        postData.put("amountPaid",amountPaid+"");
        postData.put("amountDue",amountDue+"");
        postData.put("emailId",new SessionOrganiser(this).getOrganiserEmail());
        postData.put("quantity",quantityEditText.getText().toString());
        postData.put("paymentMethod",paymentMethod);
        postData.put("eventBudget",eventBudget+"");
        postData.put("budgetLeft",budgetLeft+"");
        return postData;
    }

    private class BookAvailabilityAsyncTask extends AsyncTask<String,Void,HashMap<String,Integer>> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AvailabilityDetailsOrganiserActivity.this);
            progressDialog.setMessage("Booking order. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected HashMap<String,Integer> doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendPostRequest(BOOK_AVAIL_URL,mGetPostDataForAddAvailabilityName());
                return getExtractedDataFromJsonForBookingAvail(jsonResponse);
            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return new HashMap<>();
        }

        @Override
        protected void onPostExecute(HashMap<String,Integer> bookingStatus) {

            progressDialog.hide();
            if(bookingStatus.get("result")==2){
                Toast.makeText(AvailabilityDetailsOrganiserActivity.this,"Unable to book please try again",Toast.LENGTH_LONG).show();
            } else {

                if (bookingStatus.get("result")==1){
                    Toast.makeText(AvailabilityDetailsOrganiserActivity.this,"Booking success and it is waiting approval from provider",Toast.LENGTH_LONG).show();
                    Intent myBookingsIntent = new Intent(AvailabilityDetailsOrganiserActivity.this,MyBookings.class);
                    startActivity(myBookingsIntent);
                    finish();
                } else {

                    updateAvailabilityQuantity(bookingStatus.get("quantityAvailable"));
                    Toast.makeText(AvailabilityDetailsOrganiserActivity.this,"Unable to book. Quantity you entered is greater than availability",Toast.LENGTH_LONG).show();
                }
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
        Intent prevIntent = new Intent(this,OrganiserAvailabilityActivity.class);
        prevIntent.putExtra("serviceId",mServiceId);
        startActivity(prevIntent);
        finish();
    }
}
