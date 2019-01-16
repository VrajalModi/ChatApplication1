package com.chatapp.firebaseproject.chatapplication;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private TextView mProfileName;
    private TextView mProfileStatus;
    private TextView mProfileFriendsCount;
    private Button mProfieRequestBtn;
    private ImageView mProfileImage;
    private Button mDeclineBtn;

    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mFriendRequestDatabaseReference;
    private DatabaseReference mFriendDatabaseReference;
    private DatabaseReference mNotificationDatabaseReference;
    private DatabaseReference mRootRef;
    private FirebaseUser mCurrentUser;

    private ProgressDialog mProgressProfile;
    private String mCurrent_State;

    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mProfileName = findViewById(R.id.profile_display_name);
        mProfileFriendsCount = findViewById(R.id.profile_total_friends);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfieRequestBtn = findViewById(R.id.profile_send_req_btn);
        mProfileImage = findViewById(R.id.profile_image);
        mDeclineBtn = findViewById(R.id.profile_decline_btn);

        mProfileFriendsCount.setVisibility(View.INVISIBLE);

        mUserDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
        mFriendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mCurrent_State = "not_friends";
        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);


        mProgressProfile = new ProgressDialog(ProfileActivity.this);
        mProgressProfile.setTitle("Loading User Data");
        mProgressProfile.setMessage("Please wait we load User Data");
        mProgressProfile.setCanceledOnTouchOutside(false);
        mProgressProfile.show();

        mUserDatabaseReference.keepSynced(true);

        mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String display_status = dataSnapshot.child("status").getValue().toString();
                String display_image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(display_status);
                Picasso.get()
                        .load(display_image)
                        .placeholder(R.drawable.om).into(mProfileImage);

//                if(mCurrentUser.getUid().equals(user_id)){
//
//                    mDeclineBtn.setEnabled(false);
//                    mDeclineBtn.setVisibility(View.INVISIBLE);
//
//                    mProfieRequestBtn.setEnabled(false);
//                    mProfieRequestBtn.setVisibility(View.INVISIBLE);
//
//                }



                //_______________________FRIENDS LIST / REQUEST FEACHER______________________________
                mFriendRequestDatabaseReference.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id))
                        {
                            String request_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (request_type.equals("Friend Request Received"))
                            {
                                mCurrent_State = "request_received";
                                mProfieRequestBtn.setText("Accept Friend Request");

                                mDeclineBtn.setVisibility(View.VISIBLE);
                                mDeclineBtn.setEnabled(true);
                            }
                            else if (request_type.equals("Friend Request Sent"))
                            {
                                mCurrent_State = "request_sent";
                                mProfieRequestBtn.setText("Cancle Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);

                            }
                            mProgressProfile.dismiss();
                        }
                        else
                        {
                            mFriendDatabaseReference.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id))
                                    {
                                        mCurrent_State = "Friends";
                                        mProfieRequestBtn.setText("UnFriend This Person");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);
                                    }
                                    mProgressProfile.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    mProgressProfile.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfieRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfieRequestBtn.setEnabled(false);

                //__________________________SEND REQUEST____________________
                if (mCurrent_State.equals("not_friends"))
                {

                    DatabaseReference newNotificationRef =  mRootRef.child("Notifications").child(user_id).push();
                    String newNotificationId = newNotificationRef.getKey();

                    HashMap<String,  String> notificationData = new HashMap<>();
                    notificationData.put("from",mCurrentUser.getUid());
                    notificationData.put("type","request");

                    Map requestMap = new HashMap();
                    requestMap.put("Friend_Request/" + mCurrentUser.getUid() + "/" + user_id + "/request_type","Friend Request Sent");
                    requestMap.put("Friend_Request/" + user_id + "/" + mCurrentUser.getUid() + "/request_type","Friend Request Received");
                    requestMap.put("Notifications/" + user_id + "/" + newNotificationId,notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError != null)
                            {
                                Toast.makeText(ProfileActivity.this, "There was an error in sending request", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                mCurrent_State = "request_sent";
                                mProfieRequestBtn.setText("Cancle Friend Request");
                            }
                            mProfieRequestBtn.setEnabled(true);
                        }
                    });
                }
                //__________________________CANCLE REQUEST____________________
                if (mCurrent_State.equals("request_sent"))
                {
                    mFriendRequestDatabaseReference.
                            child(mCurrentUser.getUid())
                            .child(user_id)
                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendRequestDatabaseReference.
                                    child(user_id)
                                    .child(mCurrentUser.getUid())
                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfieRequestBtn.setEnabled(true);
                                    mCurrent_State = "not_friends";
                                    mProfieRequestBtn.setText("Send Friend Request");


                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                //____________________REQUEST RECEIVED STATE_______________________
                if (mCurrent_State.equals("request_received")) {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    DatabaseReference newNotificationRef1 =  mRootRef.child("Notifications").child(user_id).push();
                    String newNotificationId1 = newNotificationRef1.getKey();

                    HashMap<String,  String> notificationData1 = new HashMap<>();
                    notificationData1.put("from",null);
                    notificationData1.put("type",null);

                    Map friendMap = new HashMap();
                    friendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id + "/date", currentDate);
                    friendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid() + "/date", currentDate);
                    friendMap.put("Friend_Request/" + mCurrentUser.getUid() + "/" + user_id,null);
                    friendMap.put("Friend_Request/" + user_id + "/" + mCurrentUser.getUid(),null );
                    friendMap.put("Notifications/" + user_id + "/" + newNotificationId1,notificationData1);

                    mRootRef.updateChildren(friendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {


                            if (databaseError == null) {
                                mProfieRequestBtn.setEnabled(true);
                                mCurrent_State = "Friends";
                                mProfieRequestBtn.setText("UnFriend This Person");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                //_________________________UNFRIEND PERSON_______________________________

                if (mCurrent_State.equals("Friends"))
                {
                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id,null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid(),null);


                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {


                            if (databaseError == null)
                            {
                                mCurrent_State = "not_friends";
                                mProfieRequestBtn.setText("Send Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                            else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            mProfieRequestBtn.setEnabled(true);
                        }
                    });
                }
            }
        });

        mDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDeclineBtn.setVisibility(View.VISIBLE);
                mDeclineBtn.setEnabled(true);

                if (mCurrent_State.equals("request_received")) {

                    DatabaseReference newNotificationRef2 =  mRootRef.child("Notifications").child(user_id).push();
                    String newNotificationId2 = newNotificationRef2.getKey();

                    HashMap<String,  String> notificationData2 = new HashMap<>();
                    notificationData2.put("from",null);
                    notificationData2.put("type",null);


                    Map declineFriendMap = new HashMap();
                    declineFriendMap.put("Friend_Request/" + mCurrentUser.getUid() + "/" + user_id, null);
                    declineFriendMap.put("Friend_Request/" + user_id + "/" + mCurrentUser.getUid(), null);
                    declineFriendMap.put("Notifications/" + mCurrentUser.getUid() + "/" + newNotificationId2,notificationData2);


                    mRootRef.updateChildren(declineFriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError == null) {
                                mProfieRequestBtn.setEnabled(true);
                                mDeclineBtn.setEnabled(false);
                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mCurrent_State = "not_friends";
                                mProfieRequestBtn.setText("Send Friend Request");
                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mUserRef.child("online").setValue("true");
    }
    @Override
    protected void onPause() {
        super.onPause();

        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
    }
}


//        mDeclineBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                if (mCurrent_State.equals("request_sent"))
//                {
//                    mFriendRequestDatabaseReference.
//                            child(mCurrentUser.getUid())
//                            .child(user_id)
//                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                            mFriendRequestDatabaseReference.
//                                    child(user_id)
//                                    .child(mCurrentUser.getUid())
//                                    .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//                                    mCurrent_State = "not_friends";
//
//                                    mDeclineBtn.setVisibility(View.INVISIBLE);
//                                    mDeclineBtn.setEnabled(false);
//                                }
//                            });
//                        }
//                    });
//                }
//            }
//        });
