package com.chatapp.firebaseproject.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Date;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = ">>>>>>>>>>>>>>";
    private TextInputEditText mName;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private Button mCreateBtn;

    private Toolbar mToolbar;
    private ProgressDialog mRegisterProgress;

     private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

   // String name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

          mAuth = FirebaseAuth.getInstance();

        mName = (TextInputEditText) findViewById(R.id.register_name);
        mEmail = (TextInputEditText) findViewById(R.id.register_email);
        mPassword = (TextInputEditText) findViewById(R.id.register_password);
        mCreateBtn = (Button) findViewById(R.id.register_button);

        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegisterProgress = new ProgressDialog(this);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = mName.getEditableText().toString().trim();
                String email = mEmail.getEditableText().toString().trim();
                String password = mPassword.getEditableText().toString().trim();

                if(!TextUtils.isEmpty(name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    mRegisterProgress.setTitle("Registering User");
                    mRegisterProgress.setMessage("Please Wait while we create your account!");
                    mRegisterProgress.setCanceledOnTouchOutside(false);
                    mRegisterProgress.show();
                    register_user(name, email, password);
                }
            }

        });
    }

    private void register_user(final String name, final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        Toast.makeText(RegisterActivity.this, "Name: "+name +"\nEmail ID: "+email +"\nPassword: "+password, Toast.LENGTH_SHORT).show();


                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //   Log.d(TAG, "createUserWithEmail:success");
                            // FirebaseUser user = mAuth.getCurrentUser();
                            //  updateUI(user);


                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            String uId = current_user.getUid();

                            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

                            //String current_user1 = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("name",name);
                            userMap.put("emailId",email);
                            userMap.put("password",password);
                            userMap.put("date-time", String.valueOf(new Date()));
                            userMap.put("status","Hi I'm Using Vrajal's Chat App");
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");
                            userMap.put("device_token",deviceToken);


                            mDatabaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful())
                                    {
                                        mRegisterProgress.dismiss();
                                        Intent mainIntent = new Intent(RegisterActivity.this, FrontActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }
                                }
                            });



                        } else {
                            // If sign in fails, display a message to the user.
                            mRegisterProgress.hide();
                             Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Cannot Sign in.Please check the form and try again.",
                                    Toast.LENGTH_LONG).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }
}
