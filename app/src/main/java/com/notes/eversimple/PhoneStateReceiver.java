package com.notes.eversimple;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import static com.notes.eversimple.FloatingViewService.callPhoneNumber;

public class PhoneStateReceiver extends BroadcastReceiver {

    private static final String TAG = "Everee";
    @Override
    public void onReceive( Context arg0,  Intent arg1) {
        try {

            if(arg1.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                String number = arg1.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Toast.makeText(arg0,number,Toast.LENGTH_LONG).show();
                Log.d("Everee","Inside New Outgoing"+number);
                callPhoneNumber =number;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    Intent offIntent = new Intent(arg0,FloatingViewService.class);

                    arg0.startForegroundService(offIntent);
                }
                else {
                    Intent offIntent = new Intent(arg0,FloatingViewService.class);
                    arg0.startService(offIntent);
                }
            }

            if(arg1.getAction().equals("android.intent.action.PHONE_STATE")){

                String state = arg1.getStringExtra(TelephonyManager.EXTRA_STATE);

                if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                    Log.d(TAG, TAG+"Inside Extra state off hook");
                    String number = arg1.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    callPhoneNumber =number;
                    Toast.makeText(arg0,number,Toast.LENGTH_LONG).show();
                    Log.d(TAG, "outgoing number : " + number);

                }

                else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    Log.d(TAG, TAG+"Inside EXTRA_STATE_RINGING");
                    String number = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Toast.makeText(arg0,number,Toast.LENGTH_LONG).show();
                    callPhoneNumber =number;
                    Log.d(TAG, TAG+"incoming number : " + number);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    Intent offIntent = new Intent(arg0,FloatingViewService.class);

                    arg0.startForegroundService(offIntent);
                   }
                    else {
                   Intent offIntent = new Intent(arg0,FloatingViewService.class);
                   arg0.startService(offIntent);
                   }

                }
                else if(state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    Log.d(TAG, TAG+"Inside EXTRA_STATE_IDLE");
                    arg0.stopService(new Intent(arg0, FloatingViewService.class));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    }


