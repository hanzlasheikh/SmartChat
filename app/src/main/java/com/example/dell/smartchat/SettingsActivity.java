package com.example.dell.smartchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private CircleImageView mImage;
    private TextView mName,mStatus;
    private Button mImageChangeButton,mStatusChangeButton;
    private static final int Gallery_Pick=1;
    private ProgressDialog mProgress;
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mImageStorage=FirebaseStorage.getInstance().getReference();
        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        String current_user_id=mCurrentUser.getUid();
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        mUserDatabase.keepSynced(true);
        mName=findViewById(R.id.settings_displayname);
        mStatus=findViewById(R.id.settings_status);

        mStatusChangeButton=findViewById(R.id.settings_statusbtn);
        mImageChangeButton=findViewById(R.id.settings_imagebtn);
        mImage=findViewById(R.id.settings_iamge);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name=dataSnapshot.child("name").getValue().toString();
                final String image=dataSnapshot.child("image").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();
                mName.setText(name);
                mStatus.setText(status);
               /* Glide.with(SettingsActivity.this).load(image)
                        .apply(RequestOptions.placeholderOf(R.drawable.contacts_icon).error(R.drawable.contacts_icon))
                        .into(mImage);
                mName.setText(name);
                mStatus.setText(status);*/
                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.contacts_icon).into(mImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(image).placeholder(R.drawable.contacts_icon).into(mImage);

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mStatusChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_text=mStatus.getText().toString();
                Intent StatusIntent=new Intent(SettingsActivity.this,StatusActivity.class);
                StatusIntent.putExtra("status_text",status_text);
                startActivity(StatusIntent);
            }
        });
        mImageChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/

                Intent gallery_intent=new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery_intent,"SELECT IMAGE"),Gallery_Pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Gallery_Pick && resultCode==RESULT_OK)
        {
            Uri imageUri=data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(SettingsActivity.this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgress=new ProgressDialog(this);
                mProgress.setTitle("uploading Image..");
                mProgress.setMessage("Please Wait While we Upload & Process The Image");
                mProgress.setCanceledOnTouchOutside(false);
                mProgress.show();
                Uri resultUri = result.getUri();

                final File thumb_filePath=new File(resultUri.getPath());


                String current_user_id=mCurrentUser.getUid();




                StorageReference filepath=mImageStorage.child("profile_images").child(current_user_id+".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                        {   String current_user_id=mCurrentUser.getUid();
                            final StorageReference thumb_filepath=mImageStorage.child("profile_images")
                                    .child("thumbs").child(current_user_id+".jpg");



                            StorageReference filepath=mImageStorage.child("profile_images").child(current_user_id+".jpg");
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                  final   String download_url=uri.toString();
                                    try {
                                        Bitmap thumb_bitmap = new Compressor(SettingsActivity.this)
                                                .setMaxHeight(200)
                                                .setMaxWidth(200)
                                                .setQuality(75)
                                                .compressToBitmap(thumb_filePath);

                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] thumb_byte = baos.toByteArray();
                                        UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                                        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                if (task.isSuccessful())
                                                {
                                                    String current_user_id=mCurrentUser.getUid();
                                                    StorageReference thumb_filepath2=mImageStorage.child("profile_images")
                                                            .child("thumbs").child(current_user_id+".jpg");
                                                    thumb_filepath2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String download_thumbnail_url=uri.toString();
                                                            Map  updateHashmap=new HashMap();
                                                            updateHashmap.put("image",download_url);
                                                            updateHashmap.put("thumb_image",download_thumbnail_url);
                                                            mUserDatabase.updateChildren(updateHashmap);
                                                                    Toast.makeText(SettingsActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                                            mProgress.dismiss();

                                                        }
                                                    });



                                                }
                                                else {
                                                    Toast.makeText(SettingsActivity.this, "Error Uploading thimnail", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this, "Error Uploading", Toast.LENGTH_SHORT).show();
                            mProgress.hide();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}
