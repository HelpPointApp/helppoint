package com.example.reubert.appcadeirantes.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.reubert.appcadeirantes.R;

public class LoginActivity extends AppCompatActivity {

    public EditText edtUserName;
    public EditText edtPassword;
    public Button btnSignIn;
    public Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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
        public void onClick(View clickedView){}
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
