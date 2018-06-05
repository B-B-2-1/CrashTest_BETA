package com.example.josep.testingthedrawerlayout;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;

public class LocationOffDialog extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder alertDialogBuilder =
                new AlertDialog.Builder(this)
                        .setTitle("CrashAlert Requires GPS")
                        .setMessage("GPS is not Enabled. Do you want to go to settings menu?")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        })

                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();

                            }
                        })
                         .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.cancel();
                                 finish();

                             }
                         });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
