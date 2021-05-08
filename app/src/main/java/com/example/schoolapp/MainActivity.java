package com.example.schoolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private androidx.appcompat.widget.Toolbar mToolbar;

    private CircleImageView drawerAvatar;
    private TextView drawerUsername, drawerStatus;
    private FloatingActionButton new_post;

    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAtuh;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mPubDatabase;


    private TextView myPubs, allPubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.universal_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("School App");
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        drawerAvatar = (CircleImageView) view.findViewById(R.id.drawer_avatar);
        drawerUsername = (TextView) view.findViewById(R.id.drawer_username);
        drawerStatus = (TextView) view.findViewById(R.id.drawer_satus);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.Open, R.string.Close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        new_post = (FloatingActionButton) findViewById(R.id.new_post);
        new_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });

        mUserDatabase = Utils.getDatabase().getReference().child("users");
        mUserDatabase.keepSynced(true);
        mPubDatabase = Utils.getDatabase().getReference().child("publications");
        mPubDatabase.keepSynced(true);
        mAtuh = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        myPubs = (TextView) findViewById(R.id.my_pubs);
        allPubs = (TextView) findViewById(R.id.all_pubs);

        allPubs.setBackgroundResource(R.drawable.btn);
        myPubs.setBackgroundResource(R.color.black);

        myPubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myPubs.setBackgroundResource(R.drawable.btn);
                allPubs.setBackgroundResource(R.color.black);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MyFragment myFragment = new MyFragment();
                fragmentTransaction.replace(R.id.mon_frag, myFragment);
                fragmentTransaction.commit();
            }
        });

        allPubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allPubs.setBackgroundResource(R.drawable.btn);
                myPubs.setBackgroundResource(R.color.black);

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AllFragment allFragment = new AllFragment();
                fragmentTransaction.replace(R.id.mon_frag, allFragment);
                fragmentTransaction.commit();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));break;
                    case R.id.logout:
                        mAtuh.signOut();
                        startActivity(new Intent(MainActivity.this, StartActivity.class));
                        break;
                    default:
                        return true;
                }


                return true;

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mCurrentUser == null){
            startActivity(new Intent(MainActivity.this, StartActivity.class));
        }else
        {
            allPubs.setBackgroundResource(R.drawable.btn);
            myPubs.setBackgroundResource(R.color.black);
            mUserDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Picasso.get().load(dataSnapshot.child("image").getValue().toString().trim()).placeholder(R.drawable.avatar).into(drawerAvatar);
                    drawerStatus.setText(dataSnapshot.child("status").getValue().toString().trim());
                    drawerUsername.setText(dataSnapshot.child("username").getValue().toString().trim());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            AllFragment allFragment = new AllFragment();
            fragmentTransaction.replace(R.id.mon_frag, allFragment);
            fragmentTransaction.commit();


        }

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
                /*ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader . displayImage (image, pub);*/
                Picasso.get().load(image).into(pub);
            }
        }

    }

}