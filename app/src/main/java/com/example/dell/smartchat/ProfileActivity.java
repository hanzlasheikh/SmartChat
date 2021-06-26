package com.example.dell.smartchat;

import android.app.ProgressDialog;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private TextView mDisplayName, mStatus, mFriendsCount;
    private Button mSendRequestbtn, mDeclineRequestbtn;
    private ImageView mImage;
    private DatabaseReference mUsersDatabase;

    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgress;
    private DatabaseReference mFriendRequestDatabase;
    private DatabaseReference mNotifiacationsdatabase;
    private DatabaseReference mFriendDatabase;
    private  DatabaseReference mRootRef;
    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Id
        final String user_id = getIntent().getStringExtra("user_id");

        mRootRef=FirebaseDatabase.getInstance().getReference();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Req");
        mFriendDatabase= FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotifiacationsdatabase=FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mImage = findViewById(R.id.profile_imageView);
        mStatus = findViewById(R.id.profile_status);
        mFriendsCount = findViewById(R.id.profile_totalfriends);
        mSendRequestbtn = findViewById(R.id.profile_requestbtn);
        mDeclineRequestbtn = findViewById(R.id.profile_requestdeclinebtn);
        mDisplayName = findViewById(R.id.profile_displayname);
        mCurrent_state = "Not_Friends";
        mDeclineRequestbtn.setVisibility(View.INVISIBLE);
        mDeclineRequestbtn.setEnabled(false);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading User data..");
        mProgress.setMessage("Please wait while we load the User Data");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mDisplayName.setText(display_name);
                mStatus.setText(status);

             // Glide.with(ProfileActivity.this).load(image).apply(new RequestOptions().centerCrop().placeholder(R.drawable.contacts_picture)).into(mImage);
                Picasso.get().load(image).placeholder(R.drawable.contacts_picture).into(mImage);
                //---FriendList / Request feature---//
                mFriendRequestDatabase.child(mCurrentUser.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(user_id))
                                {
                                    String req_type=dataSnapshot.child(user_id)
                                            .child("request_type").getValue().toString();
                                    if (req_type.equals("received"))
                                    {

                                        mCurrent_state="req_received";
                                        mSendRequestbtn.setText("Accept Request");
                                        mDeclineRequestbtn.setVisibility(View.VISIBLE);
                                        mDeclineRequestbtn.setEnabled(true);
                                    }else if (req_type.equals("sent"))
                                    {
                                        mCurrent_state="req_sent";
                                        mSendRequestbtn.setText("Cancel Request");
                                        mDeclineRequestbtn.setVisibility(View.INVISIBLE);
                                        mDeclineRequestbtn.setEnabled(false);


                                    }

                                    mProgress.dismiss();
                                }else
                                {
                                    mFriendDatabase.child(mCurrentUser.getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(user_id))
                                                    {
                                                        mCurrent_state="friends";
                                                        mSendRequestbtn.setText("Unfriend User");
                                                        mDeclineRequestbtn.setVisibility(View.INVISIBLE);
                                                        mDeclineRequestbtn.setEnabled(false);



                                                    }
                                                    mProgress.dismiss();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    mProgress.dismiss();

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

        mSendRequestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSendRequestbtn.setEnabled(false);
                //----------NOT FRIENDS STATE-----

                if (mCurrent_state.equals("Not_Friends"))
                {

                    DatabaseReference newNotificationRef=mRootRef.child("notifications").child(user_id)
                            .push();
                    String newNotificationId=newNotificationRef.getKey();

                    HashMap<String,String> notificationsData=new HashMap<>();
                    notificationsData.put("from",mCurrentUser.getUid());
                    notificationsData.put("type","request");

                    Map requestMap=new HashMap();
                    requestMap.put( "Friend_Req/" + mCurrentUser.getUid()+ "/" + user_id + "/request_type","sent");
                    requestMap.put("Friend_Req/" + user_id + "/" + mCurrentUser.getUid() + "/request_type","received");
                    requestMap.put("notifications/" +user_id + "/" + newNotificationId, notificationsData );

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                          if (databaseError != null)
                          {
                              Toast.makeText(ProfileActivity.this, "Got some error in sending Request", Toast.LENGTH_SHORT).show();
                          }

                          mCurrent_state="req_sent";
                          mSendRequestbtn.setText("Cancel request");
                          mSendRequestbtn.setEnabled(true);



                        }
                    });

                }

                //------REQ SENT STATE---
                if (mCurrent_state.equals("req_sent"))
                {
                    mFriendRequestDatabase.child(mCurrentUser.getUid()).child(user_id).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendRequestDatabase.child(user_id).child(mCurrentUser.getUid())
                                            .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mSendRequestbtn.setEnabled(true);
                                            mCurrent_state="Not_Friends";
                                            mSendRequestbtn.setText("Send Request");
                                            mDeclineRequestbtn.setVisibility(View.INVISIBLE);
                                            mDeclineRequestbtn.setEnabled(false);



                                        }
                                    });
                                }
                            });
                }

                //------REQ RECEIVED STATE-------
                if (mCurrent_state.equals("req_received"))
                {
                    final String currentDate=DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap=new HashMap();
                    friendsMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id + "/date",currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid() + "/date",currentDate);

                    friendsMap.put("Friend_Req/" + mCurrentUser.getUid() + "/" + user_id,null);
                    friendsMap.put("Friend_Req/" + user_id + "/" + mCurrentUser.getUid(),null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError==null)
                            {
                                mSendRequestbtn.setEnabled(true);
                                mCurrent_state="friends";
                                mSendRequestbtn.setText("Unfriend User");

                                mDeclineRequestbtn.setVisibility(View.INVISIBLE);
                                mDeclineRequestbtn.setEnabled(false);
                            }
                            else
                            {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }

                //-----UnFRIEND STATE   ----

                if (mCurrent_state.equals("friends"))
                {
                    Map unfriendMap=new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError==null)
                            {

                                mCurrent_state="Not_Friends";
                                mSendRequestbtn.setText("Send Request");

                                mDeclineRequestbtn.setVisibility(View.INVISIBLE);
                                mDeclineRequestbtn.setEnabled(false);
                            }
                            else
                            {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                          mSendRequestbtn.setEnabled(true);

                        }
                    });



                }

            }
        });


    }



}
