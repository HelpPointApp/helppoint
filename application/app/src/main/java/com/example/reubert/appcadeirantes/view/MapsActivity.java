package com.example.reubert.appcadeirantes.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.example.reubert.appcadeirantes.R;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;


/**********************************************************************
 * This class will be refactored following Clean Code's instructions.
 * But probably will not be so clean for be shown as MVP.
 **********************************************************************/
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LocationListener locationListener;

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

        ensureGPSIsTurnedOn();
        configureBasicFeatures();
    }

    private void ensureGPSIsTurnedOn(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            requireGPSActivation();
        }
    }

    private void requireGPSActivation(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Para se tornar um herói, o GPS precisa estar ativo.");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ligar GPS", new ActivateGPSYesOptionHandler());
        alertDialogBuilder.create().show();
    }

    private void configureBasicFeatures(){
        try {
            googleMap.setMyLocationEnabled(true);
        } catch(SecurityException exception){}
    }

    public class ActivateGPSYesOptionHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int id) {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }
}
