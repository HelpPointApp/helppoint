package com.example.reubert.appcadeirantes.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.ParseException;
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

        storeUsefulElementsInProperties();
        updateLabelsBasedOnUser();

        final String objectId = getIntent().getExtras().getString("objectId");
        final Button buttonHelp = (Button) findViewById(R.id.btnHelped);

        this.user = User.getCurrentUser();
        this.help = Help.getHelp(objectId);

        onChangeActions();
    }

    public void storeUsefulElementsInProperties(){
        lblPersonName = (TextView) findViewById(R.id.lblPersonName);
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblAddress = (TextView) findViewById(R.id.lblAddress);
        lblIntervalPoints = (TextView) findViewById(R.id.lblIntervalPoints);
    }

    public void updateLabelsBasedOnUser(){
        final String helpObjectId = getIntent().getExtras().getString("objectId");
        final Help help = Help.getHelp(helpObjectId);
        //final User user = help.getUserTarget();

        //lblPersonName.setText(user.getFirstName());
    }

    public void onChangeActions(){
        final Button buttonHelp = (Button) findViewById(R.id.btnHelped);
        final ProgressDialog progressDialog = new ProgressDialog(this);

        if (help.getUserHelp() == null) {
            buttonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog.setTitle("Ajuda");
                    progressDialog.setMessage("Aguarde, o pedido está feito");
                    progressDialog.show();

                    Help.UserRequestHelped(help.getObjectId(), user, new Help.RequestHelpedCallback() {
                        @Override
                        public void requestHelper(Help _help) {
                            progressDialog.dismiss();
                            if (_help != null) {
                                help = _help;
                                handleCheckStatus = new HandleCheckStatusHelp();
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
            handleCheckStatus = new HandleCheckStatusHelp();
        }
        handleCheckStatus.start();
    }

    @Override
    public void onResume(){
        super.onResume();
/*        Help auxHelp = Help.getHelp(objectId);
        if (isEqualUser(help.getUserHelp(), this.user) && help.getStatus() == Help.STATUS.Helping) {
            startThread();
        }else{
            finish();
        }*/
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
        @Override
        public void run() {
            boolean running = true;
            final Help _help = help;

            Log.e("thread", String.valueOf(_help.getObjectId()));

            while (running) {
                try {
                    Help help = Help.getHelp(_help.getObjectId());

                    if (help.getStatus() == Help.STATUS.Finished) {
                        running = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }

                    Thread.sleep(5000);
                } catch (Exception e) {
                    Log.e("thread", e.toString());
                }
            }
        }
    }
}
