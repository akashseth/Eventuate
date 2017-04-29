package com.bhumca2017.eventuate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;

public class EditAvailabilitiesActivity extends BaseActivity {

    private static final String LOG_TAG = EditAvailabilitiesActivity.class.getSimpleName();
    private static String UPDATE_PRICE_URL;
    private static String DELETE_AVAIL_URL;
    private String mAvailabilityName;
    private Integer mServiceAvailabilityId;
    private String mPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_availabilities);
        UPDATE_PRICE_URL = getString(R.string.ip_address)+"/Eventuate/Services/UpdatePrice.php";
        DELETE_AVAIL_URL = getString(R.string.ip_address)+"/Eventuate/Services/DeleteServiceAvailability.php";


        setIntentExtraValue();

        TextView  availTextView= (TextView)findViewById(R.id.availability_name);
        availTextView.setText(mAvailabilityName);

        EditText  priceView= (EditText)findViewById(R.id.edit_price);
        priceView.setText(mPrice);

        Button addImageButton=(Button)findViewById(R.id.add_availability_image);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addImageIntent=new Intent(getApplicationContext(),AddAvailabilityImageActivity.class);
                startActivity(addImageIntent);
            }
        });

        Button savePriceButton = (Button)findViewById(R.id.save_price);
        savePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new UpdatePriceAsyncTask().execute();
            }
        });

        Button deleteServiceAvailabilityButton = (Button)findViewById(R.id.delete_availability);
        deleteServiceAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeleteAlert();
            }
        });

    }

    void showDeleteAlert() {

        AlertDialog.Builder  builder =new AlertDialog.Builder(this);
        builder.setTitle("Delete Confirmation");
        builder.setMessage("Are you sure to delete it?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new DeleteAvailabilityAsyncTask().execute();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
    void setIntentExtraValue() {

        mAvailabilityName = getIntent().getStringExtra("availabilityName");
        mServiceAvailabilityId = getIntent().getIntExtra("serviceAvailabilityId",0);
        mPrice = getIntent().getStringExtra("price");

    }

    private HashMap<String, String> mGetPostDataForUpdatePrice(){

        HashMap<String,String> hashMap = new HashMap<>();
        EditText  priceView= (EditText)findViewById(R.id.edit_price);
        hashMap.put("price",priceView.getText().toString());
        hashMap.put("serviceAvailabilityId",mServiceAvailabilityId.toString());

        return hashMap;
    }

    private class UpdatePriceAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendPostRequest(UPDATE_PRICE_URL,mGetPostDataForUpdatePrice());
                if(jsonResponse.equals("1")){
                    //

                }
            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);


        }
    }

    private HashMap<String, String> mGetPostDataForDeleteAvailability(){

        HashMap<String,String> hashMap = new HashMap<>();

        hashMap.put("serviceAvailabilityId",mServiceAvailabilityId.toString());

        return hashMap;
    }

    private class DeleteAvailabilityAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendPostRequest(DELETE_AVAIL_URL,mGetPostDataForDeleteAvailability());
                if(jsonResponse.equals("1")){
                    //
                }
            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            finish();
        }
    }


}
