package com.sergeant_matatov.remotesecurityphone.Services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

import com.sergeant_matatov.remotesecurityphone.Activitys.MapsActivity;
import com.sergeant_matatov.remotesecurityphone.R;


/**
 * Created by Yurka on 12.03.2016.
 */
public class ServiceInterceptSMS extends BroadcastReceiver {

    String textMessage = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        String textControl = context.getString(R.string.textSMSnewSimWithoutLocal);
        //Intercept SMS
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        if (bundle != null) {
            //get text
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                //     str += msgs[i].getOriginatingAddress();
                textMessage += msgs[i].getMessageBody();
            }

            //filter all messages
            if (!textControl.equals(textMessage) && textMessage.contains("RSP")) {

                int indexStart = textMessage.indexOf('[');
                int indexStop = textMessage.indexOf(']');

                char[] buf = new char[(indexStop - 0) - (indexStart + 1)];
                textMessage.getChars((indexStart + 1), indexStop, buf, 0);
                String textLocation = new String(buf);
                textLocation = textLocation.replace(",", "");
                String[] tempLocal = new String[2];
                tempLocal = textLocation.split(" ");
                String strLat = tempLocal[0];
                String strLon = tempLocal[1];

                //save got location from message in order to see location device on map
                SharedPreferences prefLat = context.getSharedPreferences("rsp_contact", context.MODE_PRIVATE);
                SharedPreferences.Editor ed = prefLat.edit();
                ed.putString("save_location_latitude", strLat);
                ed.putString("save_location_longitude", strLon);
                ed.commit();

                //create notification
                Intent notifyIntent = new Intent(context, MapsActivity.class);
                notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_location_on)
                        .setAutoCancel(true)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.textNotification))
                        .setContentIntent(notifyPendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(101, mBuilder.build());
            }
        }
    }
}