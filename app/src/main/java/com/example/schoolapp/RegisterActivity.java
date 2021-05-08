package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ImageView back;
    private TextInputEditText name, email, password, password2;
    private TextView login;
    private Button register;

    private DatabaseReference dataUsers;
    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dataUsers = Utils.getDatabase().getReference("users");
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        back = findViewById(R.id.register_back);
        name = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        password = findViewById(R.id.reg_password);
        password2 = findViewById(R.id.reg_password2);
        login = findViewById(R.id.reg_login_btn);
        register = findViewById(R.id.register_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, StartActivity.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    public  void registerUser(){
        String pwd = password.getText().toString().trim();
        String pwd2 = password2.getText().toString().trim();
        String login = email.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(login).matches()) {
            email.setError("Veuillez entrer une email valide svp!!");
            email.requestFocus();
            return;
        }
        if(login.isEmpty()){
            email.setError("Veuillez renseigner votre adresse mail svp!!");
            email.requestFocus();
            return;
        }
        if (pwd.isEmpty()) {
            password.setError("Veuillez renseigner votre mot de passe svp!!");
            password.requestFocus();
            return;
        }

        if(pwd.length()< 6){
            password.setError("Votre mot de passe doit avoir 6 caractères minimum!!");
            password.requestFocus();
            return;
        }

        if (!pwd.equals(pwd2)) {
            password2.setError("Vos mots de passe ne sont pas identiques");
            password2.requestFocus();
            return;
        }


        progressDialog.setTitle("Création de compte");
        progressDialog.setMessage("Veuillez patienter svp!!");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(login, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Compte Créer avec succès", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                            String id = currentUser.getUid();
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("username", name.getText().toString().trim());
                            userMap.put("status", "Salut j'utilise School App");
                            userMap.put("image", "default");
                            userMap.put("phone", "Votre numéro svp");
                            dataUsers.child(id).setValue(userMap);
                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();


                        }else {
                            Toast.makeText(RegisterActivity.this, "Nous n'avons pas pu créer votre compte, vérifier votre connexion internet et réessayez", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            password.setText("");
                        }
                    }
                });

    }
}