package com.example.josep.testingthedrawerlayout;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import pl.droidsonroids.gif.GifImageView;

public class Alert_SMS extends Activity {

    private BroadcastReceiver broadcastReceiver1;
    private String Fix;
    private String latitude;
    private String longitude;
    private TinyDB tinyDB;
    final SmsManager smsManager = SmsManager.getDefault();
    private String speed;
    private String test;
    private ArrayList<String> arrayList,numbersOnly;
    private long  alert_delay=1000;
    private SharedPreferences settingsPrefs;
    final Handler handler = new Handler();
    private int numberOfContacts=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_sms_new);
        getWindow().setGravity(Gravity.TOP|Gravity.CENTER);
        TextView textView = findViewById(R.id.textView3);
        setFinishOnTouchOutside(false);                         ////Don't Dismiss Activity on touching ouside the box

        settingsPrefs=getSharedPreferences("Settings",MODE_PRIVATE);

        switch(settingsPrefs.getInt("delaytime",0)){

            case 0: alert_delay=10000;
                break;
            case 1: alert_delay=20000;
                break;
            case 2: alert_delay=30000;
                break;
            case 3: alert_delay=60000;
                break;
            case 4: alert_delay=300000;
                break;
            case 5: alert_delay=600000;
                break;
        }
        tinyDB=new TinyDB(getApplicationContext());
        arrayList= tinyDB.getListString("NumbersOnly");
        numberOfContacts=tinyDB.getListString("NumbersOnly").size();
        if (broadcastReceiver1 == null) {
            broadcastReceiver1 = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Fix = Objects.requireNonNull(Objects.requireNonNull(intent.getExtras()).get("Fix")).toString();    // Data is sent as a string seperated by commas
                    String[] GPS_data = Fix.split(",");
                    latitude = GPS_data[0];
                    longitude = GPS_data[1];
                    speed= GPS_data[2];

                }
            };
        }
        registerReceiver(broadcastReceiver1, new IntentFilter("location_update"));
        handler.postDelayed(runnable, alert_delay);

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Dragging Cancel Layout>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        GifImageView greenbutton=findViewById(R.id.greenButInPopupGif);
        ImageView bluebutton= findViewById(R.id.blueButInPopup);
        bluebutton.setOnTouchListener(new ChoiceTouchListener());
        greenbutton.setOnDragListener(new ChoiceDragListener());

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Dragging Cancel Layout>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    }
    Runnable runnable= new Runnable()
    {
        @Override
        public void run() {

            if(latitude==null||longitude==null){
                handler.postDelayed(runnable, alert_delay);
            }
            else {
                for (int i = 0; i <numberOfContacts; i++) {
                    smsManager.sendTextMessage(arrayList.get(i), null, "Help! I've met with an accident at http://maps.google.com/?q=" + latitude + "," + longitude, null, null);
                }
                handler.removeCallbacks(runnable);
                finish();
            }
        }
    };

    private final class ChoiceTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //setup drag
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            }
            else {
                return false;
            }
        }
    }
    private class ChoiceDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            //handle drag events
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:
                    //handle the dragged view being dropped over a drop view
                    View view = (View) event.getLocalState();
                    GifImageView target=(GifImageView) v;
                    if (view.getId()==R.id.blueButInPopup){
                        if (target.getId()==R.id.greenButInPopupGif){
                            Toast.makeText(getApplicationContext(),"Alert Cancelled",Toast.LENGTH_LONG).show();
                            handler.removeCallbacksAndMessages(null);
                            finish();
                        }
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //no action necessary
                   // Toast.makeText(getApplicationContext(),"Exit",Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }

            return true;
        }

    }

    @Override
    public void onBackPressed() {


    }
}
