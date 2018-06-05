package com.example.josep.testingthedrawerlayout;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ShakeMonitor_Service extends Service implements SensorEventListener {

    private long startTime;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean supported= false;
    private static final int FORCE_THRESHOLD = 8000;
    private static final int TIME_THRESHOLD = 75;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int SHAKE_DURATION = 150;
    private static final int SHAKE_COUNT = 1;
    private float LastX=-1.0f, LastY=-1.0f, LastZ=-1.0f;
    private long mLastTime;
    private Context mContext;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;
    private boolean vibration_status=true;
    private SharedPreferences settingsPrefs;


    @Override
    public void onCreate() {
        settingsPrefs=getSharedPreferences("Settings",MODE_PRIVATE);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensorManager == null) {
            throw new UnsupportedOperationException("Sensors not supported");
        }
         supported = sensorManager.registerListener(ShakeMonitor_Service.this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        if (!supported) {
            sensorManager.unregisterListener(this, accelerometer);
            throw new UnsupportedOperationException("Accelerometer not supported");
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        long now = System.currentTimeMillis();

        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }

        if ((now - mLastTime) > TIME_THRESHOLD) {
            long diff = now - mLastTime;
            float speed = Math.abs(event.values[0] + event.values[1] + event.values[2] - LastX - LastY - LastZ) / diff * 10000;
            if (speed > FORCE_THRESHOLD) {
                if ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;
                   shaked();
                }
                mLastForce = now;
            }

        mLastTime = now;
        LastX= event.values[0];
        LastY= event.values[1];
        LastZ= event.values[2];
        }

    }

    public void shaked() {
        vibration_status=settingsPrefs.getBoolean("isVibrationOn",true);
        if(vibration_status) {
        final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(1000);
        }
        Intent i = new Intent();
        i.setClass(this, Alert_SMS.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(sensorManager!= null){
            sensorManager.unregisterListener(ShakeMonitor_Service.this, accelerometer);
            sensorManager =null;
        }
    }
}
