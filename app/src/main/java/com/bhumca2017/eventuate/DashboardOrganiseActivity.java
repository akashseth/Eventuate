package com.bhumca2017.eventuate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

       /* DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        organizerName = (TextView) header.findViewById(R.id.drawer_organizer_name);
        organizerEmail = (TextView) header.findViewById(R.id.drawer_organizer_email);*/

       /* Menu navMenu = navigationView.getMenu();
        viewExpenditure = navMenu.findItem(R.id.nav_editexpenditure);
        myBookings = navMenu.findItem(R.id.nav_mybookings);
        viewExpenditure.setEnabled(false);
        myBookings.setEnabled(false);*/

        // extracts the user and event details from json string
       /*json_string = getIntent().getExtras().getString("json_data");

        // parsing the json string
        try {
            jsonObject = new JSONObject(json_string);
            OrganizerEmail = jsonObject.getString("EmailId");
            drawer_flag_profile_input=jsonObject.getBoolean("OrganizerProfileInput");
            drawer_flag_event_input=jsonObject.getBoolean("OrganizerEventInput");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(drawer_flag_profile_input)
            SetDrawerFlag.setDrawerFlagProfileInput(true);
        else
            SetDrawerFlag.setDrawerFlagProfileInput(false);

        if(drawer_flag_event_input)
            SetDrawerFlag.setDrawerFlagEventInput(true);
        else
            SetDrawerFlag.setDrawerFlagEventInput(false);

        if(SetDrawerFlag.getDrawerFlagProfileInput())
        {
            // parsing the json string
            try {
                jsonObject = new JSONObject(json_string);
                OrganizerEmail = jsonObject.getString("EmailId");
                OrganizerName = jsonObject.getString("OrganizerName");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            organizerName.setText(OrganizerName);
            organizerEmail.setText(OrganizerEmail);
        }*/

        json_string = getIntent().getExtras().getString("json_data");

        if(SetDrawerFlag.getDrawerFlagEventInput())
        {
            // parsing the json string
            try {
                jsonObject = new JSONObject(json_string);
                OrganizerEmail = jsonObject.getString("EmailId");
                EventType = jsonObject.getString("EventType");
                EventDateDayOfMonth = jsonObject.getInt("EventDateDayOfMonth");
                EventDateMonth = jsonObject.getInt("EventDateMonth");
                EventDateYear = jsonObject.getInt("EventDateYear");
                EventTimeFromHours = jsonObject.getInt("EventTimeFromHours");
                EventTimeFromMinutes = jsonObject.getInt("EventTimeFromMinutes");
                EventTimeToHours = jsonObject.getInt("EventTimeToHours");
                EventTimeToMinutes = jsonObject.getInt("EventTimeToMinutes");
                EventBudget = jsonObject.getInt("EventBudget");
                TotalExpenditure = jsonObject.getInt("TotalExpenditure");
                BudgetLeft = jsonObject.getInt("BudgetLeft");

            } catch (JSONException e) {
                e.printStackTrace();
            }

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

            String LeftBudget = "Left : Rs. " + BudgetLeft.toString();
            budgetLeft.setText(LeftBudget);
        }



       /* if(!(SetDrawerFlag.getDrawerFlagProfile() || SetDrawerFlag.getDrawerFlagEvent() || SetDrawerFlag.getDrawerFlagExpenditure()))
            Toast.makeText(this, "WELCOME "+OrganizerEmail, Toast.LENGTH_SHORT).show();

        SetDrawerFlag.setDrawerFlagExpenditure(false);*/
    }

  /*  @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_editprofileorganizer)
        {
            // Go to Edit Profile - Organizer Form
            SetDrawerFlag.setDrawerFlagProfile(true);
            Intent intent = new Intent(getApplicationContext(), EditProfileOrganizer.class);
            intent.putExtra("json_data", json_string);
            startActivity(intent);
        }
        else if (id == R.id.nav_editeventdetails)
        {
            // Go to Edit Event Details Form
            SetDrawerFlag.setDrawerFlagEvent(true);
            Intent intent = new Intent(getApplicationContext(), EditEventDetails.class);
            intent.putExtra("json_data", json_string);
            startActivity(intent);
        }
        else if (id == R.id.nav_editexpenditure)
        {
            // Go to edit Expenditure
            Intent intent = new Intent(getApplicationContext(), EditExpenditure.class);
            intent.putExtra("json_data", json_string);
            SetDrawerFlag.setDrawerFlagExpenditure(true);
            startActivity(intent);
        }
        else if (id == R.id.nav_mybookings)
        {
            // Go to My Bookings
            new BackgroundTask_viewBookings().execute();
        }
        else if (id == R.id.nav_sendinvitations)
        {
            // send invitations, link to gmail is to be provided
        }
        else if (id == R.id.nav_aboutus)
        {
            // Go to About Us
            startActivity(new Intent(this, AboutUs.class));
        }
        else if (id == R.id.nav_signout)
        {
            CustomDialogSignout cds = new CustomDialogSignout(this);
            cds.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void viewService(View view)
    {

    }

    public void eventSuccess(View view)
    {

    }


    // Class for the custom dialog box for the signout permission

    public class CustomDialogSignout extends Dialog implements View.OnClickListener
    {
        public Activity a;
        public Dialog d;
        public Button yes, no;

        public CustomDialogSignout(Context c) {
            super(c);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.custom_dialog_signout);
            yes = (Button) findViewById(R.id.dialog_signout_yes);
            no = (Button) findViewById(R.id.dialog_signout_no);
            yes.setOnClickListener(this);
            no.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            switch(view.getId())
            {
                case R.id.dialog_signout_yes :    // set flags and signout
                    SetDrawerFlag.setDrawerFlagProfile(false);
                    SetDrawerFlag.setDrawerFlagEvent(false);
                    SetDrawerFlag.setDrawerFlagProfileInput(false);
                    SetDrawerFlag.setDrawerFlagEventInput(false);
                    // Sign Out and go to Login activity
                    startActivity(new Intent(getContext(), Login.class));
                    finish();
                    break;

                case R.id.dialog_signout_no :     dismiss();
                    break;

                default :                         break;
            }

            dismiss();
        }
    }


    // background tasks for extracting the booking details of the organizer

    public class BackgroundTask_viewBookings extends AsyncTask<Void, Void, String> {

        String url_viewBookings;

        @Override
        protected void onPreExecute()
        {
            // url of php script for extracting the expenditure details
            url_viewBookings=getString(R.string.ip_address)+"/eventuate/viewbookings_organizer.php";
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

                String data = URLEncoder.encode("EmailId", "UTF-8") + "=" + URLEncoder.encode(OrganizerEmail, "UTF-8");
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

            if((result.equals("No Bookings")))
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            else
            {
                // passing the received json to the next activity for list view display
                Intent i = new Intent(getApplicationContext(), MyBookings.class);
                i.putExtra("json_data", result);
                startActivity(i);
            }
        }
    }

*/

    // function to display the service provider list

    public void displayServiceProviderList(View view)
    {
        String serviceType="";

        switch (view.getId())
        {
            case    R.id.dashboard_service_hotelsrestaurants_button :   serviceType = "hotels_restaurants";
                break;
            case    R.id.dashboard_service_banquettelawns_button :      serviceType = "banquettes_lawns";
                break;
            case    R.id.dashboard_service_lodge_button :               serviceType = "lodging_restrooms";
                break;
            case    R.id.dashboard_service_videophotography_button :    serviceType = "video_photography";
                break;
            case    R.id.dashboard_service_travel_button :              serviceType = "travelling";
                break;
            case    R.id.dashboard_service_catering_button :            serviceType = "catering";
                break;
            case    R.id.dashboard_service_tenting_button :             serviceType = "tenting";
                break;
            case    R.id.dashboard_service_decoration_button :          serviceType = "decoration";
                break;
            case    R.id.dashboard_service_miscellaneous_button :       serviceType = "miscellaneous";
                break;
            default:                                                    break;
        }

        Toast.makeText(this, OrganizerEmail+serviceType, Toast.LENGTH_LONG).show();
    }
}
