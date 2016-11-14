package com.example.reubert.appcadeirantes.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class HelpActivity extends AppCompatActivity {

    private Help help;
    private ParseUser user;

    private boolean isUserTarget(Help help, ParseUser user){
        ParseUser userTarget = help.getUserTarget();
        return userTarget.getObjectId().equals(user.getObjectId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        final String objectId = getIntent().getExtras().getString("objectId");
        final Button buttonHelp = (Button) findViewById(R.id.btnHelped);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        user = User.getCurrentUser();

        help = Help.getHelp(objectId);

        if (help.getUserHelp() == null) {
            buttonHelp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Help.UserRequestHelped(objectId, user, new Help.RequestHelpedCallback() {
                        @Override
                        public void requestHelped(Help help) {
                            if (help != null) {
                                buttonHelp.setVisibility(View.INVISIBLE);
                            } else {
                                finish();
                            }
                        }
                    });
                }
            });
        }else{
            if(this.isUserTarget(help, user)) {
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
                                finish();
                            }
                        });
                    }
                });
            }else{
                buttonHelp.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed(){
        if (help.getUserHelp() == this.user || !this.isUserTarget(help, user)) {
            super.onBackPressed();
        }
    }
}
