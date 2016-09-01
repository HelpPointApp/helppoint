package com.example.reubert.appcadeirantes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public String nome_usuario;
    public String senha;

    public Button cadastrar;
    public Button entrar;

    public TextView nome_teste;
    public TextView senha_teste;

    public EditText campo_nome;
    public EditText campo_senha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        carregaIds();

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.btnEntrar:
                        colocaNomeESenha();
                        break;
                    case R.id.btnCadastrar:
                        carregaTelaCadastro();
                        break;
                    default:
                        break;
                }
            }
        };

        entrar.setOnClickListener(clickListener);
        cadastrar.setOnClickListener(clickListener);
    }

    public void colocaNomeSenhaTeste(String nomeUsuario,String senhaUsuario){
        nome_teste.setText(nomeUsuario);
        senha_teste.setText(senhaUsuario);
    }

    public void colocaNomeESenha(){
        nome_usuario = campo_nome.getText().toString();
        senha        = campo_senha.getText().toString();
        colocaNomeSenhaTeste(nome_usuario, senha);
    }

    public void carregaTelaCadastro(){
        Intent telaCadastro = new Intent(MainActivity.this, user_register.class);
        startActivity(telaCadastro);
    }

    public void carregaIds(){
        this.nome_teste  = (TextView) findViewById(R.id.txtNomeTeste);
        this.senha_teste = (TextView) findViewById(R.id.txtSenhaTeste);
        this.entrar      = (Button) findViewById(R.id.btnEntrar);
        this.cadastrar   = (Button) findViewById(R.id.btnCadastrar);
        this.campo_nome  = (EditText) findViewById(R.id.edtNomeUsuario);
        this.campo_senha = (EditText) findViewById(R.id.edtSenha);
    }
}
