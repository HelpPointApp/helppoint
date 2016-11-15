package com.example.reubert.appcadeirantes.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.model.Avaliation;
import com.example.reubert.appcadeirantes.model.User;

public class RatingActivity extends AppCompatActivity {

    public TextView lblTitle;
    public TextView lblSubtitle;

    public RatingBar ratingStars;

    public Button btnRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        loadAllViewElements();
        createClickListeners();

        User user = new User();
        lblTitle.setText("Como você avalia a ajuda do(a)" +user.getFirstName()+" ?");
        lblSubtitle.setText("Sua avaliação é crucial para que "+user.getFirstName()+" receba uma pontuação justa pela ajuda.");
    }

    public void loadAllViewElements(){
        lblTitle       = (TextView) findViewById(R.id.lblTitle);
        lblSubtitle      = (TextView) findViewById(R.id.lblSubtitle);
        ratingStars     = (RatingBar) findViewById(R.id.ratingStars);
        btnRate = (Button) findViewById(R.id.btnRate);
    }

    public void createClickListeners(){
        btnRate.setOnClickListener(new sendAvaliationHandler());
    }

    public class sendAvaliationHandler implements View.OnClickListener{
        @Override
        public void onClick(View clickedView){
            RatingBar rtbRatingBar = (RatingBar) findViewById(R.id.ratingStars);

            final View view = clickedView;
            Avaliation avaliation = new Avaliation();

            avaliation.setRating(rtbRatingBar.getNumStars());
        }
    }
}
