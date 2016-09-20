package com.example.reubert.appcadeirantes.view;

import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.manager.GPSManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private GPSManager gpsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        configureGPS();
        configureMap();
        focusOnCurrentUserPosition();
    }

    private void configureGPS(){
        gpsManager = GPSManager.getInstance(this);
        gpsManager.enableLocation();
    }

    @SuppressWarnings("MissingPermission")
    private void configureMap(){
        if(GPSManager.isPermissionGranted(this)){
            googleMap.setMyLocationEnabled(true);
        }
    }

    private void focusOnCurrentUserPosition(){
        Location userLocation = gpsManager.getUserLocation();
        double latitude;
        double longitude;

        if(userLocation != null){
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();
        } else {
            latitude = -19.9273724;
            longitude = -43.9474144;
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 17));
    }
}
