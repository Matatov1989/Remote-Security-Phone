package com.sergeant_matatov.remotesecurityphone.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

public class ServiceGetLocation extends Service {

    final String LOG_TAG = "myLogs";
    Location locationDevice;
    private FusedLocationProviderClient mFusedLocationClient;

    public ServiceGetLocation() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //    throw new UnsupportedOperationException("Not yet implemented");
        //      Toast.makeText(this, "service onBind", Toast.LENGTH_SHORT).show();
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(LOG_TAG, " *****start***** ");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //       return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    Log.d(LOG_TAG, " if  ");
                    // Logic to handle location object
                    locationDevice = location;
                    stopSelf();
                } else {
                    Log.d(LOG_TAG, " else  ");
                    stopSelf();
                }
            }
        });
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, ServiceSendMessage.class)
                .putExtra("lat", (locationDevice != null ? locationDevice.getLatitude() : 0.0))
                .putExtra("lon", (locationDevice != null ? locationDevice.getLongitude() : 0.0));
        startService(intent);
    }
}