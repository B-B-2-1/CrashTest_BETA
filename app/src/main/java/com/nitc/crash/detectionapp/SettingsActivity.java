package com.nitc.crash.detectionapp;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private Switch gpsSwitch;
    private SeekBar delaytime;
    private final String GPS_serviceName = "com.example.josep.testingthedrawerlayout.GPS_Service";            // To search if service is already running
    private SharedPreferences settingsPrefs;
    private TextView tvTime;
    private int delay;
    private char time_char;                   // time unit
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsPrefs=getSharedPreferences("Settings",MODE_PRIVATE);

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>GPS Switch>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        boolean isGpsOn=settingsPrefs.getBoolean("gpsServiceOnorOff",true);
        gpsSwitch=findViewById(R.id.gpsSwitch);
        if (isGpsOn){
            gpsSwitch.setChecked(true);
        }else {
            gpsSwitch.setChecked(false);
        }
        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                settingsPrefs.edit().putBoolean("gpsServiceOnorOff",isChecked).commit();
                if(!isChecked){
                    if(isServiceRunning(GPS_serviceName)) {
                        Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                        stopService(i);
                    }
                }
                else
                {
                    Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                    startService(i);
                }
            }
        });
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>/GPS Switch>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>delay Slider>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        delaytime=findViewById(R.id.seekbar);
        tvTime=findViewById(R.id.tvTime);
        switch(settingsPrefs.getInt("delaytime",0)){

            case 0: delay=10; time_char='s';
                    break;
            case 1: delay=20; time_char='s';
                    break;
            case 2: delay=30; time_char='s';
                break;
            case 3: delay=1; time_char='m';
                break;
            case 4: delay=5; time_char='m';
                break;
            case 5: delay=10; time_char='m';
                break;
        }
        delaytime.setProgress(settingsPrefs.getInt("delaytime",0));
        tvTime.setText(Integer.toString(delay)+time_char);
        delaytime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            settingsPrefs.edit().putInt("delaytime",progress).commit();
                switch(progress){

                    case 0: delay=10; time_char='s';
                        break;
                    case 1: delay=20; time_char='s';
                        break;
                    case 2: delay=30; time_char='s';
                        break;
                    case 3: delay=1; time_char='m';
                        break;
                    case 4: delay=5; time_char='m';
                        break;
                    case 5: delay=10; time_char='m';
                        break;

                }
                tvTime.setText(Integer.toString(delay)+time_char);            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>/delay Slider>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Vibration On or Off>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        Switch vibration=findViewById(R.id.switch2);
        Boolean isVibrationOn=settingsPrefs.getBoolean("isVibrationOn",true);
        if (isVibrationOn){
            vibration.setChecked(true);
        }else {
            vibration.setChecked(false);
        }

        vibration.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsPrefs.edit().putBoolean("isVibrationOn",isChecked).commit();
                Toast.makeText(getBaseContext(),Boolean.toString(isChecked),Toast.LENGTH_SHORT);
            }
        });

    }

    public void closeSettings(View view) {
        finish();
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


    @Override
    public void onBackPressed() {
        finish();
    }
}
