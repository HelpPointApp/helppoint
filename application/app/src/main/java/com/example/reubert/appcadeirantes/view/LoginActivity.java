package com.example.reubert.appcadeirantes.view;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.model.Help;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public EditText edtEmail;
    public EditText edtPassword;
    public TextView lblSignUp;
    public Button btnLogin;

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
        this.lblSignUp = (TextView) findViewById(R.id.lblSignUp);
        this.btnLogin = (Button) findViewById(R.id.btnLogin);
        this.edtEmail = (EditText) findViewById(R.id.edtEmail);
        this.edtPassword = (EditText) findViewById(R.id.edtPassword);
    }


    public void createClickListeners(){
        lblSignUp.setOnClickListener(new SignUpLabelHandler());
        btnLogin.setOnClickListener(new SignInButtonHandler());
    }


    public class SignInButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){
            EditText userName = (EditText) findViewById(R.id.edtEmail);
            EditText password = (EditText) findViewById(R.id.edtPassword);
            try {
                ParseUser parseUser = User.logIn(userName.getText().toString(), password.getText().toString());
                Intent mapsActivity = new Intent(LoginActivity.this, MapsActivity.class);
                mapsActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mapsActivity);
            } catch(Exception e){
                AlertDialog.Builder builder = new AlertDialog.Builder(clickedView.getContext());
                builder.setTitle("Falha ao entrar");
                builder.setMessage("E-mail ou senha incorreta.");
                builder.show();
            }
        }
    }

    public class SignUpLabelHandler implements View.OnClickListener {
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
