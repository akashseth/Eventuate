package com.bhumca2017.eventuate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class BaseActivity extends AppCompatActivity {

    private String[] mDrawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    public DrawerLayout fullLayout;
    public FrameLayout frameLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;
    SessionServices sessionServices;

    @Override
    public void setContentView(int layoutResID) {

        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        frameLayout = (FrameLayout) fullLayout.findViewById(R.id.content_frame);

       sessionServices = new SessionServices(this);
        TextView nameTextView = (TextView)fullLayout.findViewById(R.id.name);
        TextView emailIdTextView = (TextView)fullLayout.findViewById(R.id.email_id);

        nameTextView.setText(sessionServices.getFullName());
        emailIdTextView.setText(sessionServices.getEmailId());


        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        super.setContentView(fullLayout);

        mDrawerItems = new String[4];
        mDrawerItems[0]="Edit Profile";
        mDrawerItems[1]="View Bookings";
        mDrawerItems[2]="Home";
        mDrawerItems[3]="Logout";
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mDrawerItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                       switch (position) {
                           case 0:
                               Intent editProfile=new Intent(getApplicationContext(),EditProfileServicesActivity.class);
                               startActivity(editProfile);
                               finish();
                               return;
                           case 1:
                               Intent BookingsIntent=new Intent(getApplicationContext(),ServicesBookedActivity.class);
                               startActivity(BookingsIntent);
                               finish();
                               return;
                           case 2:
                               Intent dashboard=new Intent(getApplicationContext(),DashboardActivity.class);
                               startActivity(dashboard);
                               finish();
                               return;
                           case 3:
                               CustomDialogSignout cds = new CustomDialogSignout(BaseActivity.this);
                               cds.show();
                               return;
                       }
                   }
               });

        setupToolbar();
        setupDrawerToggle();
        // Set the list's click listener
        // mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.app_name, R.string.app_name);
        //This is necessary to change the icon of the Drawer Toggle upon state change.
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(this,DashboardActivity.class);
        startActivity(homeIntent);
        finish();
    }

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

                    sessionServices.logout();

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
}
