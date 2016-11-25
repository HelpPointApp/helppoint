package com.helppoint.app.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.helppoint.app.appcadeirantes.R;
import com.helppoint.app.factory.UserFactory;
import com.helppoint.app.model.User;
import com.helppoint.app.utilities.LayoutManager;
import com.parse.ParseException;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtFirstName;
    private EditText edtLastName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtPasswordAgain;
    private EditText edtCPF;
    private EditText edtBirthday;
    private ProgressDialog signUpProgressDialog;

    public Button btnCreateAccount;

    public Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loadElementsFromXML();
        normalizePasswordAppearance();
        createClickListeners();
    }


    public void loadElementsFromXML(){
        edtFirstName     = (EditText) findViewById(R.id.edtFirstName);
        edtLastName      = (EditText) findViewById(R.id.edtLastName);
        edtEmail         = (EditText) findViewById(R.id.edtEmail);
        edtPassword      = (EditText) findViewById(R.id.edtPassword);
        edtPasswordAgain = (EditText) findViewById(R.id.edtPasswordAgain);
        edtCPF           = (EditText) findViewById(R.id.edtCPF);
        edtBirthday      = (EditText) findViewById(R.id.edtBirthday);
        btnCreateAccount = (Button) findViewById(R.id.btnCreateAccount);
        toolbar          = (Toolbar) findViewById(R.id.toolbar);
    }


    public void normalizePasswordAppearance(){
        LayoutManager.normalizePasswordAppearance(edtPassword);
        LayoutManager.normalizePasswordAppearance(edtPasswordAgain);
    }


    public void createClickListeners(){
        btnCreateAccount.setOnClickListener(new SignUpButtonHandler());
    }


    private User createUser(){
        String firstName = edtFirstName.getText().toString();
        String lastName = edtLastName.getText().toString();
        String email = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String cpf = edtCPF.getText().toString();
        String birthday = edtBirthday.getText().toString();
        return UserFactory.create(
                firstName, lastName, email, password,
                cpf, birthday
        );
    }


    private void showRegistrationProgressDialog(){
        if(signUpProgressDialog == null){
            signUpProgressDialog = new ProgressDialog(this);
            signUpProgressDialog.setTitle("Registro");
            signUpProgressDialog.setMessage("Aguarde enquanto registramos seus dados.");
        }

        signUpProgressDialog.show();
    }


    private void hideRegistrationProgressDialog(){
        if(signUpProgressDialog != null){
            signUpProgressDialog.hide();
        }
    }


    public class SignUpButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){
            showRegistrationProgressDialog();
            User user = createUser();
            user.signUpInBackground(new SignUpEventHandler());
        }
    }


    public class SignUpEventHandler implements SignUpCallback {
        @Override
        public void done(ParseException e){
            hideRegistrationProgressDialog();

            if(e != null){
                AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
                alert.setTitle("Registro");
                alert.setMessage("Preencha todos os campos corretamente");
                alert.show();
                return;
            }

            Intent mapsActivity = new Intent(RegisterActivity.this, MapsActivity.class);
            mapsActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mapsActivity);
        }
    }
}
