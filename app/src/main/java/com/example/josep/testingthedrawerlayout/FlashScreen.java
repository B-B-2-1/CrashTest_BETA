package com.example.josep.testingthedrawerlayout;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FlashScreen extends AppCompatActivity {


    private Button button;
    private TextView textView;
    private ProgressBar progressBar;
    private int count=0;
    private int delay=3000;
    private Handler handler= new Handler();
    public int sms_permission_status=-1;
    public int gps_permission_status=-1;
    int flag=1;
    private SharedPreferences mPrefs;
    private boolean itsthefirsttime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);
        textView = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        mPrefs=getSharedPreferences("FirstTimeCheckinFlashscreen",MODE_PRIVATE);


        firstTimeorNot();
        start_handler();



    }

    private void firstTimeorNot() {
        if (mPrefs.getBoolean("firstrun", true)) {
         itsthefirsttime=true;
         mPrefs.edit().putBoolean("firstrun", false).commit();
        }else {
         itsthefirsttime=false;
        }
    }

    public void start_handler( ){
        runnable.run();

    }

    public void stopHandler(){
        handler.removeCallbacks(runnable);
    }



    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(flag==1)
                count++;
            switch(count){
                case 1: progressBar.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Initializing..");
                    delay=2000;
                    handler.postDelayed(runnable, delay);
                    break;
                case 2: textView.setText("Checking User Permissions..");
                    delay=1000;

                    handler.postDelayed(runnable, delay);
                    break;
                case 3:  check_SMS_permission();
                    delay=500;
                    handler.postDelayed(runnable, delay);
                    break;
                case 4:  if(sms_permission_status==-1 || gps_permission_status==-1)
                    flag=0;
                else
                    flag=1;
                    delay= 500;
                    handler.postDelayed(runnable, delay);
                    break;
                case 5: textView.setText("Launching App..");
                    delay= 2000;
                    handler.postDelayed(runnable, delay);
                    break;
                case 6:
                    if (itsthefirsttime) {
                        Log.d("firsttim", "run: yesss");
                        Intent intentFromFlashScreenToContacts = new Intent(FlashScreen.this, Contact.class);
                        startActivity(intentFromFlashScreenToContacts);
                        finish();
                        stopHandler();
                    }else {
                        Log.d("notfirsttime", "run: SSSSSSSSSSSSDDDDDDDDDDDDDDDDD");
                        Intent intentFromFlashScreenToMainactivity = new Intent(FlashScreen.this, MainActivity.class);
                        startActivity(intentFromFlashScreenToMainactivity);
                        finish();
                        stopHandler();
                    }
                    break;

            }

        }

    };
    private void check_SMS_permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 9);

        }
        else{
            sms_permission_status=1;
            check_GPS_Permission();
        }

    }

    public void check_GPS_Permission() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 10);


        }
        else{
            gps_permission_status=1;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case 9: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sms_permission_status=1;

                } else {
                    sms_permission_status=0;
                   /* Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;*/
                }

                check_GPS_Permission();
                break;
            }

            case 10: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gps_permission_status=1;

                } else {
                    gps_permission_status=0;
                   /* Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;*/
                }

            }

        }
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
