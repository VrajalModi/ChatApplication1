package com.chatapp.firebaseproject.chatapplication;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputEditText mStatus;
    private Button mStatusSavebtn;
    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseReference;
    private FirebaseUser currentUser;
    private DatabaseReference mUserRef;

    private ProgressDialog mStatusProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mAuth = FirebaseAuth.getInstance();
        mToolbar = findViewById(R.id.status_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        mStatus = findViewById(R.id.status_input);
        mStatusSavebtn = findViewById(R.id.status_save_button);


        String status_value = getIntent().getStringExtra("status_value");
        mStatus.setText(status_value);
       // Toast.makeText(StatusActivity.this, status_value, Toast.LENGTH_SHORT).show();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = currentUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());


        mStatusSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = mStatus.getEditableText().toString().trim();

                mStatusProgress = new ProgressDialog(StatusActivity.this);
                mStatusProgress.setTitle("Save Changes");
                mStatusProgress.setMessage("Please Wait while we save the changes!");
                mStatusProgress.setCanceledOnTouchOutside(false);
                mStatusProgress.show();


                mDatabaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            mStatusProgress.dismiss();
                        }
                        else
                        {
                            Toast.makeText(StatusActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        mUserRef.child("online").setValue("true");
//    }

    @Override
    protected void onPause() {
        super.onPause();

        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserRef.child("online").setValue("true");
    }
}
