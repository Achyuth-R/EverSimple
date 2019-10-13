package com.notes.phonememo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;


import static android.content.Context.TELEPHONY_SERVICE;
import static android.content.Intent.EXTRA_PHONE_NUMBER;
import static com.notes.phonememo.FloatingViewService.callPhoneNumber;

public class PhoneStateReceiver extends BroadcastReceiver {



    private static final String TAG = "Sonalll";
    @Override
    public void onReceive( Context arg0,  Intent intent) {
        String state=intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if(state==null)
        {
            //Outgoing call
            callPhoneNumber=intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.i("tag","Outgoing number : "+callPhoneNumber);
        }
        else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
            //Incoming call
            callPhoneNumber= intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Log.i("tag","Incoming number : "+callPhoneNumber);
        }
        Log.d(TAG, "OnReceive");
        String action = intent.getAction();
        Log.d(TAG, "action : " + action);
        callHandler(arg0,intent);

    }

    private void callHandler(Context arg0,Intent arg1) {


        try {


            if(arg1.getAction().equals("android.intent.action.PHONE_STATE")){

                String state = arg1.getStringExtra(TelephonyManager.EXTRA_STATE);
                Log.d("sonalll ","state is : "+state);


                if(state.equals("OFFHOOK")) {
//                    callPhoneNumber = arg1.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    Log.d("sonalll","Inside New Outgoing "+callPhoneNumber);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

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
//                    callPhoneNumber = arg1.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                    Log.d(TAG, TAG+"incoming number : " + callPhoneNumber);
                    if(callPhoneNumber.charAt(0)!=('*')){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        Intent offIntent = new Intent(arg0,FloatingViewService.class);
                        Log.d("Sonal","Reached 86");
                        arg0.startForegroundService(offIntent);
                    }
                    else {
                        Intent offIntent = new Intent(arg0,FloatingViewService.class);
                        Log.d("Sonal","Reached 91");
                        arg0.startService(offIntent);
                    }

                }}
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




