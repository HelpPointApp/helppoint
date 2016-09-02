package com.example.reubert.appcadeirantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public String userName;
    public String userPassword;

    public Button signUp;
    public Button signIn;

    public TextView testName;
    public TextView testPassword;

    public EditText nameField;
    public EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userlogin);

        loadIds();

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btnSignIn:
                        setNamePassword();
                        break;
                    case R.id.btnSignUp:
                        loadUserRegisterScreen();
                        break;
                    default:
                        break;
                }
            }
        };

        signIn.setOnClickListener(clickListener);
        signUp.setOnClickListener(clickListener);
    }

    /**
     * This function below is to just make an easy test.
     */
    public void setNamePasswordTest(String userName,String userPassword){
        testName.setText(userName);
        testPassword.setText(userPassword);
    }

    /**
     * This function below is to set the userName and userPassword
     */
    public void setNamePassword(){
        userName     = nameField.getText().toString();
        userPassword = passwordField.getText().toString();
        setNamePasswordTest(userName, userPassword);
    }

    /**
     * This function below, allow us to load new screen (Register screen).
     */
    public void loadUserRegisterScreen(){
        Intent screenRegister = new Intent(MainActivity.this, UserRegister.class);
        startActivity(screenRegister);
    }

    /**
     * This function below was made to get IDs
     */
    public void loadIds(){
        this.testName      = (TextView) findViewById(R.id.txtUserNameTest);
        this.testPassword  = (TextView) findViewById(R.id.txtUserPasswordTest);
        this.signIn        = (Button) findViewById(R.id.btnSignIn);
        this.signUp        = (Button) findViewById(R.id.btnSignUp);
        this.nameField     = (EditText) findViewById(R.id.edtUserName);
        this.passwordField = (EditText) findViewById(R.id.edtPassword);
    }
}
