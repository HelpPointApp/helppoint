package com.example.reubert.appcadeirantes.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.manager.GPSManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


/**********************************************************************
 * This class will be refactored following Clean Code's instructions.
 * But probably will not be so clean for be shown as MVP.
 **********************************************************************/
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
    }


    private void configureGPS(){
        gpsManager = GPSManager.getInstance(this);
        gpsManager.enableLocation();
    }


    private void configureMap(){
        try {
            googleMap.setMyLocationEnabled(true);
        } catch(SecurityException exception){}
    }
}
