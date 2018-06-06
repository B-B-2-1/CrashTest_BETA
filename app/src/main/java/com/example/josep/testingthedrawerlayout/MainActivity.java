package com.example.josep.testingthedrawerlayout;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
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
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BroadcastReceiver broadcastReceiver;
    private final String GPS_serviceName = "com.example.josep.testingthedrawerlayout.GPS_Service";            // To search if service is already running
    private final String Shake_serviceName = "com.example.josep.testingthedrawerlayout.ShakeMonitor_Service";
    private TextView latitude;
    private TextView longitude;
    private TextView speed;
    private ToggleButton speed_unit_toggle;
    private String Fix;
    private SharedPreferences settingsPrefs;
    private boolean GPS_Setting=true;
    private double final_speed;
   // private LocationManager locationManager;                  // for checking if GPS is enabled or not
    
    @Override
    protected void onResume() {

        super.onResume();

        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Fix = Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).get("Fix")).toString();    // Data is sent as a string seperated by commas
                    String[] GPS_data = Fix.split(",");
                    latitude.setText( "Latitude: " + GPS_data[0]);
                    longitude.setText( "Longitude: " + GPS_data[1]);
                    if(speed_unit_toggle.getText().equals("km/hr")){
                        final_speed= (Double.valueOf(GPS_data[2]))*3.60;
                    }
                    else if(speed_unit_toggle.getText().equals("miles/hr")) {
                        final_speed= (Double.valueOf(GPS_data[2]))*2.23694;
                    }
                    else
                    {
                        final_speed= Double.valueOf(GPS_data[2]);
                    }
                    speed.setText(String.valueOf(final_speed));

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

    }
        @Override
    protected void onCreate(Bundle savedInstanceState) {
       
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latitude= (TextView) findViewById(R.id.textView2);
        longitude= (TextView) findViewById(R.id.textView3);
        speed = (TextView)findViewById(R.id.textView4);
        speed_unit_toggle= (ToggleButton)findViewById(R.id.toggleButton) ;
        settingsPrefs=getSharedPreferences("Settings",MODE_PRIVATE);               ///////////////////////////////////////////////
        GPS_Setting=settingsPrefs.getBoolean("gpsServiceOnorOff",true);
       // locationManager=(LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(isServiceRunning(GPS_serviceName))
        {

        }
        else {
            if(GPS_Setting) {
                Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                startService(i);
            }
            else{
               // show_BackgroundServicesDisabled_dialog();
            }

        }
        if(isServiceRunning(Shake_serviceName)){

        }
        else{
            Intent j = new Intent(getApplicationContext(), ShakeMonitor_Service.class);
            startService(j);

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

        if (id == R.id.nav_camera) {
            Intent backtocontacts=new Intent(MainActivity.this,Contact.class);
            startActivity(backtocontacts);
        } else if (id == R.id.nav_gallery) {
            Intent i=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_slideshow) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("We are a group of three students of NITC. This app is part of a Vaccation project under the guidance of Dr. Ameer.\n Show your support by continued use of this app");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();

        }
// else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isServiceRunning(String name){
        ActivityManager manager= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
         if(name.equals(service.service.getClassName())){
             return true;

         }

        }
        return false;
    }
    //////////////////////////////////////////////////////Warn if Background Services are Disabled///////////////////////
public void show_BackgroundServicesDisabled_dialog(){
    AlertDialog.Builder alertDialogBuilder =
            new AlertDialog.Builder(this)
                    .setTitle("Background Servies Disabled")
                    .setMessage(" Do you want to go to settings menu?")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    })

                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i=new Intent(MainActivity.this,SettingsActivity.class);
                            startActivity(i);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();


                        }
                    });

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();

}
    @Override
    protected void onDestroy() {


        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }

        super.onDestroy();
    }

}
