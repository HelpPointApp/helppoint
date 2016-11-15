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
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class HelpActivity extends AppCompatActivity {

    private TextView lblPersonName;
    private TextView lblTitle;
    private TextView lblAddress;
    private TextView lblIntervalPoints;
    private Context context;

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

        storeUsefulElementsInProperties();
        updateLabelsBasedOnUser();

        String objectId = getIntent().getExtras().getString("objectId");
        this.help = Help.getHelp(objectId);
        this.user = User.getCurrentUser();
        this.context = this;

        onChangeActions();
    }

    public void storeUsefulElementsInProperties(){
        lblPersonName = (TextView) findViewById(R.id.lblPersonName);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblAddress = (TextView) findViewById(R.id.lblAddress);
        lblIntervalPoints = (TextView) findViewById(R.id.lblIntervalPoints);
    }

    public void updateLabelsBasedOnUser(){
        String helpObjectId = getIntent().getExtras().getString("objectId");
        Help help = Help.getHelp(helpObjectId);
        final ParseUser user = help.getUserTarget();
        user.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                lblPersonName.setText(user.getString("firstName"));
            }
        });
    }

    public void onChangeActions(){
        final Button buttonHelp = (Button) findViewById(R.id.btnHelped);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        if (help.getUserHelp() == null) {
            buttonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Context context = view.getContext();
                    progressDialog.setTitle("Ajuda");
                    progressDialog.setMessage("Aguarde, o pedido está feito");
                    progressDialog.show();

                    Help.UserRequestHelped(help.getObjectId(), user, new Help.RequestHelpedCallback() {
                        @Override
                        public void requestHelper(Help responseHelp) {
                            progressDialog.dismiss();
                            if (responseHelp != null) {
                                help = responseHelp;
                                handleCheckStatus = new HandleCheckStatusHelp(responseHelp, context);
                                handleCheckStatus.start();
                                buttonHelp.setVisibility(View.INVISIBLE);
                            } else {
                                finish();
                            }
                        }
                    });
                }
            });
        }else{
            if(isEqualUser(help.getUserTarget(), user)) {
                buttonHelp.setText("Finalizar");
                buttonHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progressDialog.setTitle("Finalizando");
                        progressDialog.setMessage("Aguarde estamos finalizando.");
                        progressDialog.show();

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
                startThread();
                buttonHelp.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed(){
        if (!((isEqualUser(help.getUserHelp(), this.user) || isEqualUser(help.getUserTarget(), this.user))
                && help.getStatus() == Help.STATUS.Helping)) {
            super.onBackPressed();
        }
    }

    public void startThread(){
        if (handleCheckStatus == null || handleCheckStatus.isInterrupted()) {
            handleCheckStatus = new HandleCheckStatusHelp(help, this);
        }
        handleCheckStatus.start();
    }

    @Override
    public void onResume(){
        super.onResume();

        if (handleCheckStatus == null || handleCheckStatus.isInterrupted()){
            if (isEqualUser(help.getUserHelp(), user)){
                help.fetchInBackground(new GetCallback<Help>() {
                    @Override
                    public void done(Help object, ParseException e) {
                        if(object.getStatus() == Help.STATUS.Helping){
                            handleCheckStatus.start();
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if (handleCheckStatus != null && handleCheckStatus.isAlive()){
            handleCheckStatus.interrupt();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if (handleCheckStatus != null && handleCheckStatus.isAlive()){
            handleCheckStatus.interrupt();
        }
    }

    private class HandleCheckStatusHelp extends Thread{

        private Help help;
        private Context context;

        public HandleCheckStatusHelp(Help help, Context context){
            super();
            this.help = help;
            this.context = context;
        }

        @Override
        public void run() {
            boolean running = true;
            Help auxHelp;
            Location userLocation;
            ParseUser parserUser = this.help.getUserTarget();
            try {
                User user = (User) ParseQuery.getQuery("User").get(parserUser.getObjectId());

                while (running) {
                    auxHelp = this.help.fetch();
                    userLocation = GPSManager.getInstance(context).getUserLocation();
                    user.setLastPosition(userLocation.getLatitude(), userLocation.getLongitude());
                    user.saveInBackground();

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
                Log.e("thread", e.toString());
            }
        }
    }
}
