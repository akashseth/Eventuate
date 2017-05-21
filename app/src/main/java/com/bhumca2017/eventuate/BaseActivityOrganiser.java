package com.bhumca2017.eventuate;

import android.app.Activity;
import android.app.ActivityManager;
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
import android.util.Log;
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
import java.util.List;

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

       // if(SetDrawerFlag.getDrawerFlagProfileInput())
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
            //intent.putExtra("json_data", sessionOrganiser.getJSonString());
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_editprofileorganizer)
        {
            // Go to Edit Profile - Organizer Form
            SetDrawerFlag.setDrawerFlagProfile(true);
            Intent intent = new Intent(getApplicationContext(), EditProfileOrganizer.class);
          //  intent.putExtra("json_data", sessionOrganiser.getJSonString());
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_editeventdetails)
        {
            // Go to Edit Event Details Form
            SetDrawerFlag.setDrawerFlagEvent(true);
            Intent intent = new Intent(getApplicationContext(), EditEventDetails.class);
           // intent.putExtra("json_data", sessionOrganiser.getJSonString());
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_editexpenditure)
        {
            // Go to edit Expenditure
            Intent intent = new Intent(getApplicationContext(), EditExpenditure.class);
            //intent.putExtra("json_data", sessionOrganiser.getJSonString());
            SetDrawerFlag.setDrawerFlagExpenditure(true);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.nav_mybookings)
        {
            // Go to My Bookings
           Intent myBookings = new Intent(this,MyBookings.class);
            startActivity(myBookings);
            finish();
        }
       /* else if (id == R.id.nav_sendinvitations)
        {
            // send invitations, link to gmail is to be provided
        }*/
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

                    // Sign Out and go to Login activity

                    sessionOrganiser.logout();

                    Intent intent = new Intent(getContext(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.dialog_signout_no :     dismiss();
                    break;

                default :                         break;
            }

            dismiss();
        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();

            Intent homeIntent = new Intent(this,DashboardOrganiseActivity.class);
            startActivity(homeIntent);

    }
}

