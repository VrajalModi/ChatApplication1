package com.chatapp.firebaseproject.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {


    private DatabaseReference mDatabaseReference;
    private FirebaseUser currentUser;
    private CircleImageView mDisplayCircleImageView;
    private TextView mName;
    private TextView mStatus;
    private Button mStatusbtn;
    private Button mImageBtn;

    private static final int GALLERY_PICK = 1;

    private StorageReference mImageStore;
    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mDisplayCircleImageView = findViewById(R.id.settings_image);
        mName = findViewById(R.id.settings_display_name);
        mStatus = findViewById(R.id.settings_user_status);
        mStatusbtn = findViewById(R.id.settings_change_status_btn);
        mImageBtn = findViewById(R.id.settings_change_image_btn);

        mImageStore = FirebaseStorage.getInstance().getReference();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = currentUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
        mDatabaseReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

                if (!image.equals("default")) {

                    Picasso.get()
                            .load(image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.om)
                            .into(mDisplayCircleImageView, new Callback() {
                                @Override
                                public void onSuccess() {


                                }

                                @Override
                                public void onError(Exception e) {

                                    Picasso.get().load(image).placeholder(R.drawable.om).into(mDisplayCircleImageView);
                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStatusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent status_intent = new Intent(SettingActivity.this,StatusActivity.class);
                status_intent.putExtra("status_value",mStatus.getText().toString());
                startActivity(status_intent);

            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallayIntent = new Intent();
                gallayIntent.setType("image/*");
                gallayIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallayIntent,"SELECT IMAGE"),GALLERY_PICK);

//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .start(SettingActivity.this);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == GALLERY_PICK  && resultCode == RESULT_OK)
        {

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(SettingActivity.this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mProgress = new ProgressDialog(SettingActivity.this);
                mProgress.setTitle("Uploading Image..");
                mProgress.setMessage("Please wait while we upload and process the image");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();

                Uri resultUri = result.getUri();
                String current_user_id = currentUser.getUid();
                File thumb_file = new File(resultUri.getPath());

                Bitmap thumb_bitmap = null;
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(400)
                            .setMaxWidth(400)
                            .setQuality(60)
                            .compressToBitmap(thumb_file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                final byte[]  thumb_byte = byteArrayOutputStream.toByteArray();


                final StorageReference filePath = mImageStore.child("Profile_Images").child(current_user_id + ".jpg");
                final StorageReference thumb_filePath = mImageStore.child("Profile_Images").child("thumb").child(current_user_id+".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String download_url = uri.toString();

                                    UploadTask uploadTask = thumb_filePath.putBytes(thumb_byte);
                                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                               if (thumb_task.isSuccessful())
                                               {
                                                   thumb_filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                       @Override
                                                       public void onSuccess(Uri thumb_uri) {

                                                           String thumb_download_url = thumb_uri.toString();

                                                           Map update_hasMap = new HashMap<>();
                                                           update_hasMap.put("image",download_url);
                                                           update_hasMap.put("thumb_image",thumb_download_url);



                                    mDatabaseReference.updateChildren(update_hasMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                mProgress.dismiss();
                                                Toast.makeText(SettingActivity.this, "Uploading Successfull", Toast.LENGTH_SHORT).show();
                                                //                                                                   }

                                            }
                                        }
                                                           });

                                                       }
                                                   });
                                               }
                                           }
                                       });
                                  }
                                });

                            }
                            else {
                            Toast.makeText(SettingActivity.this, "Error in Uploading", Toast.LENGTH_SHORT).show();
                            mProgress.dismiss();
                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SettingActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
         //   Toast.makeText(SettingActivity.this, imageUri.toString(), Toast.LENGTH_SHORT).show();

    }
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
//    public static String random() {
//        Random generator = new Random();
//        StringBuilder randomStringBuilder = new StringBuilder();
//        int randomLength = generator.nextInt(15);
//        char tempChar;
//        for (int i = 0; i < randomLength; i++){
//            tempChar = (char) (generator.nextInt(96) + 32);
//            randomStringBuilder.append(tempChar);
//        }
//        return randomStringBuilder.toString();
//    }
}
