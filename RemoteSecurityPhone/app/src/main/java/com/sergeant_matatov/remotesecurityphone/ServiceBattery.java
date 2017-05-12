package com.sergeant_matatov.remotesecurityphone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;

/**
 * Created by Yurka on 12.03.2016.
 */
public class ServiceBattery extends Service {
    Context context;
    private ReceiverBattery receiver;
    String phone;

    String type;

    SharedPreferences typePref;
    final String TYPE_SEND = "type_send";

    SharedPreferences sendPref;
    final String FLAG_SEND = "flag_sens";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        phone = intent.getStringExtra("phone");
        type = intent.getStringExtra("type");

        intent = new Intent("com.sergeant_matatov.remotesecurityphone");
        intent.putExtra("phone", phone);
        if (type.equals("notification"))
            intent.putExtra("type", "notification");
        else
            intent.putExtra("type", "command");

        sendBroadcast(intent);

        receiver = new ReceiverBattery();
        IntentFilter filter = new IntentFilter("com.sergeant_matatov.remotesecurityphone");
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, filter);

        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //обнулили тип батареи notification/command
        typePref = getSharedPreferences("Type send", MODE_PRIVATE);
        SharedPreferences.Editor ed = typePref.edit();
        ed.putString(TYPE_SEND, "");
        ed.commit();

        //обнулили flag вхтода для отправки sms
        sendPref = getSharedPreferences("Flag send", MODE_PRIVATE);
        ed = sendPref.edit();
        ed.putString(FLAG_SEND, "not send");
        ed.commit();

        unregisterReceiver(receiver);
    }
}