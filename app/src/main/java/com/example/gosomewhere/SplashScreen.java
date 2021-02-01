package com.example.gosomewhere;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//Activity to provide a user with a visual of what app they have opened
//rather than directly throwing them into the maps activity
public class SplashScreen extends AppCompatActivity {

    //Called once activity has been started
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ProgressBar progressBar = findViewById(R.id.progressBar);

        final int ACCESS_REQUEST_LOCATION = 0;


        //Checks that app has location permissions from user
        //If no permissions granted it will ask for permissions
        //If user denies to give permissions then app will carry on to next activity but will have location features disabled
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Log.e(getResources().getString(R.string.app_name), "No location permission. Asking for permission...");
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_REQUEST_LOCATION);

        } else {
            Log.i(getResources().getString(R.string.app_name), "Permission granted");
            Toast.makeText(this, R.string.toast_location_granted, Toast.LENGTH_SHORT).show();
            startMain();
        }
    }

    //Executes once the user has said whether they allow or deny permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        startMain();
    }

    //Method to move to the maps activity
    public void startMain() {
        Thread splash = new Thread()
        {
            public void run()
            {
                try
                {
                    sleep(5000); //Sleeps on splash screen for 5 seconds before moving to main app

                    Intent i = new Intent(getBaseContext(), MapsActivity.class);
                    startActivity(i);

                    //finish prevents user from going back to the splash screen
                    finish();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        };

        splash.start();
    }
}
