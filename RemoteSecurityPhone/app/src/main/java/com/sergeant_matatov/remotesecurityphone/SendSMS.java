package com.sergeant_matatov.remotesecurityphone;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.ArrayList;

/**
 * Created by Yurka on 14.12.2016.
 */

public class SendSMS extends Activity implements ISendSMS {

    @Override
    public void sendSMS(Context context, String phoneNum, String msg) {
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> al_message = new ArrayList<String>();
        al_message = sms.divideMessage(msg);
        ArrayList<PendingIntent> al_piSent = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> al_piDelivered = new ArrayList<PendingIntent>();
        for (int i = 0; i < al_message.size(); i++) {
            Intent sentIntent = new Intent("SMS_SENT");
            PendingIntent pi_sent = PendingIntent.getBroadcast(context, i, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            al_piSent.add(pi_sent);
            Intent deliveredIntent = new Intent("SMS_DELIVERED");
            PendingIntent pi_delivered = PendingIntent.getBroadcast(context, i, deliveredIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            al_piDelivered.add(pi_delivered);
        }
        sms.sendMultipartTextMessage(phoneNum, null, al_message, al_piSent, al_piDelivered);
    }
}
