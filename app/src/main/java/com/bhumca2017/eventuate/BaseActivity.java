package com.bhumca2017.eventuate;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public class BaseActivity extends AppCompatActivity {

    private String[] mDrawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    public DrawerLayout fullLayout;
    public FrameLayout frameLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    Toolbar toolbar;

    @Override
    public void setContentView(int layoutResID) {

        fullLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_base, null);
        frameLayout = (FrameLayout) fullLayout.findViewById(R.id.content_frame);

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
                               return;
                           case 1:
                               Intent BookingsIntent=new Intent(getApplicationContext(),ServicesBookedActivity.class);
                               startActivity(BookingsIntent);
                               return;
                           case 2:
                               Intent dashboard=new Intent(getApplicationContext(),DashboardActivity.class);
                               startActivity(dashboard);
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
}
