package com.helppoint.app.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.helppoint.app.appcadeirantes.R;
import com.helppoint.app.model.Help;
import com.helppoint.app.wrappers.ProgressDialog;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class RatingActivity extends AppCompatActivity {

    public TextView lblTitle;
    public TextView lblSubtitle;
    public RatingBar rtbRatingBar;
    public Button btnRate;
    private ProgressDialog progressDialog;
    private Help help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        progressDialog = ProgressDialog.getInstance();

        loadElementsFromXML();
        createClickListeners();
        loadHelpDisplay();
    }

    public void loadElementsFromXML(){
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblSubtitle = (TextView) findViewById(R.id.lblSubtitle);
        rtbRatingBar = (RatingBar) findViewById(R.id.ratingStars);
        btnRate = (Button) findViewById(R.id.btnRate);
    }

    private void loadHelpDisplay(){
        help = Help.getActive();
        help.fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                ParseUser user = help.getHelperParseUser();

                user.fetchIfNeededInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser object, ParseException e) {
                        lblTitle.setText("Como você avalia a ajuda do(a) " +object.getString("firstName") +" ?");
                        lblSubtitle.setText("Sua avaliação é crucial para que "+object.getString("firstName") +" receba uma pontuação justa pela ajuda.");
                    }
                });
            }
        });
    }

    public void createClickListeners(){
        btnRate.setOnClickListener(new RateButtonHandler());
    }

    public class RateButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){

            RatingBar rtbRatingBar = (RatingBar) findViewById(R.id.ratingStars);
            progressDialog.create(clickedView.getContext(), "Avaliando", "aguarde estamos salvando sua avaliação");

            help.rating((int)rtbRatingBar.getRating(), new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    progressDialog.hide();
                    Intent data = new Intent();
                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            });
        }
    }
}
