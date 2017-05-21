package com.bhumca2017.eventuate;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditExpenditure extends BaseActivityOrganiser {

    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    Integer sno; String date; Integer amount; String details;

    TextView totalBudget, leftBudget, expenditure;
    Button viewExpenditure, addExpenditure;

    String EmailId;
    Integer TotalBudget, LeftBudget;

    ExpenditureDetailsAdapter expenditureDetailsAdapter;
    ListView expenditureDetails;
    SessionOrganiser sessionOrganiser;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expenditure);

        totalBudget = (TextView)findViewById(R.id.totalbudget);
        leftBudget = (TextView)findViewById(R.id.leftbudget);
        viewExpenditure = (Button)findViewById(R.id.viewExpenditure_button);
        addExpenditure = (Button)findViewById(R.id.addExpenditure_button);
        expenditure = (TextView)findViewById(R.id.expenditure_text);
        expenditure.setVisibility(View.GONE);
        expenditureDetails = (ListView)findViewById(R.id.expendituredetails_list);
        expenditureDetails.setVisibility(View.GONE);



        sessionOrganiser = new SessionOrganiser(this);
        totalBudget.setText("Rs. "+sessionOrganiser.getBudget());
        leftBudget.setText("Rs. "+sessionOrganiser.getBudgetLeft());
        EmailId = sessionOrganiser.getOrganiserEmail();
    }

    public void addExpenditure(View view)
    {
        Bundle info = new Bundle();
        info.putString("method", "insert");
        info.putString("Email", EmailId);
        Intent intent = new Intent(this, EditSelectedExpenditure.class);
        intent.putExtras(info);
        startActivity(intent);
        finish();
    }

    public void viewExpenditure(View view)
    {
        BackgroundTask_extractExpenditure backgroundTask_extractExpenditure = new BackgroundTask_extractExpenditure();
        backgroundTask_extractExpenditure.execute();
    }


    // background tasks for extracting the expenditure details

    public class BackgroundTask_extractExpenditure extends AsyncTask<Void, Void, String> {

        String url_extractExpenditure;

        @Override
        protected void onPreExecute()
        {
            // url of php script for extracting the expenditure details
            progressDialog = new ProgressDialog(EditExpenditure.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            url_extractExpenditure=getString(R.string.ip_address)+"/eventuate/extract_expenditure.php";
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // connecting to the url
                URL url = new URL(url_extractExpenditure);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("EmailId", "UTF-8") + "=" + URLEncoder.encode(EmailId, "UTF-8");
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
            //Log.e("expend",result);
            progressDialog.hide();
            if((result.equals("No expenditure found")))
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            else
            {
                // making the expenditure table visible if the expenditure details exist in the database
                expenditure.setVisibility(View.VISIBLE);
                expenditureDetails.setVisibility(View.VISIBLE);
                List<ExpenditureDetails> list = new ArrayList();

                // parsing the json received
                try {
                    jsonObject = new JSONObject(result);
                    jsonArray = jsonObject.getJSONArray("expenditure");
                    int count = 0;

                    while(count<jsonArray.length())
                    {
                        JSONObject JO = jsonArray.getJSONObject(count);

                        sno = JO.getInt("sno");
                        date = JO.getString("date");
                        amount = JO.getInt("amount");
                        details = JO.getString("details");

                        ExpenditureDetails expenditureDetails = new ExpenditureDetails(EmailId, sno, date, amount, details);
                        list.add(expenditureDetails);

                        count++;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                expenditureDetailsAdapter = new ExpenditureDetailsAdapter(EditExpenditure.this, R.layout.row_layout_expendituredetails,list);
                expenditureDetails.setAdapter(expenditureDetailsAdapter);
                expenditureDetails.setEmptyView(findViewById(R.id.no_expenditure_avail));
            }
        }
    }


    public class ExpenditureDetailsAdapter extends ArrayAdapter<ExpenditureDetails> {


        Bundle info = new Bundle();
        int itemPosition =-1;

        public ExpenditureDetailsAdapter(Context context, int resource,List<ExpenditureDetails>list) {
            super(context,resource,list);
        }




        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            row = convertView;
            final ExpenditureHolder expenditureHolder;
            if (row == null)       // if the row doesn't exist
            {
                LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = layoutInflater.inflate(R.layout.row_layout_expendituredetails, parent, false);
                expenditureHolder = new ExpenditureHolder();
                expenditureHolder.tx_date = (TextView) row.findViewById(R.id.expenditureDate);
                expenditureHolder.tx_amount = (TextView) row.findViewById(R.id.expenditureAmount);
                expenditureHolder.tx_details = (TextView) row.findViewById(R.id.expenditureDetails);
                expenditureHolder.edit_button = (Button) row.findViewById(R.id.expenditureEdit_button);
                expenditureHolder.delete_button = (Button) row.findViewById(R.id.expenditureDelete_button);
                row.setTag(expenditureHolder);
            } else {
                expenditureHolder = (ExpenditureHolder) row.getTag();
            }

            // setting the resources for the text view
            final ExpenditureDetails expenditureDetails = getItem(position);
            expenditureHolder.tx_date.setText(expenditureDetails.getDate());
            expenditureHolder.tx_amount.setText("Rs. "+expenditureDetails.getAmount());
            expenditureHolder.tx_details.setText(expenditureDetails.getDetails());

            row.findViewById(R.id.expenditureEdit_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // perform edit expenditure

                    info.putString("method", "update");
                    info.putString("Email", expenditureDetails.getEmail());
                    info.putInt("Sno", expenditureDetails.getSno());
                    info.putString("Date", expenditureDetails.getDate());
                    info.putString("Amount", expenditureDetails.getAmount());
                    info.putString("Details", expenditureDetails.getDetails());

                    Intent intent = new Intent(getContext(), EditSelectedExpenditure.class);
                    intent.putExtras(info);
                    startActivity(intent);
                    finish();
                }
            });
            final int pos = position;
            row.findViewById(R.id.expenditureDelete_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // perform delete expenditure
                    itemPosition = pos;
                    CustomDialogDeleteExpenditure cdde = new CustomDialogDeleteExpenditure(getContext(), expenditureDetails.getDate(), expenditureDetails.getDetails(), expenditureDetails.getSno());
                    cdde.show();

                }
            });


            return row;
        }

        class ExpenditureHolder {
            TextView tx_date, tx_amount, tx_details;
            Button edit_button, delete_button;
        }


        // Class for the custom dialog box for the delete expenditure

        public class CustomDialogDeleteExpenditure extends Dialog implements View.OnClickListener {
            public Activity a;
            public Dialog d;
            public Button yes, no;
            TextView deletepermission;
            String date, details;
            Integer sno;


            public CustomDialogDeleteExpenditure(Context c, String date, String details, Integer sno) {
                super(c);
                this.date = date;
                this.details = details;
                this.sno = sno;

            }

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                requestWindowFeature(Window.FEATURE_NO_TITLE);
                setContentView(R.layout.custom_dialog_deleteexpenditure);
                deletepermission = (TextView) findViewById(R.id.txt_deleteentrypermission);
                deletepermission.setText("Do you really want to delete the expenditure entry '" + date + " : " + details + "' ?");
                yes = (Button) findViewById(R.id.dialog_deleteexpenditure_yes);
                no = (Button) findViewById(R.id.dialog_deleteexpenditure_no);
                yes.setOnClickListener(this);
                no.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.dialog_deleteexpenditure_yes:    // backgroundtask for delete

                        new BackgroundTask_deleteExpenditure(sno).execute();

                        break;

                    case R.id.dialog_deleteexpenditure_no:
                        dismiss();
                        break;

                    default:
                        break;
                }

                dismiss();
            }
        }

        // background tasks for deleting the expenditure entry

        public class BackgroundTask_deleteExpenditure extends AsyncTask<Void, Void, String> {

            String url_deleteExpenditure;
            Integer sno;

            BackgroundTask_deleteExpenditure(Integer sno) {
                this.sno = sno;
            }

            @Override
            protected void onPreExecute() {
                // url of php script for delete the expenditure
                url_deleteExpenditure = getString(R.string.ip_address) + "/eventuate/delete_expenditure.php";
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {

                try {
                    // connecting to the url
                    URL url = new URL(url_deleteExpenditure);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream OS = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                    String data = URLEncoder.encode("EmailId", "UTF-8") + "=" + URLEncoder.encode(EmailId, "UTF-8") + "&" +
                            URLEncoder.encode("Sno", "UTF-8") + "=" + sno;
                    bufferedWriter.write(data);
                    bufferedWriter.flush();
                    bufferedWriter.close();
                    OS.close();


                    InputStream IS = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS));

                    String line = "";
                    StringBuilder stringBuilder = new StringBuilder();  // returns the JSON response

                    while ((line = bufferedReader.readLine()) != null)
                        stringBuilder.append((line + "\n"));

                    bufferedReader.close();
                    IS.close();
                    httpURLConnection.disconnect();

                    // returning a json object string containing email id of the user
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


                if(itemPosition!=-1){
                    sessionOrganiser.updateBudgetLeft(sessionOrganiser.getBudgetLeft()+Integer.parseInt(getItem(itemPosition).getAmount()));
                    leftBudget.setText("Rs. "+ sessionOrganiser.getBudgetLeft());
                    remove(getItem(itemPosition));
                    if(getCount()==0){
                        findViewById(R.id.no_expenditure_avail).setVisibility(View.VISIBLE);
                    }
                }
           /* String EmailId="";

            // parsing the received json string

            try {
                jsonObject = new JSONObject(result);

                EmailId = jsonObject.getString("EmailId");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent i = new Intent(getApplicationContext(), SigningIn.class);
            Bundle info = new Bundle();
            info .putString("EmailId", EmailId);
            i.putExtras(info);
            startActivity(i);
            finish();*/
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        totalBudget.setText("Rs. "+sessionOrganiser.getBudget());
        leftBudget.setText("Rs. "+sessionOrganiser.getBudgetLeft());
       // new BackgroundTask_extractExpenditure().execute();
    }
}
