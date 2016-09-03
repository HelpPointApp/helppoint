package com.example.reubert.appcadeirantes.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    public EditText userName;
    public EditText email;
    public EditText password;
    public EditText testPassword;
    public EditText cpf;
    public EditText birth;

    public Button register;
    public Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userregister);

        loadIds();

        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.btnRegisterC:
                        //empty
                        break;
                    case R.id.btnCancelC:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
        register.setOnClickListener(clickListener);
        cancel.setOnClickListener(clickListener);
    }

    /**
     * This function allow to get IDs
     */
    public void loadIds(){
        userName     = (EditText) findViewById(R.id.edtUserNameC);
        email        = (EditText) findViewById(R.id.edtUserEmailC);
        password     = (EditText) findViewById(R.id.txtUserPasswordC);
        testPassword = (EditText) findViewById(R.id.txtUserTestPasswordC);
        cpf          = (EditText) findViewById(R.id.edtUserCpfC);
        birth        = (EditText) findViewById(R.id.edtUserBirthC);
        register     = (Button) findViewById(R.id.btnRegisterC);
        cancel       = (Button) findViewById(R.id.btnCancelC);
    }
}
