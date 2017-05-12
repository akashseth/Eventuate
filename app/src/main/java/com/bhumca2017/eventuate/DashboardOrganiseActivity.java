package com.bhumca2017.eventuate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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
import java.util.Calendar;

public class DashboardOrganiseActivity extends BaseActivityOrganiser
{

    TextView eventType, eventDate, eventTime, budgetLeft, organizerName, organizerEmail;
    Button eventSuccess;


    String OrganizerEmail, OrganizerName, EventType;
    Integer EventDateDayOfMonth, EventDateMonth, EventDateYear, EventTimeFromHours, EventTimeFromMinutes, EventTimeToHours, EventTimeToMinutes, EventBudget, TotalExpenditure, BudgetLeft;

    String json_string;
    JSONObject jsonObject;

    SessionOrganiser sessionOrganiser;

    //boolean drawer_flag_profile, drawer_flag_event, drawer_flag_profile_input, drawer_flag_event_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_organise);

        ArrayList<Services> servicesItem=new ArrayList<>();
        servicesItem.add(new Services("Hotel",1,R.drawable.hotel));
        servicesItem.add(new Services("Catering",2,R.drawable.catering));
        servicesItem.add(new Services("Lawns & Banquettes",3,R.drawable.lawns));
        servicesItem.add(new Services("Decorator",4,R.drawable.decorator));
        servicesItem.add(new Services("Tenting",5,R.drawable.tenting));
        servicesItem.add(new Services("Travel",6,R.drawable.travel));
        servicesItem.add(new Services("Restaurants",7,R.drawable.restaurants));
        servicesItem.add(new Services("Video/Photograph",8,R.drawable.video));
        servicesItem.add(new Services("Musical",9,R.drawable.musical));

        sessionOrganiser = new SessionOrganiser(this);
        Button createEventButton = (Button)findViewById(R.id.create_event_button);
        if(sessionOrganiser.getEventType().length()==0){
            findViewById(R.id.event_details_text).setVisibility(View.GONE);
            createEventButton.setVisibility(View.VISIBLE);
        }

        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editEventIntent = new Intent(DashboardOrganiseActivity.this,EditEventDetails.class);
                startActivity(editEventIntent);
                finish();
            }
        });

        GridView servicesGridView=(GridView)findViewById(R.id.dashbord_item);
        final ServicesItemAdapter adapter=new ServicesItemAdapter(this,servicesItem);
        servicesGridView.setAdapter(adapter);

        servicesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(getApplicationContext(),OrganiserAvailabilityActivity.class);
                intent.putExtra("serviceId",adapter.getItem(position).getServiceId());
                // Log.e("id","this "+adapter.getItem(position).getServiceId());
                startActivity(intent);
                finish();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventType = (TextView) findViewById(R.id.dashboard_eventtype_text);
        eventDate = (TextView) findViewById(R.id.dashboard_eventdate_text);
        eventTime = (TextView) findViewById(R.id.dashboard_eventtime_text);
        budgetLeft = (TextView) findViewById(R.id.dashboard_eventremainingbudget_text);

        eventSuccess = (Button) findViewById(R.id.dashboard_eventsuccess_button);
        eventSuccess.setEnabled(false);




        if(SetDrawerFlag.getDrawerFlagEventInput())
        {
               // OrganizerEmail = jsonObject.getString("EmailId");
                EventType = sessionOrganiser.getEventType();
                EventDateDayOfMonth = sessionOrganiser.getEventDateDayOfMonth();
                EventDateMonth = sessionOrganiser.getEventDateMonth();
                EventDateYear = sessionOrganiser.getEventDateYear();
                EventTimeFromHours = sessionOrganiser.getEventTimeFromHours();
                EventTimeFromMinutes = sessionOrganiser.getEventTimeFromMinutes();
                EventTimeToHours = sessionOrganiser.getEventTimeToHours();
                EventTimeToMinutes = sessionOrganiser.getEventTimeToMinutes();
                EventBudget = sessionOrganiser.getBudget();
               // TotalExpenditure = jsonObject.getInt("TotalExpenditure");
                BudgetLeft = sessionOrganiser.getBudgetLeft();



            // if event info exists, then highlight the menu items
           // viewExpenditure.setEnabled(true);
            //myBookings.setEnabled(true);

            // setting the dashboard data as per the received json data

            java.util.Calendar c = java.util.Calendar.getInstance();
            int DayOfMonth = c.get(java.util.Calendar.DAY_OF_MONTH);
            int Month = c.get(java.util.Calendar.MONTH);
            int Year = c.get(java.util.Calendar.YEAR);
            int TimeHours = c.get(Calendar.HOUR_OF_DAY);
            int TimeMinutes = c.get(Calendar.MINUTE);

            // making the "Event Success" button active after event is over
            if(Year == EventDateYear)
            {
                if(Month == EventDateMonth)
                {
                    if(DayOfMonth == EventDateDayOfMonth)
                    {
                        if(TimeHours == EventTimeToHours)
                        {
                            if(TimeMinutes >= EventTimeToMinutes)
                                eventSuccess.setEnabled(true);
                        }
                        else if(TimeHours > EventTimeFromHours)
                            eventSuccess.setEnabled(true);
                    }
                    else if(DayOfMonth > EventDateDayOfMonth)
                        eventSuccess.setEnabled(true);
                }
                else if(Month > EventDateMonth)
                    eventSuccess.setEnabled(true);
            }
            else if(Year > EventDateYear)
                eventSuccess.setEnabled(true);

            eventType.setText(EventType);

            String date;
            if(EventDateDayOfMonth < 10)
                date = "0"+EventDateDayOfMonth.toString() + "/" + EventDateMonth.toString() + "/" + EventDateYear.toString();
            else
                date = EventDateDayOfMonth.toString() + "/" + EventDateMonth.toString() + "/" + EventDateYear.toString();
            if(EventDateMonth < 10)
                date = EventDateDayOfMonth.toString() + "/" + "0"+EventDateMonth.toString() + "/" + EventDateYear.toString();
            else
                date = EventDateDayOfMonth.toString() + "/" + EventDateMonth.toString() + "/" + EventDateYear.toString();
            eventDate.setText(date);

            String timeFrom, timeTo;
            if(EventTimeFromHours < 10)
            {
                if(EventTimeFromMinutes < 10)
                    timeFrom = "0"+EventTimeFromHours.toString() + ":" + "0"+EventTimeFromMinutes.toString();
                else
                    timeFrom = "0"+EventTimeFromHours.toString() + ":" + EventTimeFromMinutes.toString();
            }
            else
            {
                if(EventTimeFromMinutes < 10)
                    timeFrom = EventTimeFromHours.toString() + ":" + "0"+EventTimeFromMinutes.toString();
                else
                    timeFrom = EventTimeFromHours.toString() + ":" + EventTimeFromMinutes.toString();
            }

            if(EventTimeToHours < 10)
            {
                if(EventTimeToMinutes < 10)
                    timeTo = "0"+EventTimeToHours.toString() + ":" + "0"+EventTimeToMinutes.toString();
                else
                    timeTo = "0"+EventTimeToHours.toString() + ":" + EventTimeToMinutes.toString();
            }
            else
            {
                if(EventTimeToMinutes < 10)
                    timeTo = EventTimeToHours.toString() + ":" + "0"+EventTimeToMinutes.toString();
                else
                    timeTo = EventTimeToHours.toString() + ":" + EventTimeToMinutes.toString();
            }

            String time = timeFrom + " - " + timeTo;
            eventTime.setText(time);

           // String LeftBudget = "Left : Rs. " + BudgetLeft.toString();
            budgetLeft.setText("Left : Rs. " + new SessionOrganiser(this).getBudgetLeft());
        }



       /* if(!(SetDrawerFlag.getDrawerFlagProfile() || SetDrawerFlag.getDrawerFlagEvent() || SetDrawerFlag.getDrawerFlagExpenditure()))
            Toast.makeText(this, "WELCOME "+OrganizerEmail, Toast.LENGTH_SHORT).show();

        SetDrawerFlag.setDrawerFlagExpenditure(false);*/
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to exit from app?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               ActivityCompat.finishAffinity(DashboardOrganiseActivity.this);
            }
        });
        builder.setNegativeButton("NO",null);
        builder.show();
    }
}
