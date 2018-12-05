package com.sergeant_matatov.remotesecurityphone.Services;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Yurka on 12.03.2016.
 */
public class ServiceSecurity extends BroadcastReceiver {
    final String LOG_TAG = "myLogs";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, " on");
        try {
            Thread.sleep(50000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, " 1on");
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceLauncher = new Intent(context, ServiceSecurity.class);
            context.startService(serviceLauncher);

            SharedPreferences sinSIMPref = context.getSharedPreferences("rsp_contact", context.MODE_PRIVATE);
            String oldSIM = sinSIMPref.getString("save_serial_sim_card", "");

            TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            String newSIM = telephonyMgr.getSimSerialNumber();

            if (/*oldSIM.equals(newSIM)*/true){
                intent = new Intent(context, ServiceGetLocation.class);
                context.startService(intent);
            }
        }
    }
}
