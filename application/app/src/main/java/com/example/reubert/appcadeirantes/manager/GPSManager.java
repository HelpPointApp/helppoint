package com.example.reubert.appcadeirantes.manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
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


    public void enableLocation(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)){
            requireLocationEnabled();
        }
    }


    private void requireLocationEnabled(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Para se tornar um herói, o GPS precisa estar ativo.");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ligar GPS", activatePositiveOptionHandler);
        alertDialogBuilder.create().show();
    }


    private class ActivatePositiveOptionHandler implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int id) {
            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }

}
