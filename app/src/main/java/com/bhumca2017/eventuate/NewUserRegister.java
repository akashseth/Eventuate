package com.bhumca2017.eventuate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class NewUserRegister extends AppCompatActivity {

    RadioGroup userType;
    RadioButton userTypeOrganizer, userTypeService;
    EditText newUserEmail;
    Button registerUser;

    String newUserType, newUserEmailId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_register);

        userType=(RadioGroup)findViewById(R.id.user_type_select);
        userTypeOrganizer=(RadioButton)findViewById(R.id.button_usertype_organizer);
        userTypeService=(RadioButton)findViewById(R.id.button_usertype_service);
        newUserEmail=(EditText)findViewById(R.id.newuser_email);
        registerUser=(Button)findViewById(R.id.button_newuserregister);

        SetDrawerFlag.setDrawerFlagProfileInput(false);
        SetDrawerFlag.setDrawerFlagEventInput(false);
        SetDrawerFlag.setDrawerFlagProfile(false);
        SetDrawerFlag.setDrawerFlagEvent(false);
    }

    public void verifyEmail(View view)
    {
        if( !Patterns.EMAIL_ADDRESS.matcher(newUserEmail.getText()).matches()) {

            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if(userType.getCheckedRadioButtonId()==-1)      // when no radio button is checked
        {
            Toast.makeText(this, "Select User Type", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // extracting the new user type
            if(userTypeOrganizer.isChecked())
                newUserType="Organizer";
            else if(userTypeService.isChecked())
                newUserType="Service";

            // extracting the new user email id
            newUserEmailId = newUserEmail.getText().toString();


            new BackgroundTaskRegistration().execute();
        }
    }

    public class BackgroundTaskRegistration extends AsyncTask<Void, Void, String> {

        String url_register, json_string;

        @Override
        protected void onPreExecute()
        {
            // url of php script for handling registration
            url_register=getString(R.string.ip_address)+"/eventuate/register.php";
            progressDialog = new ProgressDialog(NewUserRegister.this);
            progressDialog.setMessage("Registering. Please wait... ");
            progressDialog.setCancelable(false);
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // connecting to the url
                URL url = new URL(url_register);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("newUserType", "UTF-8") + "=" + URLEncoder.encode(newUserType, "UTF-8") + "&" +
                                URLEncoder.encode("newUserEmailId", "UTF-8") + "=" + URLEncoder.encode(newUserEmailId, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();


                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));

                String response="";
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();  // returns the JSON response

                while  ((line=bufferedReader.readLine())!=null)
                {
                    response+=line;

                    stringBuilder.append((line+"\n"));
                }

                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();

                if(response == "User already exists....")
                    return response;
                else    // returning a JSON string
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
            //Log.e("result",result);
            if(result.equals("User already exists...."))
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            else
            {
                json_string = result;

                // pass the json data to the next activity
                Intent intent = new Intent(getApplicationContext(), EmailVerification.class);
                intent.putExtra("json_data", json_string);
                startActivity(intent);
                finish();
            }
        }
    }
}
