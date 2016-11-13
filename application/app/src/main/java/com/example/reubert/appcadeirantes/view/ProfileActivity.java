package com.example.reubert.appcadeirantes.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.reubert.appcadeirantes.R;

public class ProfileActivity extends AppCompatActivity {

    public TextView txtEmail;
    public TextView txtNascimento;
    public TextView txtCpf;

    public Button btnVoltar;

    public ImageView imgPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        loadAllViewElements();
    }

    public void loadAllViewElements(){
        txtEmail = (TextView) findViewById(R.id.txtProfileEmail);
        txtNascimento = (TextView) findViewById(R.id.txtProfileNascimento);
        txtCpf        = (TextView) findViewById(R.id.txtProfileCpf);
        btnVoltar     = (Button) findViewById(R.id.btnProfileVoltar);
        imgPerfil     = (ImageView) findViewById(R.id.imgProfile);
    }
}
