package com.sergeant_matatov.remotesecurityphone.Services;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Yurka on 12.03.2016.
 */
public class ServiceSecur extends BroadcastReceiver {
    SharedPreferences sinSIMPref;    //для sim
    final String SAVED_SIM = "saved_sim";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceLauncher = new Intent(context, ServiceSecur.class);
            context.startService(serviceLauncher);

            //вытаскиваем старый серийник sim карты
            //    sinSIMPref = context.getSharedPreferences("My sim", context.MODE_PRIVATE);
            //    String oldSIM = sinSIMPref.getString(SAVED_SIM, "");


            SharedPreferences sinSIMPref = context.getSharedPreferences("rsp_contact", context.MODE_PRIVATE);
            String oldSIM = sinSIMPref.getString("save_serial_sim_card", "");
            //    SharedPreferences.Editor ed = sinSIMPref.edit();
            //    ed.putString("save_serial_sim_card", numSIM);
            //     ed.commit();


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
