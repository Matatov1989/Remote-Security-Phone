package com.sergeant_matatov.remotesecurityphone.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sergeant_matatov.remotesecurityphone.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    /*    // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
*/

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
        startActivity(new Intent(MapsActivity.this, MainActivity11.class));
    }
}
