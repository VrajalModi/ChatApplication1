package com.chatapp.firebaseproject.chatapplication;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class FrontActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;
    private DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);

        mAuth = FirebaseAuth.getInstance();
        mViewPager = findViewById(R.id.main_tabpager);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Vrajal Modi's Chat App");

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

            if (mAuth.getCurrentUser() != null) {
                mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            }


    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null)
        {
           sendToStart();
        }
        else {
            mUserRef.child("online").setValue("true");
        }
      //  updateUI(currentUser);
    }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        FirebaseUser currentUser = mAuth.getCurrentUser();
       // currentUser.equals(null);

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
            FirebaseAuth.getInstance().signOut();

    }

    private void sendToStart() {

        Intent startIntent = new Intent(FrontActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId() == R.id.main_logout_btn)
         {
             mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
             FirebaseAuth.getInstance().signOut();
             sendToStart();
         }

         if (item.getItemId() == R.id.main_settings_btn)
         {
             Intent settingsIntent = new Intent(FrontActivity.this,SettingActivity.class);
             startActivity(settingsIntent);
         }

         if (item.getItemId() == R.id.main_all_btn)
         {
             Intent settingsIntent = new Intent(FrontActivity.this,UserActivity.class);
             startActivity(settingsIntent);
         }

         return true;
    }
}
