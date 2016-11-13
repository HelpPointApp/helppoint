package com.example.reubert.appcadeirantes.view;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.model.User;

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
        edtUserName      = (EditText) findViewById(R.id.edtUserName);
        edtEmail         = (EditText) findViewById(R.id.edtEmail);
        edtPassword      = (EditText) findViewById(R.id.edtPassword);
        edtPasswordAgain = (EditText) findViewById(R.id.edtPasswordAgain);
//        edtCPF           = (EditText) findViewById(R.id.edtCPF);
//        edtBirthday      = (EditText) findViewById(R.id.edtBirthday);
        btnSignUp        = (Button) findViewById(R.id.btnSignUp);
        btnCancel        = (Button) findViewById(R.id.btnCancel);
    }


    public void createClickListeners(){
        btnSignUp.setOnClickListener(new SignUpButtonHandler());
        btnCancel.setOnClickListener(new CancelButtonHandler());
    }


    public class CancelButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){

        }
    }


    public class SignUpButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){
            EditText edtUserName      = (EditText) findViewById(R.id.edtUserName);
            EditText edtEmail         = (EditText) findViewById(R.id.edtEmail);
            EditText edtPassword      = (EditText) findViewById(R.id.edtPassword);
            EditText edtPasswordAgain = (EditText) findViewById(R.id.edtPasswordAgain);
//            EditText edtCPF           = (EditText) findViewById(R.id.edtCPF);
//            EditText edtBirthday      = (EditText) findViewById(R.id.edtBirthday);

            try {
                User user = new User();
                user.setUsername(edtUserName.getText().toString());
                user.setEmail(edtEmail.getText().toString());
                user.setPassword(edtPassword.getText().toString());
                user.setPoints(0);
                user.signUp();
            }catch (Exception e){
                AlertDialog.Builder alert = new AlertDialog.Builder(clickedView.getContext());
                alert.setTitle("Registro");
                alert.setMessage("Preencha todos os campos corretamente");
                alert.show();
            }
        }
    }
}
