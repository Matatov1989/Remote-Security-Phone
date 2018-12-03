package com.sergeant_matatov.remotesecurityphone.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import com.sergeant_matatov.remotesecurityphone.R;

import java.util.ArrayList;

public class ServiceSendMessage extends Service {

    final String LOG_TAG = "myLogs";


    public ServiceSendMessage() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //    Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, " start SEND");


        //      intent = Intent.getIntent();

        Log.d(LOG_TAG, " start SEND lat "+ intent.getDoubleExtra("lat", 0.0));
        Log.d(LOG_TAG, " start SEND lon "+ intent.getDoubleExtra("lon", 0.0));
        double lat = intent.getDoubleExtra("lat", 0.0);
        double lon = intent.getDoubleExtra("lon", 0.0);

        String message = "";
        if (lat == 0.0 && lon == 0.0)
            message = getString(R.string.textSMSnewSimWithoutLocal);
        else
            message = getString(R.string.textSMSnewSimWithLocal, ""+lat, ""+lon);

        Log.d(LOG_TAG, " start SEND message "+ message);

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> al_message = new ArrayList<String>();
        al_message = sms.divideMessage(message);
        ArrayList<PendingIntent> al_piSent = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> al_piDelivered = new ArrayList<PendingIntent>();
        for (int i = 0; i < al_message.size(); i++) {
            Intent sentIntent = new Intent("SMS_SENT");
            PendingIntent pi_sent = PendingIntent.getBroadcast(this, i, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            al_piSent.add(pi_sent);
            Intent deliveredIntent = new Intent("SMS_DELIVERED");
            PendingIntent pi_delivered = PendingIntent.getBroadcast(this, i, deliveredIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            al_piDelivered.add(pi_delivered);
        }
//        sms.sendMultipartTextMessage("0526461150", null, al_message, al_piSent, al_piDelivered);

        stopSelf();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, " stop SEND");
        //   Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();


    }

}