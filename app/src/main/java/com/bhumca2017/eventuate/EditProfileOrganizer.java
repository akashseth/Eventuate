package com.bhumca2017.eventuate;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class EditProfileOrganizer extends BaseActivityOrganiser {

    String json_string;
    JSONObject jsonObject;

    String OrganizerEmail, OrganizerName, OrganizerMob, OrganizerAddress;

    TextView organizer_name, organizer_mob, organizer_address;
    Button submit_organizer_profile;
    SessionOrganiser sessionOrganiser;


    boolean drawer_flag = SetDrawerFlag.getDrawerFlagProfile();
    // if drawer_flag=false => first time profile is edited, i.e., next step is EditEventDetails &
    //                          in php, data is inserted into the table
    // if drawer_flag=true => profile is edited previously, only updated this time, i.e., next step is Dashboard &
    //                          in php, data is updated into the table

    boolean drawer_flag_input = SetDrawerFlag.getDrawerFlagProfileInput();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_organizer);

        sessionOrganiser = new SessionOrganiser(this);

        organizer_name = (EditText)findViewById(R.id.OrganizerName);
        organizer_mob = (EditText)findViewById(R.id.OrganizerMob);
        organizer_address = (EditText)findViewById(R.id.OrganizerAddress);
        submit_organizer_profile = (Button)findViewById(R.id.button_submit_profileOrganizer);

        // extracts the json string
       // json_string = getIntent().getExtras().getString("json_data");

        if(sessionOrganiser.getDrawerFlagProfileInput() == true)       // organizer data exists in the database
        {

                OrganizerEmail = sessionOrganiser.getOrganiserEmail();
                OrganizerName = sessionOrganiser.getOrganiserName();
                OrganizerMob = sessionOrganiser.getOrganizerMobNo();
                OrganizerAddress = sessionOrganiser.getOrganizerAddress();


            organizer_name.setText(OrganizerName);
            organizer_mob.setText(OrganizerMob);
            organizer_address.setText(OrganizerAddress);
        }
        if(drawer_flag_input == false)       // organizer data doesn't exist in the database
        {
            // parsing the json string

                OrganizerEmail = sessionOrganiser.getOrganiserEmail();

        }

    }



    public void submitOrganizerProfile(View view)
    {
        OrganizerName = organizer_name.getText().toString();
        OrganizerMob = organizer_mob.getText().toString();
        OrganizerAddress = organizer_address.getText().toString();

        if(OrganizerName.equals("") || OrganizerMob.equals("") || OrganizerAddress.equals(""))
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_LONG).show();
        else if(OrganizerMob.length() != 10)
            Toast.makeText(this, "Mobile No. is invalid...", Toast.LENGTH_LONG).show();
        else {
            new BackgroundTask_updateOrganizerProfile().execute();
        }
    }


    // Background tasks to execute the php script updateOrganizerProfile.php

    public class BackgroundTask_updateOrganizerProfile extends AsyncTask<Void, Void, String> {

        String url_updateOrganizerProfile, json_string, flag_val;

        @Override
        protected void onPreExecute()
        {
            // url of php script for handling profile updation tasks for the organizer
            url_updateOrganizerProfile=getString(R.string.ip_address)+"/eventuate/updateOrganizerProfile.php";

            if(drawer_flag_input == true)
                flag_val = "true";
            else
                flag_val = "false";

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // connecting to the url
                URL url = new URL(url_updateOrganizerProfile);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("DrawerFlag", "UTF-8") + "=" + URLEncoder.encode(flag_val, "UTF-8") + "&" +
                        URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(OrganizerEmail, "UTF-8") + "&" +
                        URLEncoder.encode("Name", "UTF-8") + "=" + URLEncoder.encode(OrganizerName, "UTF-8") + "&" +
                        URLEncoder.encode("Mob", "UTF-8") + "=" + URLEncoder.encode(OrganizerMob, "UTF-8") + "&" +
                        URLEncoder.encode("Address", "UTF-8") + "=" + URLEncoder.encode(OrganizerAddress, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();


                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();  // returns the JSON response

                while((line=bufferedReader.readLine())!=null)
                    stringBuilder.append((line+"\n"));

                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();

                // returning a json object string containing email id of the user
                return stringBuilder.toString().trim();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {

            if((result.equals("")))
                Toast.makeText(getApplicationContext(), "Profile not updated...Try again!!", Toast.LENGTH_LONG).show();
            else
            {
                Toast.makeText(getApplicationContext(), "Profile updated successfully...", Toast.LENGTH_LONG).show();
                SetDrawerFlag.setDrawerFlagProfileInput(true);


                json_string = result;

                sessionOrganiser = new SessionOrganiser(EditProfileOrganizer.this);
                sessionOrganiser.updateOrganiserName(OrganizerName);
                sessionOrganiser.updateOrganiserMobNo(OrganizerMob);
                sessionOrganiser.updateOrganiserAddress(OrganizerAddress);
                organizerName.setText(OrganizerName);


                // pass the json data to the next activity
                Intent intent;
                if(drawer_flag == false)
                {
                    intent = new Intent(getApplicationContext(), EditEventDetails.class);
                    intent.putExtra("json_data", json_string);
                    startActivity(intent);
                    finish();
                }
                else if(drawer_flag == true)
                {
                    /*intent = new Intent(getApplicationContext(), SigningIn.class);

                    // parsing the json string
                    try {
                        jsonObject = new JSONObject(json_string);
                        OrganizerEmail = jsonObject.getString("EmailId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    intent.putExtra("EmailId", OrganizerEmail);
                    startActivity(intent);*/
                    Intent dashboardIntent = new Intent(EditProfileOrganizer.this,DashboardOrganiseActivity.class);
                    startActivity(dashboardIntent);
                    finish();
                }
            }
        }
    }
}
