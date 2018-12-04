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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Log.d(LOG_TAG, " *****start***** ");

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

                    Log.d(LOG_TAG, " get latitude:  " + location.getLatitude());
                    Log.d(LOG_TAG, "get longitude:  " + location.getLongitude());

                    stopSelf();
                    //                   startLocationUpdates();
                } else {
                    Log.d(LOG_TAG, " else  ");


                    /*
                    if (ActivityCompat.checkSelfPermission(ServiceGetLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ServiceGetLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {

                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult == null) {
                                return;
                            }

                            locationDevice = locationResult.getLastLocation();

                            Log.d(LOG_TAG, "get update latitude:  " + locationDevice.getLatitude());
                            Log.d(LOG_TAG, "get update longitude:  " + locationDevice.getLongitude());

                            mFusedLocationClient.removeLocationUpdates(this);
                            stopSelf();


                            for (Location location : locationResult.getLocations()) {
                                // Update UI with location data
                                // ...
                                Log.d(LOG_TAG, "get update latitude:  " + location.getLatitude());
                                Log.d(LOG_TAG, "get update longitude:  " + location.getLongitude());

                                String loc = "lat: " + location.getLatitude() + "\nlon: " + location.getLongitude();
                                Toast.makeText(ServiceGetLocation.this, loc, Toast.LENGTH_SHORT).show();

                                mFusedLocationClient.removeLocationUpdates(this);
                                stopSelf();
                            }
                        }
                    }, null);
                  */

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

    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;


}