package com.example.reubert.appcadeirantes.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.ParseUser;

public class HelpActivity extends AppCompatActivity {

    private Help help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        final String objectId = getIntent().getExtras().getString("objectId");
        final ParseUser user = User.getCurrentUser();
        final Button buttonHelp = (Button) findViewById(R.id.btnHelped);
        help = Help.getHelp(objectId);
        buttonHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Help.UserRequestHelped(objectId, user, new Help.RequestHelpedCallback(){
                    @Override
                    public void requestHelped(Help help){
                        if(help != null) {
                            buttonHelp.setVisibility(View.INVISIBLE);
                        }else{
                            finish();
                        }
                    }
                });
            }
        });
    }



}
