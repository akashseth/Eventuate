package com.bhumca2017.eventuate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class EditAvailabilitiesActivity extends BaseActivity {

    private static final String LOG_TAG = EditAvailabilitiesActivity.class.getSimpleName();
    private static String UPDATE_PRICE_URL;
    private static String DELETE_AVAIL_URL;
    private static String IMAGES_AVAIL_URL;
    private String mAvailabilityName;
    private Integer mServiceAvailabilityId;
    EditText priceView, quantityView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_availabilities);
        UPDATE_PRICE_URL = getString(R.string.ip_address)+"/Eventuate/Services/UpdatePriceQuantity.php";
        DELETE_AVAIL_URL = getString(R.string.ip_address)+"/Eventuate/Services/DeleteServiceAvailability.php";
        IMAGES_AVAIL_URL = getString(R.string.ip_address)+"/Eventuate/Services/FetchAvailImagesPath.php";

        priceView = (EditText)findViewById(R.id.edit_price);
        quantityView= (EditText)findViewById(R.id.edit_quantity);
        setIntentExtraValue();

        TextView  availTextView= (TextView)findViewById(R.id.availability_name);
        availTextView.setText(mAvailabilityName);


        final Button addImageButton=(Button)findViewById(R.id.add_availability_image);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addImageIntent=new Intent(getApplicationContext(),AddAvailabilityImageActivity.class);
                addImageIntent.putExtra("serviceAvailabilityId",mServiceAvailabilityId);
                addImageIntent.putExtra("availabilityName",mAvailabilityName);
                startActivity(addImageIntent);
            }
        });



        Button savePriceButton = (Button)findViewById(R.id.save_price_quantity);
        savePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new UpdatePriceQuantityAsyncTask().execute();
            }
        });

        Button deleteServiceAvailabilityButton = (Button)findViewById(R.id.delete_availability);
        deleteServiceAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeleteAlert();
            }
        });


        new FetchAvailImagesPath().execute();

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
        priceView.setText(getIntent().getStringExtra("price"));
        quantityView.setText(getIntent().getStringExtra("quantity"));

    }

    private HashMap<String, String> mGetPostDataForUpdatePriceQuantity(){

        HashMap<String,String> hashMap = new HashMap<>();

        hashMap.put("price",priceView.getText().toString());
        hashMap.put("serviceAvailabilityId",mServiceAvailabilityId.toString());
        hashMap.put("quantity",quantityView.getText().toString());
        return hashMap;
    }

    private class UpdatePriceQuantityAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendPostRequest(UPDATE_PRICE_URL,mGetPostDataForUpdatePriceQuantity());
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

    private ArrayList<AvailabilityImages> getExtractedDataFromJson(String jsonResponse)  {

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

    private String mGetRequestGetUrl() {

        String requestUrl =  IMAGES_AVAIL_URL+"?serviceAvailabilityId="+mServiceAvailabilityId;
        //Log.e(LOG_TAG,requestUrl);
        return requestUrl;
    }
    private class FetchAvailImagesPath extends AsyncTask<String,Void,ArrayList<AvailabilityImages>> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<AvailabilityImages> doInBackground(String... imgString) {

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
        protected void onPostExecute(ArrayList<AvailabilityImages> images) {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            GridView imageGridView = (GridView)findViewById(R.id.avail_images_grid_list);
            if(images.isEmpty()) {

                TextView noImageView =(TextView)findViewById(R.id.no_image_avail);
                noImageView.setVisibility(View.VISIBLE);
                imageGridView.setEmptyView(noImageView);

            } else {

                AvailabilityImagesAdapter adapter = new AvailabilityImagesAdapter(EditAvailabilitiesActivity.this, images);
                imageGridView.setAdapter(adapter);

            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        new FetchAvailImagesPath().execute();
    }
}
