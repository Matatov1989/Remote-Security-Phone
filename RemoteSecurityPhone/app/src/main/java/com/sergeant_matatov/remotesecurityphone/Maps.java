package com.sergeant_matatov.remotesecurityphone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps extends FragmentActivity {

    private GoogleMap mMap;        // Might be null if Google Play services APK is not available.

    SharedPreferences latPref;    //сохраняет lat
    SharedPreferences lonPref;    //сохраняет lon
    final String SAVED_LAT = "saved_lat";
    final String SAVED_LON = "saved_lon";

    Double latitude;
    Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_maps);

        latitude = Double.valueOf(loadLat());
        longitude = Double.valueOf(loadLon());

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Marker"));

        //вид на карту
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))        //точка
                .zoom(15)                                       //зум
                .bearing(45)                                    //поворот карт
                .tilt(20)                                       //угол наклона
                .build();

        //И передаем полученный объект в метод newCameraPosition, получая CameraUpdate, который в свою очередь передаем в метод animateCamera
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
    }

    //загружает Latitude
    public String loadLat() {
        latPref = getSharedPreferences("Latitude", MODE_PRIVATE);
        String lat = "";
        lat = latPref.getString(SAVED_LAT, "");

        if (lat.equals(""))
            lat = "33.005790";
        return lat;
    }

    //загружает Longitude
    public String loadLon() {
        lonPref = getSharedPreferences("Longitude", MODE_PRIVATE);
        String lon = "";
        lon = lonPref.getString(SAVED_LON, "");

        if (lon.equals(""))
            lon = "35.099440";
        return lon;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Maps.this, MainActivity.class));
    }
}
