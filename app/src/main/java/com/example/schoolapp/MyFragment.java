package com.example.schoolapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAtuh;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mPubDatabase;

    private RecyclerView mrecyclerView;
    private LinearLayoutManager mLayoutManager;

    public MyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_my, container, false);

        mUserDatabase = Utils.getDatabase().getReference().child("users");
        mUserDatabase.keepSynced(true);
        mPubDatabase = Utils.getDatabase().getReference().child("publications");
        mPubDatabase.keepSynced(true);
        mAtuh = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mrecyclerView = (RecyclerView) v.findViewById(R.id.post_list);
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mrecyclerView.setHasFixedSize(true);
        mrecyclerView.setLayoutManager(mLayoutManager);

        return  v;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerAdapter<Post, MainActivity.PubsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Post, MainActivity.PubsViewHolder>(
                Post.class,
                R.layout.pub_template,
                MainActivity.PubsViewHolder.class,
                mPubDatabase.orderByChild("post_date")
        ) {

            @Override
            protected void populateViewHolder(final MainActivity.PubsViewHolder postViewHolder, final Post post, int i) {


                if(post.getUser_id().equals(mCurrentUser.getUid())){
                    mUserDatabase.child(post.getUser_id()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            postViewHolder.textContent(post.getText_content());
                            postViewHolder.datePost("Date du post : "+ post.getPost_date());
                            postViewHolder.pubImage(post.getPub_image());
                            postViewHolder.setImageUser(dataSnapshot.child("image").getValue().toString());
                            postViewHolder.setLogin(dataSnapshot.child("username").getValue().toString());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else {
                    postViewHolder.itemView.setVisibility(View.GONE);
                }


            }
        };

        mrecyclerView.setAdapter(firebaseRecyclerAdapter);


    }
}