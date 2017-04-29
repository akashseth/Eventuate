package com.bhumca2017.eventuate;

/**
 * Created by toaka on 13-04-2017.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;


public class AddAvailabilityImageActivity extends BaseActivity {

    Bitmap photo = null;
    private int IMAGE_FROM_GALLERY = 1;
    private int IMAGE_FROM_CAMERA = 2;

    private static String UPLOAD_URL;

    private static final String LOG_TAG = AddAvailabilityImageActivity.class.getSimpleName();
    private Integer mServiceProviderId=2;
    private Integer mAvailabilityId=1;
    private String mAvailabilityName="room";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UPLOAD_URL=getString(R.string.ip_address)+"/Eventuate/Services/AddAvailabilityImage.php";
        setContentView(R.layout.activity_add_availability_image);

        Button uploadImgButton=(Button)findViewById(R.id.upload_Image);

        uploadImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photo != null) {
                    new UploadImageAsyncTask().execute();
                } else {
                    showEmptyImageAlert();
                }
            }
        });
    }

    void showEmptyImageAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Empty Image");
        alertDialog.setMessage("Please select an image");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });
        alertDialog.show();
    }

    public void clickPic(View view) {
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
            imageView.setImageBitmap(photo);
        }
        else if (requestCode == IMAGE_FROM_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                photo = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(photo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showFileChooser(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_FROM_GALLERY);
    }

    public String getImgString()
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private HashMap<String, String> mGetPostDataForAddImage(){

        HashMap<String ,String> postData = new HashMap<>();
        postData.put("serviceProviderId", mServiceProviderId.toString());
        postData.put("availabilityId",mAvailabilityId.toString());
        postData.put("imageName",mAvailabilityName+"-"+(new Date().getTime())+".jpg");
        if(photo == null) {
            postData.put("base64","null");
        } else {
            postData.put("base64", getImgString());
        }

        return postData;
    }

    private class UploadImageAsyncTask extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(AddAvailabilityImageActivity.this);
            progressDialog.setMessage("Uploading please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... imgString) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();
            String jsonResponse = "";
            try {
                 jsonResponse = requestHandler.sendPostRequest(UPLOAD_URL,mGetPostDataForAddImage());

            } catch (IOException e) {
                Log.e(LOG_TAG,"Problem while requesting post method",e);
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {

           progressDialog.hide();
            if(jsonResponse.equals("1")){
                //
                Toast.makeText(AddAvailabilityImageActivity.this,"Uploaded succesfully",Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                Toast.makeText(AddAvailabilityImageActivity.this,"Unable to upload please try again",Toast.LENGTH_SHORT).show();
            }



        }
    }
}




