package com.br.frases.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.br.frases.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import helpers.ConfiguracaoFirebase;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private Button btnEntrar;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inicializaComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        verificarUsuarioLogado();

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etEmail.getText().toString();
                String senha = etSenha.getText().toString();

                if (email.isEmpty() || senha.isEmpty()){

                    Toast.makeText(LoginActivity.this, "Os campos email e senha são obrigatórios!", Toast.LENGTH_LONG).show();

                }else{
                    autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                startActivity(new Intent(getBaseContext(), HomeActivity.class));
                                finish();

                            }else{
                                Toast.makeText(LoginActivity.this, "Erro ao fazer login!" + task.getException(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    private void verificarUsuarioLogado() {

        FirebaseUser usuarioLogado = autenticacao.getCurrentUser();
        if( usuarioLogado != null){

            startActivity(new Intent(getBaseContext(), HomeActivity.class));
        }

    }

    private void inicializaComponentes() {
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);

        btnEntrar = findViewById(R.id.btnEntrar);
    }

    public void chamaCadastro(View view) {
        startActivity(new Intent(getBaseContext(), CadastroActivity.class));
    }
}