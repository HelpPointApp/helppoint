package com.helppoint.app.manager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

public class GPSManager {

    private static GPSManager instance;
    private Context context;
    private final DialogInterface.OnClickListener activatePositiveOptionHandler = new ActivatePositiveOptionHandler();

    public synchronized static GPSManager getInstance(Context context){
        if(instance == null){
            instance = new GPSManager();
        }

        instance.context = context;
        return instance;
    }

    public static boolean isPermissionGranted(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void enableLocation(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
            requireLocationEnabled();
        }
    }

    private void requireLocationEnabled() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Para se tornar um herói, o GPS precisa estar ativo.");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ligar GPS", activatePositiveOptionHandler);
        alertDialogBuilder.create().show();
    }

    @SuppressWarnings("MissingPermission")
    public Location getUserLocation(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;

        if(GPSManager.isPermissionGranted(context)) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if(location == null){
            // @toDo: there is a better way than use NETWORK_PROVIDER
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        return location;
    }

    private class ActivatePositiveOptionHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int id) {
            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }
}
