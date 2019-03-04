package com.sergeant_matatov.remotesecurityphone.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;

import com.sergeant_matatov.remotesecurityphone.R;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class ServiceSendMessage extends Service {

    final String LOG_TAG = "myLogs";

    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";

    public ServiceSendMessage() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //get phone number from selected contact
        final String CALLER_NUMBER = "contact_number";
        SharedPreferences personPref = getSharedPreferences("rsp_contact", MODE_PRIVATE);
        String phoneNumber = personPref.getString(CALLER_NUMBER, "");

        //get type a message and location
        boolean flag = intent.getBooleanExtra("flagSecuritySMS", false);
        double lat = intent.getDoubleExtra("lat", 0.0);
        double lon = intent.getDoubleExtra("lon", 0.0);

        String message = "";
        //if true, get text about changet a sim card
        //and if has location, get text with location.
        if (flag) {
            if (lat == 0.0 && lon == 0.0)
                message = getString(R.string.textSMSnewSimWithoutLocal);
            else
                message = getString(R.string.textSMSnewSimWithLocal, "" + lat, "" + lon);
        } else
            message = getString(R.string.textSMSFirst);
/*
        //send sms message
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
        sms.sendMultipartTextMessage(phoneNumber, null, al_message, al_piSent, al_piDelivered);
*/
        stopSelf();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private void sendMessage() {
        String body = "";
        String from = "";
        String to = "";

        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
        );

        Map<String, String> data = new HashMap<>();
        data.put("From", from);
        data.put("To", to);
        data.put("Body", body);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twilio.com/2010-04-01/")
                .build();
        TwilioApi api = retrofit.create(TwilioApi.class);

        api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful())
                    Log.d(LOG_TAG, "onResponse->success " );
                else
                    Log.d(LOG_TAG, "onResponse->failure "+response.toString());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(LOG_TAG, "onFailure");
            }
        });
    }

    interface TwilioApi {
        @FormUrlEncoded
        @POST("Accounts/{ACCOUNT_SID}/SMS/Messages")
        Call<ResponseBody> sendMessage(
                @Path("ACCOUNT_SID") String accountSId,
                @Header("Authorization") String signature,
                @FieldMap Map<String, String> metadata
        );
    }


    @Override
    public void onDestroy() {

    }
}