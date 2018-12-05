package com.sergeant_matatov.remotesecurityphone.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

import com.sergeant_matatov.remotesecurityphone.R;


/**
 * Created by Yurka on 12.03.2016.
 */
public class ServiceInterceptSMS extends BroadcastReceiver {
    final String LOG_TAG = "myLogs";

    String[] strCom;    //для команды
    Context context;

    String textMessage = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        String textControl = context.getString(R.string.textSMSnewSimWithoutLocal);
        //Intercept SMS
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
  //      String str = "";
        if (bundle != null) {
            //get text
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            Log.d(LOG_TAG, "get long msgs.len: " + msgs.length);
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
           //     str += msgs[i].getOriginatingAddress();
           //     str += "-";
           //     str += msgs[i].getMessageBody().toString();
                textMessage += msgs[i].getMessageBody();
                Log.d(LOG_TAG, "get long getMessageBody(): " + msgs[i].getMessageBody());
            }

            Log.d(LOG_TAG, " if " + (textControl.equals(textMessage)));

            Log.d(LOG_TAG, " textControl " + textControl);
            Log.d(LOG_TAG, " textMessage " + textMessage);

            if (!textControl.equals(textMessage)){

                int indexStart = textMessage.indexOf('[');
                int indexStop = textMessage.indexOf(']');

                Log.d(LOG_TAG, " index " + indexStart+ " "+indexStop);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_logo)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(textMessage)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);


                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.notify(101, mBuilder.build());
            }
        }
    }
}