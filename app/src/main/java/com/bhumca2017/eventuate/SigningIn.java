package com.bhumca2017.eventuate;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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

public class SigningIn extends AppCompatActivity {

    String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing_in);

        Email = getIntent().getExtras().getString("EmailId");

        new BackgroundTaskLogin().execute();
    }


    // for background tasks for login, i.e. retrieving user and event data


    public class BackgroundTaskLogin extends AsyncTask<Void, Void, String> {

        String url_login, json_string;

        @Override
        protected void onPreExecute() {
            // url of php script for handling registration
            url_login = getString(R.string.ip_address)+"/eventuate/login.php";

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // connecting to the url
                URL url = new URL(url_login);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("EmailId", "UTF-8") + "=" + URLEncoder.encode(Email, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();


                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));

                String line = "";
                StringBuilder stringBuilder = new StringBuilder();  // returns the JSON response

                while  ((line=bufferedReader.readLine())!=null)
                {
                    stringBuilder.append((line+"\n"));
                }

                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();

                // returning a JSON string
                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
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

            json_string = result;
            SessionOrganiser sessionOrganiser = new SessionOrganiser(getApplicationContext());
            sessionOrganiser.createLoginSession(json_string);

            try {
                JSONObject jsonObject = new JSONObject(json_string);
               // String OrganizerEmail = jsonObject.getString("EmailId");
                String EventType = jsonObject.getString("EventType");
                Integer EventDateDayOfMonth = jsonObject.getInt("EventDateDayOfMonth");
                Integer EventDateMonth = jsonObject.getInt("EventDateMonth");
                Integer EventDateYear = jsonObject.getInt("EventDateYear");
                Integer EventTimeFromHours = jsonObject.getInt("EventTimeFromHours");
                Integer EventTimeFromMinutes = jsonObject.getInt("EventTimeFromMinutes");
                Integer EventTimeToHours = jsonObject.getInt("EventTimeToHours");
                Integer  EventTimeToMinutes = jsonObject.getInt("EventTimeToMinutes");
                Integer EventBudget = jsonObject.getInt("EventBudget");
               // TotalExpenditure = jsonObject.getInt("TotalExpenditure");
               Integer BudgetLeft = jsonObject.getInt("BudgetLeft");
                String organiserMob = jsonObject.getString("OrganizerMob");
                String organiserAddress = jsonObject.getString("OrganizerAddress");

                sessionOrganiser.saveEventDetails(EventType,EventDateDayOfMonth,EventDateMonth,EventDateYear,EventTimeFromHours,
                        EventTimeFromMinutes,EventTimeToHours,EventTimeToMinutes);
                sessionOrganiser.updateBudget(EventBudget);
                sessionOrganiser.updateBudgetLeft(BudgetLeft);
                sessionOrganiser.updateOrganiserMobNo(organiserMob);
                sessionOrganiser.updateOrganiserAddress(organiserAddress);

            }catch (JSONException e){

            }

            // pass the json data to the next activity
            Intent intent = new Intent(getApplicationContext(), DashboardOrganiseActivity.class);

            startActivity(intent);
            finish();
        }
    }
}
