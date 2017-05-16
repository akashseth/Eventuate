package com.bhumca2017.eventuate;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.HashMap;
import java.util.List;

public class ServicesBookedActivity extends BaseActivity {

    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    ListView bookings;
    SessionServices sessionServices;
    private static String PERFORM_ACTION_BOOKING_URL;
    private int mBookingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_booked);

        PERFORM_ACTION_BOOKING_URL = getString(R.string.ip_address)+"/Eventuate/Services/UpdateBookingStatus.php";

        sessionServices = new SessionServices(this);

        bookings = (ListView) findViewById(R.id.bookings_list);

        new BackgroundTask_viewBookings().execute();
    }


   /* public void gotoDrawer(View view)
    {
        finish();
    }*/


    public class BookingDetailsAdapter extends ArrayAdapter {

        List list = new ArrayList();

        public BookingDetailsAdapter(Context context, int resource) {
            super(context, resource);
        }

        public void add(BookingDetailsServices object) {
            list.add(object);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public BookingDetailsServices getItem(int position) {
            return (BookingDetailsServices) list.get(position);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row;
            row = convertView;
            final BookingDetailsHolder bookingDetailsHolder;
            if(row==null)       // if the row doesn't exist
            {
                LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = layoutInflater.inflate(R.layout.rowlayout_services_booked, parent, false);
                bookingDetailsHolder = new BookingDetailsHolder();
                bookingDetailsHolder.tx_date = (TextView) row.findViewById(R.id.bookingDate);
                bookingDetailsHolder.tx_nameserviceprovider = (TextView) row.findViewById(R.id.bookingServiceProviderName);
                bookingDetailsHolder.tx_servicetype = (TextView) row.findViewById(R.id.bookingServiceType);
                bookingDetailsHolder.tx_servicespecification = (TextView) row.findViewById(R.id.bookingServiceSpecification);
                bookingDetailsHolder.tx_amountpaid = (TextView) row.findViewById(R.id.bookingAmountPaid);
                bookingDetailsHolder.tx_amountdue = (TextView) row.findViewById(R.id.bookingAmountDue);
                bookingDetailsHolder.tx_bookingStatus = (TextView)row.findViewById(R.id.booking_status);
                bookingDetailsHolder.tx_quantity = (TextView)row.findViewById(R.id.quantity_booking);
                row.setTag(bookingDetailsHolder);
            }
            else
            {
                bookingDetailsHolder = (BookingDetailsHolder) row.getTag();
            }

            // setting the resources for the text view
            final BookingDetailsServices bookingDetailsOrganizer =  this.getItem(position);
            bookingDetailsHolder.tx_date.setText(bookingDetailsOrganizer.getdate());
            bookingDetailsHolder.tx_nameserviceprovider.setText(bookingDetailsOrganizer.getNameUser());
            bookingDetailsHolder.tx_servicetype.setText(bookingDetailsOrganizer.getTypeService());
            bookingDetailsHolder.tx_servicespecification.setText(bookingDetailsOrganizer.getServiceSpecification());
            bookingDetailsHolder.tx_amountpaid.setText("Rs. "+bookingDetailsOrganizer.getAmountPaid().toString());
            bookingDetailsHolder.tx_amountdue.setText("Rs. "+bookingDetailsOrganizer.getAmountDue().toString());
            bookingDetailsHolder.tx_quantity.setText(bookingDetailsOrganizer.getQuantity().toString());
            if(bookingDetailsOrganizer.getBookingStatus().equals("confirmed")){

                bookingDetailsHolder.tx_bookingStatus.setText("Confirmed by service provider");
                bookingDetailsHolder.tx_bookingStatus.setTextColor(Color.parseColor("#169613"));
            } else  if(bookingDetailsOrganizer.getBookingStatus().equals("pending")){

                LinearLayout textLinearLayout = (LinearLayout)row.findViewById(R.id.booking_text_linear);
                LinearLayout buttonLinearLayout = (LinearLayout)row.findViewById(R.id.booking_button_linear);
                textLinearLayout.setVisibility(View.GONE);
                buttonLinearLayout.setVisibility(View.VISIBLE);
            } else {

                bookingDetailsHolder.tx_bookingStatus.setText("Declined by service provider");
                bookingDetailsHolder.tx_bookingStatus.setTextColor(Color.parseColor("#c60000"));
            }

            final Button cancelButton = (Button)row.findViewById(R.id.cancel_button);
            final Button confirmButton = (Button)row.findViewById(R.id.confirm_button);

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showCancelOrConfirmAlert(cancelButton.getText().toString());
                }
            });

            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        mBookingId = getItem(position).getBookingId();
                    showCancelOrConfirmAlert(confirmButton.getText().toString());
                }
            });

            return row;
        }

        class BookingDetailsHolder
        {
            TextView tx_date, tx_nameserviceprovider, tx_servicetype, tx_servicespecification, tx_amountpaid, tx_amountdue,tx_bookingStatus,tx_quantity;
        }
    }

    private void showCancelOrConfirmAlert(String actionType){

        actionType = actionType.toLowerCase();
        final String bookingStatus = actionType;
        AlertDialog.Builder builder = new AlertDialog.Builder(ServicesBookedActivity.this);
        builder.setTitle("Order Confirmation");
        if(actionType.equals("cancel")) {
            builder.setTitle("Order Cancellation");
        }


        builder.setMessage("Are you sure? Do you want to "+actionType+" this order?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                new PerformActionOnBookingAsyncTask().execute(bookingStatus);
            }
        });
        builder.setNegativeButton("NO",null);
        builder.show();
    }

    private HashMap<String, String> mGetPostDataCancelOrConfirmBooking(String actionType){

        actionType = actionType.equals("cancel")?  "declined" : "confirmed";
        HashMap<String ,String> postData = new HashMap<>();
        postData.put("bookingId", mBookingId+"");
        postData.put("bookingStatus",actionType);
        return postData;
    }

    private class PerformActionOnBookingAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... bookingStatus) {

            ServerRequestHandler requestHandler=new ServerRequestHandler();

            try {
                String jsonResponse = requestHandler.sendPostRequest(PERFORM_ACTION_BOOKING_URL,mGetPostDataCancelOrConfirmBooking(bookingStatus[0]));
                return jsonResponse;
            } catch (IOException e) {
               // Log.e(LOG_TAG,"Problem while requesting get method",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);

            new BackgroundTask_viewBookings().execute();

        }
    }

    // background tasks for extracting the booking details of the organizer

    public class BackgroundTask_viewBookings extends AsyncTask<Void, Void, String> {

        String url_viewBookings;

        @Override
        protected void onPreExecute()
        {
            // url of php script for extracting the expenditure details
            url_viewBookings=getString(R.string.ip_address)+"/Eventuate/Services/ViewBookingsServices.php";
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                // connecting to the url
                URL url = new URL(url_viewBookings);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                String data = URLEncoder.encode("serviceProviderId", "UTF-8") + "=" + URLEncoder.encode(sessionServices.getUserId()+"", "UTF-8");
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

                // returning a json object string containing the booking details of the organizer
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

            // Log.e("resultJson",result);
            if((result.equals("No Bookings")))
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            else
            {
                json_string = result;

                try {
                    jsonObject = new JSONObject(json_string);
                    jsonArray = jsonObject.getJSONArray("bookings");

                    String date, userName, serviceType, serviceSpecification;
                    Integer sno, amountPaid, amountDue;
                    final BookingDetailsAdapter bookingDetailsAdapter = new BookingDetailsAdapter(ServicesBookedActivity.this, R.layout.rowlayout_bookings_organizer);

                    bookings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                            Intent bookingDetailsIntent = new Intent(ServicesBookedActivity.this,BookingDetailsServicesActivity.class);
                            bookingDetailsIntent.putExtra("quantity",bookingDetailsAdapter.getItem(position).getQuantity());
                            bookingDetailsIntent.putExtra("serviceAvailabilityId",bookingDetailsAdapter.getItem(position).getSno());
                            bookingDetailsIntent.putExtra("availabilityName",bookingDetailsAdapter.getItem(position).getServiceSpecification());
                            bookingDetailsIntent.putExtra("emailId",bookingDetailsAdapter.getItem(position).getUserEmail());
                            bookingDetailsIntent.putExtra("bookingDate",bookingDetailsAdapter.getItem(position).getBookingDate());
                            startActivity(bookingDetailsIntent);
                            finish();

                        }
                    });
                    int count=0;
                    while(count<jsonArray.length())
                    {
                        JSONObject JO = jsonArray.getJSONObject(count);

                        sno = JO.getInt("sno");
                        date = JO.getString("date");
                        serviceType = JO.getString("type_service");
                        userName = JO.getString("name_user");
                        serviceSpecification = JO.getString("service_specification");
                        amountPaid = JO.getInt("amount_paid");
                        amountDue = JO.getInt("amount_due");
                        String userEmail = JO.getString("user_email");
                        String bookingStatus = JO.getString("bookingStatus");
                        int quantity = JO.getInt("quantity");
                        int serviceId = JO.getInt("serviceId");
                        int bookingId = JO.getInt("booking_id");
                        String bookingDate = JO.getString("bookingDate");
                        BookingDetailsServices bookingDetailsServices = new BookingDetailsServices(sno, date, serviceType,
                                userName, serviceSpecification, amountPaid, amountDue,userEmail,
                                bookingStatus,quantity,serviceId,bookingId,bookingDate);
                        bookingDetailsAdapter.add(bookingDetailsServices);

                        count++;
                    }


                    bookings.setAdapter(bookingDetailsAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
