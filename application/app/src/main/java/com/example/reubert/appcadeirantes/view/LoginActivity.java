package com.example.reubert.appcadeirantes.view;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public EditText edtUserName;
    public EditText edtPassword;
    public Button btnSignIn;
    public Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseObject.registerSubclass(Help.class);
        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("Lw7z2ythkSBQRww4bn9Rb5Zb15Ss202TYgrcfFdE")
                .clientKey("fNt452wjdnk0sxbrA5SixH8DhFNJLCU0BggEiYAO")
                .server("https://parseapi.back4app.com").build()
        );

        ParseUser user = User.getCurrentUser();
        if (user != null) {
            Intent mapsActivity = new Intent(LoginActivity.this, MapsActivity.class);
            mapsActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mapsActivity);
            finish();
        }

        setContentView(R.layout.activity_login);

        loadAllViewElements();
        createClickListeners();
    }


    public void loadAllViewElements(){
        this.btnSignIn = (Button) findViewById(R.id.btnSignIn);
        this.btnSignUp = (Button) findViewById(R.id.btnSignUp);
        this.edtUserName = (EditText) findViewById(R.id.edtUserName);
        this.edtPassword = (EditText) findViewById(R.id.edtPassword);
    }


    public void createClickListeners(){
        btnSignIn.setOnClickListener(new SignInButtonHandler());
        btnSignUp.setOnClickListener(new SignUpButtonHandler());
    }


    public class SignInButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){
            EditText userName = (EditText) findViewById(R.id.edtUserName);
            EditText password = (EditText) findViewById(R.id.edtPassword);
            try {
                ParseUser parseUser = User.logIn(userName.getText().toString(), password.getText().toString());
                Intent mapsActivity = new Intent(LoginActivity.this, MapsActivity.class);
                mapsActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mapsActivity);
            }catch(Exception e){
                AlertDialog.Builder builder = new AlertDialog.Builder(clickedView.getContext());
                builder.setTitle("login");
                builder.setMessage("username ou password incorreto.");
                builder.show();
            }
        }
    }


    public class SignUpButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){
            loadUserRegisterScreen();
        }

        private void loadUserRegisterScreen(){
            Intent screenRegister = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(screenRegister);
        }
    }
}
