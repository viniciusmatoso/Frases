package model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import helpers.ConfiguracaoFirebase;

public class Usuario {
    private String idUsuario;
    private String nome;
    private String idade;
    private String sexo;

    public Usuario() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdade() {
        return idade;
    }

    public void setIdade(String idade) {
        this.idade = idade;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void salvar(){

        DatabaseReference refFirebase = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = refFirebase.child("usuarios")
                .child(idUsuario);

        usuarioRef.setValue(this);
    }
}
