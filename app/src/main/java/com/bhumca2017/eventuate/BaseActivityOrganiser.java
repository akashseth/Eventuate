package com.bhumca2017.eventuate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
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

public class BaseActivityOrganiser extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    String json_string;
    JSONObject jsonObject;
    String OrganizerEmail, OrganizerName;
    TextView  organizerName, organizerEmail;
    MenuItem viewExpenditure, myBookings;
    boolean  drawer_flag_profile_input, drawer_flag_event_input;
    SessionOrganiser sessionOrganiser;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {

        DrawerLayout fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base_oraganiser, null);
        FrameLayout frameLayout = (FrameLayout) fullLayout.findViewById(R.id.content_frame);

        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        super.setContentView(fullLayout);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarr);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        organizerName = (TextView) header.findViewById(R.id.drawer_organizer_name);
        organizerEmail = (TextView) header.findViewById(R.id.drawer_organizer_email);

        Menu navMenu = navigationView.getMenu();
        viewExpenditure = navMenu.findItem(R.id.nav_editexpenditure);
        myBookings = navMenu.findItem(R.id.nav_mybookings);
        viewExpenditure.setEnabled(false);
        myBookings.setEnabled(false);

        // extracts the user and event details from json string
        //json_string = getIntent().getExtras().getString("json_data");


            sessionOrganiser = new SessionOrganiser(this);
            OrganizerEmail = sessionOrganiser.getOrganiserEmail();
            drawer_flag_profile_input=sessionOrganiser.getDrawerFlagProfileInput();
            drawer_flag_event_input=sessionOrganiser.getDrawerFlagEvenInput();


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
                OrganizerEmail = sessionOrganiser.getOrganiserEmail();
                OrganizerName = sessionOrganiser.getOrganiserName();

            organizerName.setText(OrganizerName);
            organizerEmail.setText(OrganizerEmail);
        }

        if(SetDrawerFlag.getDrawerFlagEventInput()) {

            // if event info exists, then highlight the menu items
            viewExpenditure.setEnabled(true);
            myBookings.setEnabled(true);
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard)
        {

            Intent intent = new Intent(getApplicationContext(), DashboardOrganiseActivity.class);
            intent.putExtra("json_data", sessionOrganiser.getJSonString());
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_editprofileorganizer)
        {
            // Go to Edit Profile - Organizer Form
            SetDrawerFlag.setDrawerFlagProfile(true);
            Intent intent = new Intent(getApplicationContext(), EditProfileOrganizer.class);
            intent.putExtra("json_data", sessionOrganiser.getJSonString());
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_editeventdetails)
        {
            // Go to Edit Event Details Form
            SetDrawerFlag.setDrawerFlagEvent(true);
            Intent intent = new Intent(getApplicationContext(), EditEventDetails.class);
            intent.putExtra("json_data", sessionOrganiser.getJSonString());
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_editexpenditure)
        {
            // Go to edit Expenditure
            Intent intent = new Intent(getApplicationContext(), EditExpenditure.class);
            intent.putExtra("json_data", sessionOrganiser.getJSonString());
            SetDrawerFlag.setDrawerFlagExpenditure(true);
            startActivity(intent);
            finish();
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
                finish();
            }
        }
    }


}

