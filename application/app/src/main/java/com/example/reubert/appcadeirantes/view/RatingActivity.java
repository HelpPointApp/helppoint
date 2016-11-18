package com.example.reubert.appcadeirantes.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.model.Rating;

public class RatingActivity extends AppCompatActivity {

    private TextView lblTitle;
    private TextView lblSubtitle;
    private Button btnRate;
    private RatingBar rtbRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        loadElementsFromXML();
        createClickListeners();
    }

    public void loadElementsFromXML(){
        lblTitle = (TextView) findViewById(R.id.lblTitle);
        lblSubtitle = (TextView) findViewById(R.id.lblSubtitle);
        rtbRatingBar = (RatingBar) findViewById(R.id.ratingStars);
        btnRate = (Button) findViewById(R.id.btnRate);
    }

    public void createClickListeners(){
        btnRate.setOnClickListener(new RateButtonHandler());
    }

    public class RateButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){
            Rating rating = new Rating();
            rating.setHelp(null); // Daqui a pouco pego esse help
            rating.setHelperUser(null);
            rating.setStars(rtbRatingBar.getNumStars());
        }
    }
}
