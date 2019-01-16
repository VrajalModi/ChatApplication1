package com.chatapp.firebaseproject.chatapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mUsersList;
    private RecyclerView.LayoutManager layoutManager;
    private DatabaseReference mDatabaseReference;
    private FirebaseRecyclerOptions<Users> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Users, UsersViewHolder> recyclerAdapter;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserRef;
    private DatabaseReference mOrUserRef;
    private FirebaseAuth mAuth;
    String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mToolbar = findViewById(R.id.users_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersList = findViewById(R.id.user_list);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mOrUserRef = FirebaseDatabase.getInstance().getReference();
//        final String id = String.valueOf(mUserRef);



        mOrUserRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myId = dataSnapshot.child(mAuth.getCurrentUser().getUid()).getKey();
                Log.e(">>>>>","ORIGNIONAL"+myId);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(mDatabaseReference, Users.class)
                .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(UsersViewHolder holder, int position, Users model) {

                //qmAijUPWLLev52ArWyiQ0r4rzT72
                final String user_id = getRef(position).getKey();
                Log.e(">>>>>","USERID"+user_id);

                if (myId.equals(user_id))
                {
                    holder.mUserCardView.setVisibility(View.GONE);
                }
                else
                {
                    holder.mUserCardView.setVisibility(View.VISIBLE);
                    holder.mUserNameView.setText(model.getName());
                    holder.mUserSatusView.setText(model.getStatus());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent profileIntent = new Intent(UserActivity.this,ProfileActivity.class);
                            profileIntent.putExtra("user_id",user_id);
                            startActivity(profileIntent);

                        }
                    });

                    Picasso.get().load(model.getThumb_image()).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.om).into(holder.mUserCircleViewImage, new Callback() {
                        @Override
                        public void onSuccess() {

                            //   Toast.makeText(UserActivity.this, "SuccessFully", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(Exception e) {

                            //    Toast.makeText(UserActivity.this, "Error In Image load" + e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });

                }
            }

            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_single_layout, viewGroup, false);
                UsersViewHolder usersViewHolder = new UsersViewHolder(view);
                return usersViewHolder;
            }
        };


        mUsersList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(UserActivity.this, LinearLayoutManager.VERTICAL, false);
        mUsersList.setLayoutManager(layoutManager);
        mUsersList.setAdapter(recyclerAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (recyclerAdapter != null) {
            recyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {

        if (recyclerAdapter != null) {
            recyclerAdapter.stopListening();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerAdapter != null) {
            recyclerAdapter.startListening();
        }
        mUserRef.child("online").setValue("true");
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (recyclerAdapter != null) {
            recyclerAdapter.stopListening();
        }
        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder {


        public TextView mUserNameView;
        public TextView mUserSatusView;
        public CircleImageView mUserCircleViewImage;
        public CardView mUserCardView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserNameView = (TextView) itemView.findViewById(R.id.user_single_name);
            mUserSatusView = (TextView) itemView.findViewById(R.id.user_single_status);
            mUserCircleViewImage = (CircleImageView) itemView.findViewById(R.id.user_single_image);
            mUserCardView = (CardView) itemView.findViewById(R.id.card_view2);

        }

    }

}


//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
//                .setQuery(mDatabaseReference, Users.class)
//                .build();
//
//
//        FirebaseRecyclerAdapter<Users,UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options)
//        {
//            @Override
//            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
//
//                holder.mUserNameViw.setText(model.getName());
//
//            }
//
//            @NonNull
//            @Override
//            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_single_layout, viewGroup, false);
//                UsersViewHolder usersViewHolder = new UsersViewHolder(view);
//                return usersViewHolder;
//            }
//
//
//        };
//        mUsersList.setAdapter(firebaseRecyclerAdapter);
//
//        Toast.makeText(UserActivity.this, "Succssfull", Toast.LENGTH_SHORT).show();
//    }

