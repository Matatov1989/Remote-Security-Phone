package com.sergeant_matatov.remotesecurityphone;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Yurka on 12.03.2016.
 */
public class ServiceNotif extends Service {
    NotificationManager nm;
    Context context;

    String phone;

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        phone = intent.getStringExtra("phone");
        sendNotif(phone);
        return Service.START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
        nm.cancelAll();

        Notification noti = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.titleDialogUrgently))
                .setContentText(getString(R.string.dialogUrgentlyCall) + " " + phone)
                .setSmallIcon(R.drawable.cast_ic_notification_1)
                .build();
        noti.flags = Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;

        nm.notify(101, noti);
    }

    public void sendNotif(String phone) {
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.original);
        long[] vib = {500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500, 500};

        Notification noti = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.titleDialogUrgently))
                .setContentText(getString(R.string.dialogUrgentlyCall) + " " + phone)
                .setSmallIcon(R.drawable.cast_ic_notification_1)
                .setVibrate(vib)
                .setSound(sound)
                .build();
        noti.flags = Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;

        nm.notify(101, noti);
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }
}
