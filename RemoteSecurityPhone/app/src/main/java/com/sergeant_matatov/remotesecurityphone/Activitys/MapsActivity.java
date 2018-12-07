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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sergeant_matatov.remotesecurityphone.R;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;     // Might be null if Google Play services APK is not available.

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(getString(R.string.titleMarkerMap)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_phone_iphone)));

        //view map
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))        //point
                .zoom(15)                                       //zoom
                .bearing(45)                                    //card rotation
                .tilt(20)                                       //tilt angle
                .build();

        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.animateCamera(cameraUpdate);
    }

    //get Latitude
    public String loadLat() {
        SharedPreferences latPref = getSharedPreferences("rsp_contact", MODE_PRIVATE);
        return latPref.getString("save_location_latitude", "33.005790");
    }

    //get Longitude
    public String loadLon() {
        SharedPreferences lonPref = getSharedPreferences("rsp_contact", MODE_PRIVATE);
        return lonPref.getString("save_location_longitude", "35.099440");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MapsActivity.this, MainActivity.class));
    }
}
