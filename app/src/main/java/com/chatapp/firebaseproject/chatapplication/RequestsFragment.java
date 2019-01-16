package com.chatapp.firebaseproject.chatapplication;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mFriendList;
    private DatabaseReference mRequestDatabase;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_User_Id;
    private View myView;

    private FirebaseRecyclerOptions<Requests> firebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<Requests, ReqestsViewHolder> recyclerAdapter;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_User_Id = mAuth.getCurrentUser().getUid();
        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(mCurrent_User_Id);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mRequestDatabase.keepSynced(true);
        mUserDatabase.keepSynced(true);

        mFriendList = myView.findViewById(R.id.friend_list);
        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Requests>()
                .setQuery(mRequestDatabase, Requests.class)
                .build();

        recyclerAdapter = new FirebaseRecyclerAdapter<Requests, ReqestsViewHolder>(firebaseRecyclerOptions) {

            @Override
            protected void onBindViewHolder(final ReqestsViewHolder holder, int position, final Requests model) {


//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    Intent profileIntent = new Intent(UserActivity.this,ProfileActivity.class);
//                    profileIntent.putExtra("user_id",user_id);
//                    startActivity(profileIntent);
//
//                }
//            });
                final String list_user_id = getRef(position).getKey();
                mRequestDatabase.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String type = dataSnapshot.child("request_type").getValue().toString();
                        holder.mUserTimeView.setText(type);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(userOnline);

                        }

                       // holder.mUserTimeView.setText(model.getDate());
                        holder.mUserNameView.setText(userName);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent intent = new Intent(getContext(), ProfileActivity.class);
                                intent.putExtra("user_id", list_user_id);
                                startActivity(intent);

//                                CharSequence option[] = new CharSequence[]{"Open Profile", "Send Message"};
//
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                builder.setTitle("Select Options");
//                                builder.setItems(option, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                        if (which == 0) {
//                                            Intent intent = new Intent(getContext(), ProfileActivity.class);
//                                            intent.putExtra("user_id", list_user_id);
//                                            startActivity(intent);
//                                        }
//                                        if (which == 1) {
//                                            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
//                                            chatIntent.putExtra("user_id", list_user_id);
//                                            chatIntent.putExtra("user_name", userName);
//                                            startActivity(chatIntent);
//                                        }
//
//                                    }
//                                });
//                                builder.show();
                            }
                        });


                        Picasso.get().load(userThumb).placeholder(R.drawable.om).into(holder.mUserCircleViewImage, new Callback() {
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ReqestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_single_layout, viewGroup, false);
                ReqestsViewHolder friendsViewHolder = new ReqestsViewHolder(view);
                return friendsViewHolder;
            }
        };


        mFriendList.setAdapter(recyclerAdapter);

        return myView;
    }
    public static class ReqestsViewHolder extends RecyclerView.ViewHolder {

        public TextView mUserNameView;
        public TextView mUserTimeView;
        public CircleImageView mUserCircleViewImage;
        // public ImageView mUserOnlineView;

        public ReqestsViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserNameView = (TextView) itemView.findViewById(R.id.user_single_name);
            mUserTimeView = (TextView) itemView.findViewById(R.id.user_single_status);
            mUserCircleViewImage = (CircleImageView) itemView.findViewById(R.id.user_single_image);
            //    mUserOnlineView = (ImageView)itemView.findViewById(R.id.user_single_online_icon);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) itemView.findViewById(R.id.user_single_online_icon);

            if (online_status.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (recyclerAdapter != null) {
            recyclerAdapter.startListening();
        }
    }

    @Override
    public void onStop() {

        if (recyclerAdapter != null) {
            recyclerAdapter.stopListening();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerAdapter != null) {
            recyclerAdapter.startListening();
        }
    }
}
