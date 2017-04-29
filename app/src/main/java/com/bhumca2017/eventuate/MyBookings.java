package com.bhumca2017.eventuate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyBookings extends AppCompatActivity {

    String json_string;
    JSONObject jsonObject;
    JSONArray jsonArray;
    BookingDetailsAdapter bookingDetailsAdapter;
    ListView bookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        bookings = (ListView) findViewById(R.id.bookings_list);
        bookingDetailsAdapter = new BookingDetailsAdapter(this, R.layout.rowlayout_bookings_organizer);
        bookings.setAdapter(bookingDetailsAdapter);

        json_string = getIntent().getExtras().getString("json_data");

        try {
            jsonObject = new JSONObject(json_string);
            jsonArray = jsonObject.getJSONArray("bookings");

            String date, serviceProviderName, serviceType, serviceSpecification;
            Integer sno, amountPaid, amountDue;
            int count=0;
            while(count<jsonArray.length())
            {
                JSONObject JO = jsonArray.getJSONObject(count);

                sno = JO.getInt("sno");
                date = JO.getString("date");
                serviceType = JO.getString("type_service");
                serviceProviderName = JO.getString("name_serviceprovider");
                serviceSpecification = JO.getString("service_specification");
                amountPaid = JO.getInt("amount_paid");
                amountDue = JO.getInt("amount_due");

                BookingDetailsOrganizer bookingDetailsOrganizer = new BookingDetailsOrganizer(sno, date, serviceType, serviceProviderName, serviceSpecification, amountPaid, amountDue);
                bookingDetailsAdapter.add(bookingDetailsOrganizer);

                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void gotoDrawer(View view)
    {
        finish();
    }


    public class BookingDetailsAdapter extends ArrayAdapter {

        List list = new ArrayList();

        public BookingDetailsAdapter(Context context, int resource) {
            super(context, resource);
        }

        public void add(BookingDetailsOrganizer object) {
            list.add(object);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            row = convertView;
            final BookingDetailsHolder bookingDetailsHolder;
            if(row==null)       // if the row doesn't exist
            {
                LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = layoutInflater.inflate(R.layout.rowlayout_bookings_organizer, parent, false);
                bookingDetailsHolder = new BookingDetailsHolder();
                bookingDetailsHolder.tx_date = (TextView) row.findViewById(R.id.bookingDate);
                bookingDetailsHolder.tx_nameserviceprovider = (TextView) row.findViewById(R.id.bookingServiceProviderName);
                bookingDetailsHolder.tx_servicetype = (TextView) row.findViewById(R.id.bookingServiceType);
                bookingDetailsHolder.tx_servicespecification = (TextView) row.findViewById(R.id.bookingServiceSpecification);
                bookingDetailsHolder.tx_amountpaid = (TextView) row.findViewById(R.id.bookingAmountPaid);
                bookingDetailsHolder.tx_amountdue = (TextView) row.findViewById(R.id.bookingAmountDue);
                row.setTag(bookingDetailsHolder);
            }
            else
            {
                bookingDetailsHolder = (BookingDetailsHolder) row.getTag();
            }

            // setting the resources for the text view
            final BookingDetailsOrganizer bookingDetailsOrganizer = (BookingDetailsOrganizer) this.getItem(position);
            bookingDetailsHolder.tx_date.setText(bookingDetailsOrganizer.getdate());
            bookingDetailsHolder.tx_nameserviceprovider.setText(bookingDetailsOrganizer.getNameServiceProvider());
            bookingDetailsHolder.tx_servicetype.setText(bookingDetailsOrganizer.getTypeService());
            bookingDetailsHolder.tx_servicespecification.setText(bookingDetailsOrganizer.getServiceSpecification());
            bookingDetailsHolder.tx_amountpaid.setText(bookingDetailsOrganizer.getAmountPaid().toString());
            bookingDetailsHolder.tx_amountdue.setText(bookingDetailsOrganizer.getAmountDue().toString());

            return row;
        }

        class BookingDetailsHolder
        {
            TextView tx_date, tx_nameserviceprovider, tx_servicetype, tx_servicespecification, tx_amountpaid, tx_amountdue;
        }
    }
}
