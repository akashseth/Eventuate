package com.bhumca2017.eventuate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class AddAvailabilitiesActivity extends AppCompatActivity {

    Bitmap photo = null;
    private int IMAGE_FROM_GALLERY = 1;
    private int IMAGE_FROM_CAMERA = 2;
    private static final String LOG_TAG = AddAvailabilityImageActivity.class.getSimpleName();
    private static String ADD_Avail_URL;
    private static String ADD_Avail_NAME_URL;
    private static String FETCH_Avail_URL;

    private ArrayList<String> mAvailabilities;
    private int mServiceId;
    private String mNewAvailabilityName;
    private String mSelectedAvailabilityName;
    private boolean mAddedNewAvailability = false;
    private int mServiceProviderId;

    SessionServices sessionServices;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_availabilities);

        sessionServices = new SessionServices(this);
        mServiceProviderId = sessionServices.getUserId();
        setServiceId();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ADD_Avail_URL = getString(R.string.ip_address)+"/Eventuate/Services/AddAvailability.php";
        ADD_Avail_NAME_URL=this.getString(R.string.ip_address)+"/Eventuate/Services/AddAvailabilityName.php";
        FETCH_Avail_URL=this.getString(R.string.ip_address)+"/Eventuate/Services/FetchAvailability.php";

        new FetchAvailabilityAsyncTask().execute();

        Button addAvailabilityNameButton =(Button)findViewById(R.id.add_availability);

        addAvailabilityNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAvailabilityName();

            }
        });

        Button addImageButton = (Button)findViewById(R.id.add_image);
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageChooserDialog();
            }
        });

        Button addAvailabilityButton =(Button)findViewById(R.id.submit);

        addAvailabilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AddAvailabilityAsyncTask().execute();
            }
        });


    }
    private void setServiceId(){

        mServiceId = getIntent().getIntExtra("serviceId",0);
    }
    void imageChooserDialog(){
        final CharSequence items[] = {"Take Photo","Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        TextView title = new TextView(this);
        title.setText("Add Image");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        builder.setCustomTitle(title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(items[which] == "Take Photo") {
                    clickPic();
                } else {
                    showFileChooser();

                    //Log.e(LOG_TAG,photo.toString());
                }
            }
        });
        builder.show();
    }

    public void clickPic() {
        // Check Camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // start the image capture Intent
            startActivityForResult(intent, IMAGE_FROM_CAMERA);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = (ImageView) findViewById(R.id.image_preview);
        if (requestCode == IMAGE_FROM_CAMERA && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(photo);
        }
        else if (requestCode == IMAGE_FROM_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                photo = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_FROM_GALLERY);
    }

    private String getImgString()
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private ArrayList<String> getExtractedDataFromJson(String jsonResponse)  {

        ArrayList<String> availabilityNameList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            for(int i =0 ;i < jsonArray.length(); i++){

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                availabilityNameList.add(jsonObject.getString("availability_name"));
            }
        }catch (JSONException e) {

            Log.e(LOG_TAG,"Error in parsing json",e);
        }

        return availabilityNameList;
    }
    private String mGetRequestGetUrl() {

        String requestUrl =  FETCH_Avail_URL+"?serviceId="+mServiceId;
        //Log.e(LOG_TAG,requestUrl);
        return requestUrl;
    }
    private class FetchAvailabilityAsyncTask extends AsyncTask<String,Void,String> {

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
                mAvailabilities = getExtractedDataFromJson(jsonResponse);

            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            Spinner availabilityList=(Spinner)findViewById(R.id.availability_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, mAvailabilities);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            availabilityList.setAdapter(adapter);
           if(mAddedNewAvailability) {
               Toast.makeText(getApplicationContext(),"new",Toast.LENGTH_LONG).show();
               availabilityList.setSelection(mAvailabilities.size() - 1);

           }
        }
    }

    void addAvailabilityName(){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setMessage("Add new availability name");

        final EditText input = new EditText(this);

        alert.setView(input);

        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //Toast.makeText(getApplicationContext(),input.getText().toString(),Toast.LENGTH_SHORT).show();
                mNewAvailabilityName = input.getText().toString();
                new AddAvailabilityNameAsyncTask().execute();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
    private HashMap<String, String> mGetPostDataForAddAvailabilityName(){

        HashMap<String ,String> postData = new HashMap<>();
        postData.put("serviceId", mServiceId+"");
        postData.put("availabilityName",mNewAvailabilityName);
        return postData;
    }

    private class AddAvailabilityNameAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendPostRequest(ADD_Avail_NAME_URL,mGetPostDataForAddAvailabilityName());
                if(jsonResponse.equals("1")){
                    //Log.e(LOG_TAG,jsonResponse);
                    mAddedNewAvailability = true;
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

            new FetchAvailabilityAsyncTask().execute();

        }
    }

    private HashMap<String, String> mGetPostDataForAddAvailability(){

        Spinner spinner = (Spinner)findViewById(R.id.availability_list);
        mSelectedAvailabilityName = spinner.getSelectedItem().toString();

        TextView priceView = (TextView)findViewById(R.id.price);
        String price = priceView.getText().toString();

        HashMap<String ,String> postData = new HashMap<>();
        postData.put("serviceProviderId", mServiceProviderId+"");
        postData.put("serviceId", mServiceId+"");
        postData.put("availabilityName",mSelectedAvailabilityName);
        postData.put("price",price);
        postData.put("imageName",mSelectedAvailabilityName+"-"+(new Date().getTime())+".jpg");
        if(photo == null) {
            postData.put("base64","null");
        } else {
            postData.put("base64", getImgString());
        }

        TextView quantityView = (TextView)findViewById(R.id.quantity);
        String quantity = quantityView.getText().toString();
        postData.put("quantity",quantity);

        return postData;
    }

    private class AddAvailabilityAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendPostRequest(ADD_Avail_URL,mGetPostDataForAddAvailability());
                return jsonResponse;
            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            if(result.equals("1")){

                Toast.makeText(AddAvailabilitiesActivity.this,"Successfully added new availability",Toast.LENGTH_LONG).show();
                finish();
            } else {

                Toast.makeText(AddAvailabilitiesActivity.this,"Unable to add. Plea try again",Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAddedNewAvailability = false;
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent prevIntent = new Intent(this,ServicesAvailabilitiesActivity.class);
        prevIntent.putExtra("serviceId",mServiceId);
        startActivity(prevIntent);
        finish();
    }
}
