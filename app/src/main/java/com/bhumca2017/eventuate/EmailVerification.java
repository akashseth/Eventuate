package com.bhumca2017.eventuate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class EmailVerification extends AppCompatActivity {

    String json_string;
    String Email, Passcode, UserType;
    JSONObject jsonObject;

    TextView email;
    EditText passcode;
    Button signInButton;

    Integer attemptsLeft = 3;   // after 3 unsuccessful login attempts, the SIGNIN button will be disabled

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        email = (TextView) findViewById(R.id.eMail);
        passcode = (EditText) findViewById(R.id.passCode);
        signInButton = (Button)findViewById(R.id.email_sign_in_button);

        // extracts the json string
        json_string = getIntent().getExtras().getString("json_data");

        // parsing the json string
        try {
            jsonObject = new JSONObject(json_string);
            Email = jsonObject.getString("newUserEmailId");
            Passcode = jsonObject.getString("newUserPasscode");
            UserType = jsonObject.getString("newUserType");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        email.setText(Email);
        passcode.setText(Passcode);

        Toast.makeText(this, Passcode, Toast.LENGTH_SHORT).show();      // remove this after passcode is sent to email
    }

    public void attemptSignIn(View view)
    {
        String attempt_Passcode;

        attempt_Passcode = passcode.getText().toString();

        if(!(attempt_Passcode.equals(Passcode)))
        {
            attemptsLeft--;
            Toast.makeText(this, "Login attempts Left = "+attemptsLeft.toString(), Toast.LENGTH_SHORT).show();
            if(attemptsLeft == 0)
                signInButton.setEnabled(false);
        }
        else        // successful verification
            new BackgroundTaskVerification().execute();
    }


    // Background tasks to execute the php script registration_success.php

    public class BackgroundTaskVerification extends AsyncTask<Void, Void, String> {

        String url_registration_success, json_string;

        @Override
        protected void onPreExecute()
        {
            // url of php script for handling post registration tasks
            url_registration_success=getString(R.string.ip_address)+"/eventuate/registration_success.php";

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // connecting to the url
                URL url = new URL(url_registration_success);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(Email, "UTF-8") + "&" +
                        URLEncoder.encode("UserType", "UTF-8") + "=" + URLEncoder.encode(UserType, "UTF-8") + "&" +
                        URLEncoder.encode("Passcode", "UTF-8") + "=" + URLEncoder.encode(Passcode, "UTF-8");
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

            //Log.e("json",result);
            if((result.equals("")))
                Toast.makeText(getApplicationContext(), "Registration Unsuccessful...Try again!!", Toast.LENGTH_LONG).show();
            else
            {
                Toast.makeText(getApplicationContext(), "Registration Successful...", Toast.LENGTH_LONG).show();

                json_string = result;


                // pass the json data to the next activity
                Intent intent;
                if(UserType.equals("Organizer"))
                {
                    intent = new Intent(getApplicationContext(), EditProfileOrganizer.class);
                    intent.putExtra("json_data", json_string);
                    startActivity(intent);
                    finish();
                }
                else if(UserType.equals("Service"))
                {
                    intent = new Intent(getApplicationContext(), EditProfileServicesActivity.class);
                   // intent.putExtra("serviceId", 1);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }
}

