package com.example.reubert.appcadeirantes.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.reubert.appcadeirantes.R;
import com.example.reubert.appcadeirantes.manager.ServerManager;
import com.example.reubert.appcadeirantes.model.User;
import com.example.reubert.appcadeirantes.utilities.LayoutManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private TextView lblSignUp;
    private Button btnLogin;

    private ServerManager serverManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isUserAlreadyLogged()){
            startMapsActivity();
            finish();
        }

        setContentView(R.layout.activity_login);
        loadAllServices();
        loadAllViewElements();
        normalizePasswordAppearance();
        createClickListeners();
    }

    private boolean isUserAlreadyLogged(){
        ParseUser user = User.getCurrentUser();
        return user != null;
    }

    private void startMapsActivity(){
        Intent mapsActivity = new Intent(LoginActivity.this, MapsActivity.class);
        mapsActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mapsActivity);
    }

    private void loadAllServices(){
        serverManager = ServerManager.getInstance();
    }

    private void loadAllViewElements(){
        this.lblSignUp   = (TextView) findViewById(R.id.lblSignUp);
        this.btnLogin    = (Button) findViewById(R.id.btnLogin);
        this.edtEmail    = (EditText) findViewById(R.id.edtEmail);
        this.edtPassword = (EditText) findViewById(R.id.edtPassword);
    }


    private void normalizePasswordAppearance(){
        LayoutManager.normalizePasswordAppearance(edtPassword);
    }


    private void createClickListeners(){
        lblSignUp.setOnClickListener(new SignUpLabelHandler());
        btnLogin.setOnClickListener(new SignInButtonHandler());
    }

    private class SignInButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View clickedView){
            final View view = clickedView;
            final ProgressDialog progressDialog = new ProgressDialog(view.getContext());

            EditText userName = (EditText) findViewById(R.id.edtEmail);
            EditText password = (EditText) findViewById(R.id.edtPassword);

            progressDialog.setTitle("Autenticando");
            progressDialog.setMessage("Aguarde enquanto realizamos a autenticação.");
            progressDialog.show();

            User.logInInBackground(
                userName.getText().toString(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        progressDialog.dismiss();
                        if (user != null) {
                            Intent mapsActivity = new Intent(
                                    LoginActivity.this, MapsActivity.class);
                            mapsActivity.setFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(mapsActivity);
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                            builder.setTitle("Falha ao entrar");
                            builder.setMessage("E-mail ou senha incorreta.");
                            builder.show();
                        }
                    }
                });
        }
    }

    private class SignUpLabelHandler implements View.OnClickListener {
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
