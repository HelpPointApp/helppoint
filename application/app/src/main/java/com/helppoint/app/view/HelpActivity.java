package com.helppoint.app.view;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.helppoint.app.appcadeirantes.R;
import com.helppoint.app.manager.GPSManager;
import com.helppoint.app.model.Help;
import com.helppoint.app.model.User;
import com.helppoint.app.service.AddressIntentService;
import com.helppoint.app.wrappers.ProgressDialog;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HelpActivity extends AppCompatActivity {

    public static final String HELP_OBJECT_ID_KEY = "objectId";

    private TextView lblPersonName;
    private TextView lblTitle;
    private TextView lblAddress;
    private TextView lblIntervalPoints;
    private TextView btnHelp;

    private String helpedUserName;

    private ProgressDialog progressDialog;
    private Geocoder geocoder;
    private Help currentHelp;
    private ParseUser currentLoggedUser;
    private HandleCheckStatusHelp handleCheckStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        loadElementsFromXML();
        loadGeneralValues();
        loadAllServices();
        updateLabelsBasedOnHelpedUser();
        startFetchAddressService();

        onChangeActions();
    }

    private void loadElementsFromXML(){
        lblPersonName = (TextView) findViewById(R.id.lblPersonName);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblAddress = (TextView) findViewById(R.id.lblAddress);
        lblIntervalPoints = (TextView) findViewById(R.id.lblIntervalPoints);
        btnHelp = (TextView) findViewById(R.id.btnHelp);
    }

    private void loadGeneralValues(){
        currentHelp = Help.getByObjectId(getCurrentHelpObjectId());
        currentLoggedUser = User.getCurrentUser();
        ParseUser helpedUser = currentHelp.getHelpedParseUser();
        helpedUser.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if(e == null){
                    helpedUserName = object.getString("firstName");
                    updateLabelsBasedOnHelpedUser();
                }
            }
        });
    }

    private void loadAllServices(){
        progressDialog = ProgressDialog.getInstance();
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    private void updateLabelsBasedOnHelpedUser(){
        lblPersonName.setText(helpedUserName);
        lblTitle.setText("Oi herói! Você poderia ajudar o(a) " + helpedUserName + " a subir as escadas?");
        btnHelp.setText("Ser herói do(a) " + helpedUserName);

        try {
            double latitude = (getIntent().getExtras().getDouble("helpLocationLatitude"));
            double longitude = (getIntent().getExtras().getDouble("helpLocationLongitude"));
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                lblAddress.setText(returnedAddress.getAddressLine(0));
            } else {
                lblAddress.setText("Endereço não encontrado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startFetchAddressService(){
        /*
        Location userLocation = new Location("TEMP_PROVIDER");
        userLocation.setLatitude(-19.9523872);
        userLocation.setLongitude(-43.9696899);

        Intent intent = new Intent(this, AddressIntentService.class);
        intent.putExtra(AddressIntentService.RECEIVER, new AddressIntentServiceReceiver(new Handler()));
        intent.putExtra(AddressIntentService.LOCATION, userLocation);
        startService(intent);
        */
    }

    private String getCurrentHelpObjectId(){
        return getIntent().getExtras().getString(HELP_OBJECT_ID_KEY);
    }

    public void onChangeActions(){

        if (currentHelp.getHelperParseUser() == null) {
            btnHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.create(HelpActivity.this, "Ajuda", "Aguarde, o pedido está feito");

                    Help.UserRequestHelped(currentHelp.getObjectId(), currentLoggedUser, new Help.RequestHelpedCallback() {
                        @Override
                        public void requestHelper(Help responseHelp) {
                            progressDialog.hide();

                            if (responseHelp != null) {
                                currentHelp = responseHelp;
                                startHandler();
                                btnHelp.setVisibility(View.INVISIBLE);
                            } else {
                                finish();
                            }
                        }
                    });
                }
            });
        }else{
            if(areUsersEqual(currentHelp.getHelpedParseUser(), currentLoggedUser)) {
                btnHelp.setText("Finalizar");
                btnHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopHandler();
                        progressDialog.create(HelpActivity.this, "Finalizando", "Aguarde, estamos finalizando.");
                        currentHelp.finish(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                progressDialog.hide();

                                Intent data = new Intent();
                                data.putExtra("objectId", currentHelp.getObjectId());
                                setResult(Activity.RESULT_OK, data);
                                finish();
                            }
                        });
                    }
                });
            }else{
                startHandler();
                btnHelp.setVisibility(View.INVISIBLE);
            }
        }
    }

    private boolean areUsersEqual(ParseUser userT, ParseUser userP){
        if (userT == null) return false;
        return userT.getObjectId().equals(userP.getObjectId());
    }

    @Override
    public void onBackPressed(){
        if (!((areUsersEqual(currentHelp.getHelperParseUser(), this.currentLoggedUser) || areUsersEqual(currentHelp.getHelpedParseUser(), this.currentLoggedUser))
                && currentHelp.getStatus() == Help.STATUS.Helping)) {
            super.onBackPressed();
        }
    }

    public void startHandler(){
        if (handleCheckStatus != null && handleCheckStatus.isAlive()) {
            handleCheckStatus.interrupt();
        }

        handleCheckStatus = new HandleCheckStatusHelp(currentHelp);
        handleCheckStatus.start();
    }

    public void stopHandler(){
        if (handleCheckStatus != null && !handleCheckStatus.isInterrupted()){
            handleCheckStatus.interrupt();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        if (areUsersEqual(currentHelp.getHelperParseUser(), currentLoggedUser)){
            currentHelp.fetchInBackground(new GetCallback<Help>() {
                @Override
                public void done(Help object, ParseException e) {
                    if(object.getStatus() == Help.STATUS.Helping){
                        startHandler();
                    }
                }
            });
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        stopHandler();
    }

    @Override
    public void onStop(){
        super.onStop();
        stopHandler();
    }

    private class HandleCheckStatusHelp extends Thread{

        private Help help;

        public HandleCheckStatusHelp(Help help){
            super();
            this.help = help;
        }

        @Override
        public void run() {
            boolean running = true;
            Location userLocation;
            Help auxHelp;
            ParseGeoPoint parseGeoPoint;
            ParseUser parserUser = this.help.getHelpedParseUser();
            try {
                parserUser.fetch();

                while (running) {
                    auxHelp = help.fetch();

                    userLocation = GPSManager.getInstance(HelpActivity.this).getUserLocation();
                    parseGeoPoint = new ParseGeoPoint(
                            userLocation.getLatitude(), userLocation.getLongitude());
                    parserUser.put("lastPosition", parseGeoPoint);
                    parserUser.saveInBackground();

                    if (auxHelp.getStatus() == Help.STATUS.Finished) {
                        running = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }

                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                Log.e("thread check status", e.toString());
            }
        }
    }

    private class AddressIntentServiceReceiver extends ResultReceiver {
        AddressIntentServiceReceiver(Handler handler){
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData){
            lblAddress.setText(resultData.getString(AddressIntentService.RESULT));
        }
    }
}
