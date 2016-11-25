package com.helppoint.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import com.helppoint.app.utilities.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddressIntentService extends IntentService {
    public static final int SUCCESS_RESULT_CODE = 1;
    public static final String RESULT = Constants.MAIN_PACKAGE + ".ADDRESS_SERVICE_RESULT";
    public static final String LOCATION = Constants.MAIN_PACKAGE + ".ADDRESS_SERVICE_LOCATION_EXTRA";
    public static final String RECEIVER = Constants.MAIN_PACKAGE + ".ADDRESS_SERVICE_RECEIVER";

    protected ResultReceiver resultReceiver;

    public AddressIntentService(){
        super("AddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        resultReceiver = intent.getParcelableExtra(RECEIVER);

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location helpedUserLocation = intent.getParcelableExtra(LOCATION);
        double latitude = helpedUserLocation.getLatitude();
        double longitude = helpedUserLocation.getLongitude();

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        }
        catch(IOException ex){}
        catch(IllegalArgumentException ex){}

        if(addresses != null && addresses.size() > 0){
            Address address = addresses.get(0);
            ArrayList<String> addressPieces = new ArrayList<>();

            for(int i = 0; i < address.getMaxAddressLineIndex(); i++){
                addressPieces.add(address.getAddressLine(i));
            }

            deliverResultToReceiver(SUCCESS_RESULT_CODE, TextUtils.join(System.getProperty("line.separator"), addressPieces));
        }
    }

    private void deliverResultToReceiver(int resultCode, String result){
        Bundle bundle = new Bundle();
        bundle.putString(RESULT, result);
        resultReceiver.send(resultCode, bundle);
    }
}