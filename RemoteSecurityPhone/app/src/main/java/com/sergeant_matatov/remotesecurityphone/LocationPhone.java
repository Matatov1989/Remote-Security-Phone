package com.sergeant_matatov.remotesecurityphone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by Yurka on 17.10.2016.
 */

public class LocationPhone extends Activity implements LocationListener {
    static Context context1;
    SendSMS sms;

    SharedPreferences contactPref;    //для контакта
    final String SAVED_CONTACT = "saved_contact";

    boolean isGPSEnabled = false;           // flag for GPS status
    boolean isNetworkEnabled = false;       // flag for network status

    Location location;      // location
    LocationManager locationManager;
    static String msg;
    static String phoneNum;
    static int typeSend;
    static int cnt = 0;

    public void getCoordinates(Context context, String phone, int type) {
        phoneNum = phone;
        context1 = context;
        typeSend = type;

        if (type == 1)
            msg = "";

        else if (type == 2) {
            msg = context.getResources().getString(R.string.smsNewSim).toString() + "-";
            phoneNum = loadContact();
        }

        LocationListener locationListener = new LocationPhone();

        try {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                msg += context.getString(R.string.smsProvaiders);
                sms = new SendSMS();
                sms.sendSMS(context1, phoneNum, msg);
            } else if (isGPSEnabled && isNetworkEnabled) {
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                        locationListener.onLocationChanged(location);
                    }
                } else if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 2000, 0, this);
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                        locationListener.onLocationChanged(location);
                    }
                } else {
                    msg += context.getString(R.string.smsProvaiders);
                    sms = new SendSMS();
                    sms.sendSMS(context1, phoneNum, msg);
                }
            } else if (isGPSEnabled) {
                locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 2000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
                    locationListener.onLocationChanged(location);
                } else {
                    locationListener.onProviderDisabled(location.getProvider());
                }
            } else if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 2000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
                    locationListener.onLocationChanged(location);
                } else
                    locationListener.onProviderDisabled(location.getProvider());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (cnt != 0) {
            if (location != null) {
                msg += "Coordinates-" + location.getLatitude() + ", " + location.getLongitude() + "-" + location.getSpeed() * 3600 / 1000 + "km/h-" + location.getProvider().toString() + "-";
                sms = new SendSMS();
                sms.sendSMS(context1, phoneNum, msg);
                locationManager.removeUpdates(this);        //stop location gps
            } else {
                msg += location.getProvider().toString() + " " + context1.getString(R.string.smsProvaidersGpsOrNet);
                sms = new SendSMS();
                sms.sendSMS(context1, phoneNum, msg);
                locationManager.removeUpdates(this);        //stop location gps
            }
        } else
            cnt++;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        msg += " " + provider + " " + context1.getString(R.string.smsProvaidersGpsOrNet);
        sms = new SendSMS();
        sms.sendSMS(context1, phoneNum, msg);
    }

    //загружает контакт
    public String loadContact() {
        contactPref = context1.getSharedPreferences("My Contact", context1.MODE_PRIVATE);
        String contact = contactPref.getString(SAVED_CONTACT, "");
        return contact;
    }
}
