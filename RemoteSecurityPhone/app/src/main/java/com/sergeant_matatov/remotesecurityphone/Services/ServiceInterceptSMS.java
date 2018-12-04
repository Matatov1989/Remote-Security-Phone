package com.sergeant_matatov.remotesecurityphone.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    String text;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Intercept SMS
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "";
        if (bundle != null) {
            //get text
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            Log.d(LOG_TAG, "get long msgs.len: " + msgs.length);
            for (int i = 0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str += msgs[i].getOriginatingAddress();
                str += "-";
                str += msgs[i].getMessageBody().toString();
                Log.d(LOG_TAG, "get long getMessageBody(): " + msgs[i].getMessageBody());
            }

            Log.d(LOG_TAG, "get long sms: " + str);

            if (str.charAt(str.length() - 1) != '-') {
                str += '-';
            }

            //breake a message to phone number and text
            strCom = str.split("-");

            Log.d(LOG_TAG, "get sms:  " + strCom[0]);
            Log.d(LOG_TAG, "get sms:  " + strCom[1]);

            if (strCom.length == 2) {

            }
            else if (strCom.length == 3) {

            }
        }
    }
}