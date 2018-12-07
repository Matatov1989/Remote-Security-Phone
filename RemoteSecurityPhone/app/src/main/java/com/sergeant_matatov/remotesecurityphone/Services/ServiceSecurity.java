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

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPrefs = context.getSharedPreferences("rsp_contact", context.MODE_PRIVATE);

        //if switch button on position ON
        if (sharedPrefs.getBoolean("switch_button_job", false)) {

            //sleep 50 seconds because after restart, device will not to get location
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                Intent serviceLauncher = new Intent(context, ServiceSecurity.class);
                context.startService(serviceLauncher);

                //get saved a serial number
                SharedPreferences sinSIMPref = context.getSharedPreferences("rsp_contact", context.MODE_PRIVATE);
                String oldSIM = sinSIMPref.getString("save_serial_sim_card", "");

                TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String newSIM = telephonyMgr.getSimSerialNumber();

                //if has new sim card, the programm get location and sent message to selected user
                if (oldSIM.equals(newSIM)) {
                    intent = new Intent(context, ServiceGetLocation.class);
                    context.startService(intent);
                }
            }
        }
    }
}
