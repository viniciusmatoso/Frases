package com.br.frases.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.br.frases.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import helpers.ConfiguracaoFirebase;
import helpers.UsuarioFirebase;
import model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText etNome, etIdade, etEmail, etSenha;
    private Button btnCadastrar;
    private RadioGroup rgSexo;
    private RadioButton rbMasc, rbFem;

    private FirebaseAuth autenticacao;

    private String idUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializaComponentes();

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etEmail.getText().toString();
                String senha = etSenha.getText().toString();
                String nome = etNome.getText().toString();
                String idade = etIdade.getText().toString();
                String sexo = "";

                if(rbMasc.isChecked()){
                    sexo = "Masculino";
                }else{
                    sexo = "Feminino";
                }

                if(!nome.isEmpty() || !idade.isEmpty() || !sexo.isEmpty() || !email.isEmpty() || !senha.isEmpty()){

                    String finalSexo = sexo;

                    autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(CadastroActivity.this,
                                        "Cadastro realizado com sucesso!",
                                        Toast.LENGTH_SHORT).show();

                                idUsuario = UsuarioFirebase.getIdUsuario();

                                Usuario usuario = new Usuario();
                                usuario.setIdade(idade);
                                usuario.setNome(nome);
                                usuario.setSexo(finalSexo);
                                usuario.setIdUsuario(idUsuario);
                                usuario.salvar();
                                finish();

                                startActivity(new Intent(getBaseContext(), HomeActivity.class));

                            }else{
                                String erroExcecao = "";

                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    erroExcecao = "Digite uma senha mais forte!";
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    erroExcecao = "Por favor, digite um e-mail válido!";
                                } catch (FirebaseAuthUserCollisionException e) {
                                    erroExcecao = "E-mail já cadastrado!";
                                } catch (Exception e) {
                                    erroExcecao = "ao cadastrar usuário: " + e.getMessage();
                                }

                                // Montagem da mensagem em caso de erro
                                Toast.makeText(CadastroActivity.this,
                                        "Erro: " + erroExcecao,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(CadastroActivity.this, "Todos os campos são obrigatórios!", Toast.LENGTH_LONG).show();
                }



            }
        });
    }

    private void inicializaComponentes() {
        etNome = findViewById(R.id.etNome);
        etIdade = findViewById(R.id.etIdade);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);

        rgSexo = findViewById(R.id.rgSexo);
        rbMasc = findViewById(R.id.rbMasc);
        rbFem = findViewById(R.id.rbFem);

        btnCadastrar = findViewById(R.id.btnCadastrar);
    }
}