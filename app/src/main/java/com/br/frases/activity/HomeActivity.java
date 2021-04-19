package com.br.frases.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.br.frases.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import helpers.ConfiguracaoFirebase;
import helpers.UsuarioFirebase;
import model.Usuario;

public class HomeActivity extends AppCompatActivity {

    private static final int SELECAO_GALERIA = 2000;

    private FirebaseAuth autenticacao;
    private ImageView imgPerfil;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";
    private TextView txtNome;

    private StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();

        storageReference = ConfiguracaoFirebase.getStorageReference();

        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //REFERENCIA PARA O CAMPO USUARIOS NO FIREBASE
        DatabaseReference database = ConfiguracaoFirebase.getFirebase().child("usuarios");


        //METODO DO FIREBASE PARA BUSCAR EM TEMPO REAL NO BANCO "UMA ESPECIE DE OBSERVADOR"
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot keyNode : snapshot.getChildren()){
                    Usuario usuario = keyNode.getValue(Usuario.class);
                    txtNome.setText(usuario.getNome());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        inicializaComponentes();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Menu Principal");
        setSupportActionBar(toolbar);

        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) != null){

                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
    }

    private void inicializaComponentes() {
        imgPerfil = findViewById(R.id.imgPerfil);
        txtNome = findViewById(R.id.txtNome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair:
                autenticacao.signOut();
                finish();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try{
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                //verifica se a imagem foi escolhida e já faz o upload
                if(imagem != null){
                    imgPerfil.setImageBitmap(imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //configurando storage
                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("usuarios")
                            .child(idUsuarioLogado + "jpeg");

                    //tarefa de upload
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);


                    //em caso de falha no upload
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(HomeActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                                }
                            });
                            Toast.makeText(HomeActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}