package com.bhumca2017.eventuate;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Splash Screen Timer
        int SPLASH_TIME_OUT=3000;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final SessionOrganiser sessionOrganiser = new SessionOrganiser(this);

        new Handler().postDelayed(new Runnable() {
            // showing splash screen with a timer
            @Override
            public void run()   {
                // This method will be executed once the timer is over
                // Starting the next activity
                if(sessionOrganiser.isLoggedIn()) {
                    Intent i = new Intent(SplashScreen.this, DashboardOrganiseActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                } else if(new SessionServices(SplashScreen.this).isLoggedIn()) {
                    Intent i = new Intent(SplashScreen.this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                }else {

                    Intent i = new Intent(SplashScreen.this, Login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }

            }
        }, SPLASH_TIME_OUT);
    }
}
