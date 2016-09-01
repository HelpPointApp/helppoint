package com.example.reubert.appcadeirantes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class user_register extends AppCompatActivity {

    public EditText nome_usuario;
    public EditText email;
    public EditText senha;
    public EditText senha_comparacao;
    public EditText cpf;
    public EditText nascimento;

    public Button registrar;
    public Button cancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_register);

        carregaIds();

        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                switch(view.getId()){
                    case R.id.btnRegistrar:
                        //empty
                        break;
                    case R.id.btnCancelar:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
        registrar.setOnClickListener(clickListener);
        cancelar.setOnClickListener(clickListener);
    }

    public void carregaIds(){
        nome_usuario     = (EditText) findViewById(R.id.edtNomeUsuarioC);
        email            = (EditText) findViewById(R.id.edtEmailC);
        senha            = (EditText) findViewById(R.id.txtSenhaC);
        senha_comparacao = (EditText) findViewById(R.id.txtSenhaComparacaoC);
        cpf              = (EditText) findViewById(R.id.edtCpfC);
        nascimento       = (EditText) findViewById(R.id.edtNascimentoC);
        registrar        = (Button) findViewById(R.id.btnRegistrar);
        cancelar         = (Button) findViewById(R.id.btnCancelar);
    }
}
