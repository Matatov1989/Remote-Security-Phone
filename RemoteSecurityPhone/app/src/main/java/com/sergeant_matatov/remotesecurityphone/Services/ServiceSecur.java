package com.sergeant_matatov.remotesecurityphone.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import com.sergeant_matatov.remotesecurityphone.LocationPhone;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Yurka on 12.03.2016.
 */
public class ServiceSecur extends BroadcastReceiver {
    SharedPreferences sinSIMPref;    //для sim
    final String SAVED_SIM = "saved_sim";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceLauncher = new Intent(context, ServiceSecur.class);
            context.startService(serviceLauncher);

            //вытаскиваем старый серийник sim карты
            sinSIMPref = context.getSharedPreferences("My sim", context.MODE_PRIVATE);
            String oldSIM = sinSIMPref.getString(SAVED_SIM, "");

            TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            String newSIM = telephonyMgr.getSimSerialNumber();

            //если симка новая то об этом узнает друг
            if (!oldSIM.equals(newSIM)) {
                LocationPhone l = new LocationPhone();
                l.getCoordinates(context, "", 2);
            }
        }
    }
}
