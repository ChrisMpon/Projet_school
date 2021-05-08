package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicationActivity extends AppCompatActivity {


    private androidx.appcompat.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);

        mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.universal_toolbar2);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Mes publications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




    }


    public static class PubsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public PubsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setImageUser(String image)
        {
            CircleImageView imageUser = (CircleImageView) mView.findViewById(R.id.avatar_user);
            Picasso.get().load(image).placeholder(R.drawable.avatar).into(imageUser);
        }
        public void setLogin(String login)
        {
            TextView userLogin = (TextView) mView.findViewById(R.id.user_name);
            userLogin.setText(login);
        }

        public void datePost(String date)
        {
            TextView dateP = (TextView) mView.findViewById(R.id.post_date);
            dateP.setText(date);
        }

        public void textContent(String content)
        {
            TextView txt_content = (TextView) mView.findViewById(R.id.text_content);
            txt_content.setText(content);
        }

        public void pubImage(String image)
        {
            ImageView pub = (ImageView) mView.findViewById(R.id.pub_image);

            if(image.equals("default"))
            {
                pub.setVisibility(View.GONE);
            }else {
                Picasso.get().load(image).into(pub);
            }
        }

    }

    }

