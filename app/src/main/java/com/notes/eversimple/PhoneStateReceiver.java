package com.notes.eversimple;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;


public class PhoneStateReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, FloatingViewService.class));
                } else {
                    context.startService(new Intent(context, FloatingViewService.class));
                }
                    Log.d("Everee","Ringing");

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, FloatingViewService.class));
                } else {
                    context.startService(new Intent(context, FloatingViewService.class));
                }
                Log.d("Everee","OFKOOK");
                break;

            case TelephonyManager.CALL_STATE_IDLE:
                context.stopService(new Intent(context, FloatingViewService.class));
                Log.d("Everee","Sto[");
                break;
        }
    }


}
