package com.chatapp.firebaseproject.chatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class StartActivity extends AppCompatActivity {

    private Button mRegisterBtn;
    private Button mLoginBtn;
    private ImageView mWelcomeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegisterBtn = findViewById(R.id.register_btn);
        mLoginBtn = findViewById(R.id.login_btn);
        mWelcomeImage = findViewById(R.id.welcomeImage);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent register_intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(register_intent);
                finish();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent register_intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(register_intent);
                finish();

            }
        });
    }
}
