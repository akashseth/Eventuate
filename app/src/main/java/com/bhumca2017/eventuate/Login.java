package com.bhumca2017.eventuate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.HashSet;
import java.util.Set;

public class Login extends Activity {

    EditText emailId, passCode;
    Button signInButton, registerButton;
    TextView net_conn;

    String userEmail, userPass;
    int userIdService;
    ProgressDialog progressDialog;

    Integer attemptsLeft = 3;   // after 3 unsuccessful login attempts, the SIGNIN button will be disabled

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        net_conn = (TextView)findViewById(R.id.network_conn_status);
        emailId = (EditText) findViewById(R.id.email);
        passCode = (EditText) findViewById(R.id.passcode);
        signInButton = (Button) findViewById(R.id.email_sign_in_button);
        registerButton = (Button) findViewById(R.id.register_button);

        // check whether network connection is available or not, if not, the signIn & register buttons are disabled
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected())
            net_conn.setVisibility(View.INVISIBLE);
        else
        {
            //signInButton.setEnabled(false);
            //registerButton.setEnabled(false);
        }

    }

    public void attemptLogin(View view) {

        userEmail=emailId.getText().toString();
        userPass=passCode.getText().toString();

        if( !Patterns.EMAIL_ADDRESS.matcher(emailId.getText()).matches()) {

            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userEmail.equals("") || userPass.equals(""))
        {
            // case when Email or Password field is empty
            Toast.makeText(getApplicationContext(), "EMAIL & PASSCODE required ",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            // attempt login
            new BackgroundTaskVerification().execute();
        }
    }

    public void newRegistration(View view)
    {
        Intent intent = new Intent(Login.this, NewUserRegister.class);
        startActivity(intent);
    }


    //  for background tasks for attemptLogin

    public class BackgroundTaskVerification extends AsyncTask<Void, Void, String> {

        String url_verification, json_string;

        @Override
        protected void onPreExecute()
        {
            // url of php script for handling post registration tasks
            url_verification=getString(R.string.ip_address)+"/eventuate/verification.php";

            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // connecting to the url
                URL url = new URL(url_verification);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("Email", "UTF-8") + "=" + URLEncoder.encode(userEmail, "UTF-8") + "&" +
                        URLEncoder.encode("Passcode", "UTF-8") + "=" + URLEncoder.encode(userPass, "UTF-8");
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

            progressDialog.hide();
            if((result.equals("")))
                Toast.makeText(getApplicationContext(), "No user exists for this Email...Try again!!", Toast.LENGTH_LONG).show();
            else
            {
                json_string = result;
                Log.e("log",json_string);

                JSONObject jsonObject;
                String Email="", Passcode="", UserType="";

                // parsing the json string
                try {
                    jsonObject = new JSONObject(json_string);
                    Email = jsonObject.getString("EmailId");
                    Passcode = jsonObject.getString("PassCode");
                    UserType = jsonObject.getString("UserType");
                    userIdService = jsonObject.getInt("userId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Checking for correctness of passcode
                if(Passcode.equals(userPass))
                {
                    // pass the json data to the next activity
                    Intent intent;
                    if(UserType.equals("Organizer"))
                    {
                        intent = new Intent(getApplicationContext(), SigningIn.class);
                        intent.putExtra("EmailId", Email);
                        startActivity(intent);
                        finish();
                    }
                    else if(UserType.equals("Service"))
                    {
                        try {
                            jsonObject = new JSONObject(json_string);
                            String fullName = jsonObject.getString("fullName");
                            String address = jsonObject.getString("address");
                            String mobileNo = jsonObject.getString("mobileNo");
                            JSONArray servicesId = jsonObject.getJSONArray("servicesId");
                            Set<String>servicesIdSet = new HashSet<>();
                            for(int i = 0; i < servicesId.length(); i++){
                                servicesIdSet.add(servicesId.getString(i));
                            }
                            new SessionServices(Login.this).updateProfile(servicesIdSet,fullName,mobileNo,address);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        new SessionServices(Login.this).createLoginSession(userIdService,Email);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                {
                    attemptsLeft--;
                    Toast.makeText(getApplicationContext(), "Attempts Left = "+attemptsLeft.toString(), Toast.LENGTH_SHORT).show();
                    if(attemptsLeft == 0)
                        signInButton.setEnabled(false);
                }
            }
        }
    }
}