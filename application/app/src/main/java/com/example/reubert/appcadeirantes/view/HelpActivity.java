package com.example.reubert.appcadeirantes.view;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.example.reubert.appcadeirantes.model.Avaliation;
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class HelpActivity extends AppCompatActivity {

    private TextView lblPersonName;
    private TextView lblTitle;
    private TextView lblAddress;
    private TextView lblIntervalPoints;

    private Help help;
    private ParseUser user;
    private HandleCheckStatusHelp handleCheckStatus;

    private boolean isEqualUser(ParseUser userT, ParseUser userP){
        if (userT == null) return false;
        return userT.getObjectId().equals(userP.getObjectId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        loadElementsFromXML();
        updateLabelsBasedOnUser();

        String objectId = getIntent().getExtras().getString("objectId");
        this.help = Help.getHelp(objectId);
        this.user = User.getCurrentUser();

        onChangeActions();
    }

    public void loadElementsFromXML(){
        lblPersonName = (TextView) findViewById(R.id.lblPersonName);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblAddress = (TextView) findViewById(R.id.lblAddress);
        lblIntervalPoints = (TextView) findViewById(R.id.lblIntervalPoints);
    }

    public void updateLabelsBasedOnUser(){}

    public void onChangeActions(){
        final Button buttonHelp = (Button) findViewById(R.id.btnHelped);

        if (help.getHelperParseUser() == null) {

            buttonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ProgressDialog progressDialog = new ProgressDialog(HelpActivity.this);
                    progressDialog.setTitle("Ajuda");
                    progressDialog.setMessage("Aguarde, o pedido está feito");
                    progressDialog.show();

                    Help.UserRequestHelped(help.getObjectId(), user, new Help.RequestHelpedCallback() {
                        @Override
                        public void requestHelper(Help responseHelp) {
                            progressDialog.dismiss();
                            if (responseHelp != null) {
                                help = responseHelp;
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
            if(isEqualUser(help.getHelpedParseUser(), user)) {
                buttonHelp.setText("Finalizar");
                buttonHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final ProgressDialog progressDialog = new ProgressDialog(HelpActivity.this);
                        progressDialog.setTitle("Finalizando");
                        progressDialog.setMessage("Aguarde estamos finalizando.");
                        progressDialog.show();

                        stopHandler();

                        help.finish(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                progressDialog.dismiss();
                                Intent data = new Intent();
                                data.putExtra("objectId", help.getObjectId());
                                data.putExtra("type", 2);
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

    @Override
    public void onBackPressed(){
        if (!((isEqualUser(help.getHelperParseUser(), this.user) || isEqualUser(help.getHelpedParseUser(), this.user))
                && help.getStatus() == Help.STATUS.Helping)) {
            super.onBackPressed();
        }
    }

    public void startHandler(){
        if (handleCheckStatus != null && handleCheckStatus.isAlive()) {
            handleCheckStatus.interrupt();
        }
        handleCheckStatus = new HandleCheckStatusHelp(help);
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

        final Context context = this;

        if (isEqualUser(help.getHelperParseUser(), user)){
            help.fetchInBackground(new GetCallback<Help>() {
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
            Help auxHelp;
            Location userLocation;
            ParseGeoPoint parseGeoPoint;
            ParseUser parserUser = this.help.getHelpedParseUser();
            try {
                parserUser.fetch();

                while (running) {
                    auxHelp = this.help.fetch();
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
