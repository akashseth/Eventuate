package com.bhumca2017.eventuate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class EditSelectedExpenditure extends BaseActivityOrganiser {

    String EmailId; Integer sno; String date, amount, details;
    Bundle info;
    String flag;

    EditText inputDate, inputAmount, inputDetails;

    String Date, Details; Integer Amount;
    Integer totalBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_selected_expenditure);

        inputDate = (EditText) findViewById(R.id.expenditureDate_edit);
        inputAmount = (EditText) findViewById(R.id.expenditureAmount_edit);
        inputDetails = (EditText) findViewById(R.id.expenditureDetails_edit);

        info = getIntent().getExtras();

        EmailId = info.getString("Email");

        if(info.getString("method").equals("update"))       // update expenditure
        {
            sno = info.getInt("Sno");
            date = info.getString("Date");
            amount = info.getString("Amount");
            amount = amount.substring(4);
            details = info.getString("Details");

            flag = "true";

            inputDate.setText(date);
            inputAmount.setText(amount);
            inputDetails.setText(details);
        }
        else if(info.getString("method").equals("insert"))                // add expenditure
        {
            flag = "false";

            sno = 0;
            date = "";
            amount = "0";
            details = "";
        }
    }

    public void submit(View view)
    {
        Date = inputDate.getText().toString();
        Amount = Integer.parseInt(inputAmount.getText().toString());
        Details = inputDetails.getText().toString();

        if(Date.equals("") || Amount.toString().equals("") || Details.equals(""))
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
        else
        {
            SessionOrganiser sessionOrganiser = new SessionOrganiser(EditSelectedExpenditure.this);
            int previousBudget = sessionOrganiser.getBudget();
            int budgetLeft = sessionOrganiser.getBudgetLeft()==-1 ? previousBudget : sessionOrganiser.getBudgetLeft();
            int oldAmount = Integer.parseInt(this.amount);
            int newAmount = Amount;
            budgetLeft = budgetLeft + oldAmount - newAmount;
            totalBudget = previousBudget;

            if(budgetLeft<0){
                totalBudget = previousBudget + Math.abs(budgetLeft);
                budgetLeft = 0;
                Toast.makeText(EditSelectedExpenditure.this, "Your total budget has been increased to "+totalBudget+". Limit your expands",Toast.LENGTH_LONG).show();
            }
            sessionOrganiser.updateBudget(totalBudget);
            sessionOrganiser.updateBudgetLeft(budgetLeft);
            new BackgroundTask_updateExpenditure().execute();
        }
    }

    // background tasks for updating the expenditure details

    public class BackgroundTask_updateExpenditure extends AsyncTask<Void, Void, String> {

        String url_updateExpenditure;

        @Override
        protected void onPreExecute()
        {
            // url of php script for updating the expenditure details
            url_updateExpenditure=getString(R.string.ip_address)+"/eventuate/update_expenditure.php";
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // connecting to the url
                URL url = new URL(url_updateExpenditure);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("EmailId", "UTF-8") + "=" + URLEncoder.encode(EmailId, "UTF-8") + "&" +
                              URLEncoder.encode("Flag", "UTF-8") + "=" + URLEncoder.encode(flag, "UTF-8") + "&" +
                              URLEncoder.encode("Sno", "UTF-8") + "=" + sno + "&" +
                              URLEncoder.encode("Date", "UTF-8") + "=" + URLEncoder.encode(Date, "UTF-8") + "&" +
                              URLEncoder.encode("Amount", "UTF-8") + "=" + Amount + "&" +
                              URLEncoder.encode("OldAmount", "UTF-8") + "=" + Integer.parseInt(amount) + "&" +
                              URLEncoder.encode("totalBudget", "UTF-8") + "=" + totalBudget + "&" +
                              URLEncoder.encode("Details", "UTF-8") + "=" + URLEncoder.encode(Details, "UTF-8");
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

            String json_string, Email="";
            JSONObject jsonObject;

            if((result.equals("")))
                Toast.makeText(getApplicationContext(), "Update not successful, try again!!!", Toast.LENGTH_SHORT).show();
            else
            {
                Toast.makeText(getApplicationContext(), "Update successful", Toast.LENGTH_SHORT).show();

                json_string = result;

                // pass the json data to dashboard activity
                Intent intent = new Intent(getApplicationContext(), SigningIn.class);

                // parsing the json string
                try {
                    jsonObject = new JSONObject(json_string);
                    Email = jsonObject.getString("EmailId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent.putExtra("EmailId", Email);
                startActivity(intent);
                finish();
            }
        }
    }

}
