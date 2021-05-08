package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private CircleImageView avatar, editAvatar;
    private TextInputEditText name, email, phone, editUsername;
    private TextView username, status;
    private ImageView back;
    private ImageButton edit;
    private Button updateProfile, logoutProfile;

    private FirebaseUser mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        back = (ImageView) findViewById(R.id.profile_go_back);
        status = (TextView) findViewById(R.id.profile_satus);
        avatar = (CircleImageView) findViewById(R.id.profile_avatar);
        username = (TextView) findViewById(R.id.profile_username);
        name = (TextInputEditText) findViewById(R.id.profile_name);
        email = (TextInputEditText) findViewById(R.id.profile_email);
        phone = (TextInputEditText) findViewById(R.id.profile_phone);
        updateProfile = (Button) findViewById(R.id.profile_update);
        logoutProfile = (Button) findViewById(R.id.profile_log_out);
        editAvatar = (CircleImageView) findViewById(R.id.profile_avatar);

        mAuth = FirebaseAuth.getInstance().getCurrentUser();

        logoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this, StartActivity.class));
                finish();
            }
        });
        mUserDatabase = Utils.getDatabase().getReference().child("users").child(mAuth.getUid());
        mUserDatabase.keepSynced(true);

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChange();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                username.setText(dataSnapshot.child("username").getValue().toString().trim());
                status.setText(dataSnapshot.child("status").getValue().toString().trim());
                name.setText(dataSnapshot.child("username").getValue().toString().trim());
                email.setText(mAuth.getEmail().toString());
                phone.setText(dataSnapshot.child("phone").getValue().toString().trim());
                Picasso.get().load(dataSnapshot.child("image").getValue().toString().trim()).placeholder(R.drawable.avatar).into(avatar);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveChange(){

        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("username", name.getText().toString().trim());
        userMap.put("status", "Salut j'utilise School App");
        userMap.put("image", "default");
        userMap.put("phone", phone.getText().toString().trim());
        mUserDatabase.setValue(userMap);
        Toast.makeText(ProfileActivity.this, "Vos informations ont été mises à jour",Toast.LENGTH_SHORT).show();
    }
}