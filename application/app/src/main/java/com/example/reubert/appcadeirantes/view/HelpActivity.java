package com.example.reubert.appcadeirantes.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.manager.GPSManager;
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.example.reubert.appcadeirantes.wrappers.ProgressDialog;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class HelpActivity extends AppCompatActivity {

    public static final String HELP_OBJECT_ID_KEY = "objectId";

    private TextView lblPersonName;
    private TextView lblTitle;
    private TextView lblAddress;
    private TextView lblIntervalPoints;

    private ProgressDialog progressDialog;

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

        onChangeActions();
    }

    private void loadElementsFromXML(){
        lblPersonName = (TextView) findViewById(R.id.lblPersonName);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblAddress = (TextView) findViewById(R.id.lblAddress);
        lblIntervalPoints = (TextView) findViewById(R.id.lblIntervalPoints);
    }

    private void loadGeneralValues(){
        currentHelp = Help.getByObjectId(getCurrentHelpObjectId());
        currentLoggedUser = User.getCurrentUser();
    }

    private void loadAllServices(){
        progressDialog = ProgressDialog.getInstance();
    }

    private void updateLabelsBasedOnHelpedUser(){
        ParseUser helpedUser = currentHelp.getHelpedParseUser();
    }

    private String getCurrentHelpObjectId(){
        return getIntent().getExtras().getString(HELP_OBJECT_ID_KEY);
    }

    public void onChangeActions(){
        final Button buttonHelp = (Button) findViewById(R.id.btnHelped);

        if (currentHelp.getHelperParseUser() == null) {
            buttonHelp.setOnClickListener(new View.OnClickListener() {
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
                                buttonHelp.setVisibility(View.INVISIBLE);
                            } else {
                                finish();
                            }
                        }
                    });
                }
            });
        }else{
            if(areUsersEqual(currentHelp.getHelpedParseUser(), currentLoggedUser)) {
                buttonHelp.setText("Finalizar");
                buttonHelp.setOnClickListener(new View.OnClickListener() {
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
                buttonHelp.setVisibility(View.INVISIBLE);
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
}
