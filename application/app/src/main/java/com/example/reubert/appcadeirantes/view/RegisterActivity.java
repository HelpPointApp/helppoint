package com.example.reubert.appcadeirantes.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.model.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

    public EditText edtUserName;
    public EditText edtEmail;
    public EditText edtPassword;
    public EditText edtPasswordAgain;
    public EditText edtCPF;
    public EditText edtBirthday;

    public Button btnSignUp;
    public Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loadAllViewElements();
        createClickListeners();
    }


    public void loadAllViewElements(){
//        edtUserName      = (EditText) findViewById(R.id.edtUserName);
        edtEmail         = (EditText) findViewById(R.id.edtEmail);
        edtPassword      = (EditText) findViewById(R.id.edtPassword);
        edtPasswordAgain = (EditText) findViewById(R.id.edtPasswordAgain);
//        edtCPF           = (EditText) findViewById(R.id.edtCPF);
//        edtBirthday      = (EditText) findViewById(R.id.edtBirthday);
        btnSignUp        = (Button) findViewById(R.id.btnLogin);
        btnCancel        = (Button) findViewById(R.id.btnCancel);
    }


    public void createClickListeners(){
        btnSignUp.setOnClickListener(new SignUpButtonHandler());
        btnCancel.setOnClickListener(new CancelButtonHandler());
    }


    public class CancelButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){
            finishActivity(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        }
    }


    public class SignUpButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){
//            EditText edtUserName      = (EditText) findViewById(R.id.edtUserName);
            EditText edtEmail         = (EditText) findViewById(R.id.edtEmail);
            EditText edtPassword      = (EditText) findViewById(R.id.edtPassword);
            EditText edtPasswordAgain = (EditText) findViewById(R.id.edtPasswordAgain);
//            EditText edtCPF           = (EditText) findViewById(R.id.edtCPF);
//            EditText edtBirthday      = (EditText) findViewById(R.id.edtBirthday);
            final View view = clickedView;
            final ProgressDialog progressDialog = new ProgressDialog(clickedView.getContext());
            progressDialog.setTitle("Registro");
            progressDialog.setMessage("Aguarde enquanto registramos seus dados.");
            progressDialog.show();

            User user = new User();
            user.setUsername(edtEmail.getText().toString());
            user.setEmail(edtEmail.getText().toString());
            user.setPassword(edtPassword.getText().toString());
            user.setPoints(0);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    progressDialog.dismiss();
                    if (e == null) {
                        Intent mapsActivity = new Intent(RegisterActivity.this, MapsActivity.class);
                        mapsActivity.setFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mapsActivity);
                    }else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                        alert.setTitle("Registro");
                        alert.setMessage("Preencha todos os campos corretamente");
                        alert.show();
                    }
                }
            });

        }
    }
}
