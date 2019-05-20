package com.notes.eversimple;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


public class PhoneStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                context.startService(new Intent(context, FloatingViewService.class));
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                context.startService(new Intent(context, FloatingViewService.class));

                break;
            case TelephonyManager.CALL_STATE_IDLE:
                context.stopService(new Intent(context, FloatingViewService.class));
                break;
        }
    }
}
