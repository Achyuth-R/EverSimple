package com.notes.eversimple;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


import org.jetbrains.annotations.NotNull;

import static android.content.Intent.EXTRA_PHONE_NUMBER;
import static com.notes.eversimple.FloatingViewService.callPhoneNumber;

public class PhoneStateReceiver extends BroadcastReceiver {


    private static final String TAG = "Everee";
    @Override
    public void onReceive( Context arg0,  Intent intent) {
        Log.i(TAG, "OnReceive");
        String action = intent.getAction();
        Log.i(TAG, "action : " + action);
        callHandler(arg0,intent);

    }

    private void callHandler(Context arg0,Intent arg1) {


        try {


            if(arg1.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {

                callPhoneNumber = arg1.getStringExtra(EXTRA_PHONE_NUMBER);
                Log.d("Evereee","Outgoing number "+callPhoneNumber);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    Intent offIntent = new Intent(arg0,FloatingViewService.class);
                    Log.d("Everee","Inside New Outgoing"+callPhoneNumber);
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
                    String num=arg1.getStringExtra(EXTRA_PHONE_NUMBER);



                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        ;
                        Intent offIntent = new Intent(arg0,FloatingViewService.class);

                        arg0.startForegroundService(offIntent);
                    }
                    else {
                        Intent offIntent = new Intent(arg0,FloatingViewService.class);
                        arg0.startService(offIntent);
                    }
                }

                else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    Log.d(TAG, TAG+"Inside EXTRA_STATE_RINGING");
                    callPhoneNumber = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                    Log.d(TAG, TAG+"incoming number : " + callPhoneNumber);
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




