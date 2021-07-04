package com.nitc.crash.detectionapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class GPS_Service extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private boolean spike= false;
    double finalspeed ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        listener = new LocationListener() {

            private Location lastLocation = null;
            private double calculatedSpeed = 0;

            @Override
            public void onLocationChanged(Location location) {
                if (lastLocation != null) {
                    double elapsedTime = (location.getTime() - lastLocation.getTime()) / 1_000; // Convert milliseconds to seconds
                    calculatedSpeed = lastLocation.distanceTo(location) / elapsedTime;
                }
                this.lastLocation = location;
                if(location.hasSpeed()){
                    finalspeed=location.getSpeed();
                }
                else{
                    finalspeed=calculatedSpeed;
                }


                Intent j = new Intent("location_update");
                j.putExtra("Fix",  location.getLatitude() + "," + location.getLongitude() + "," + finalspeed);
                sendBroadcast(j);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                show_Alert();
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);


        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,0,listener);
        }

    }


public void show_Alert()
{

    Intent i = new Intent();
    i.setClass(this, LocationOffDialog.class);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(i);

}
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){

            locationManager.removeUpdates(listener);
        }
    }
}
