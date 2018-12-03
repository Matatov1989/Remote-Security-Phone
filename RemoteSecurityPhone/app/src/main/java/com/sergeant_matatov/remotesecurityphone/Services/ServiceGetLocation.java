package com.sergeant_matatov.remotesecurityphone.Services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

public class ServiceGetLocation extends Service {

    final String LOG_TAG = "myLogs";
    Location location;

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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Log.d(LOG_TAG, " start ");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //       return;
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Logic to handle location object
                    Log.d(LOG_TAG, " latitude:  " + location.getLatitude());
                    Log.d(LOG_TAG, "longitude:  " + location.getLongitude());
                    Log.d(LOG_TAG, " provider:  " + location.getProvider());
                    stopSelf();
                }
                else
                {
                    Log.d(LOG_TAG, " null:  " );
                    stopSelf();
                }
            }
        });

      /*  mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    location = task.getResult();

                    Log.d(LOG_TAG, " latitude:  " + location.getLatitude());
                    Log.d(LOG_TAG, "longitude:  " + location.getLongitude());
                    Log.d(LOG_TAG, " provider:  " + location.getProvider());
                }

                stopSelf();
            }
        });*/

        //   stopSelf();
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, ServiceSendMessage.class)
                .putExtra("lat", (location != null ? location.getLatitude() : 0.0))
                .putExtra("lon", (location != null ? location.getLongitude() : 0.0));
        startService(intent);

    }
}