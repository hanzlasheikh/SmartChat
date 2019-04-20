package com.example.dell.smartchat;


import android.app.AlertDialog;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsFragment extends Fragment {
    private RecyclerView mFriendslist;
    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    private View mMainView;


    public FriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView=inflater.inflate(R.layout.fragment_friends,container,false);
        mFriendslist=mMainView.findViewById(R.id.friends_list);
        mAuth=FirebaseAuth.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();
        mFriendsDatabase=FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase=FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mFriendslist.setHasFixedSize(true);
        mFriendslist.setLayoutManager(new LinearLayoutManager(getContext()));

            return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> friendsRecyclerviewAdapter=new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>
                ( Friends.class,
                        R.layout.users_single_layout,
                        FriendsViewHolder.class,
                        mFriendsDatabase

                ) {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position) {
                         viewHolder.setDate(model.getDate());

                         final String list_user_id=getRef(position).getKey();
                         mUsersDatabase.child(list_user_id).addValueEventListener(
                                 new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                         final String userName=dataSnapshot.child("name").getValue().toString();
                                         String userThumb=dataSnapshot.child("thumb_image").getValue().toString();

                                         if (dataSnapshot.hasChild("online")){
                                             String userOnline=dataSnapshot.child("online").getValue().toString();
                                             viewHolder.setUserOnline(userOnline);
                                         }

                                         viewHolder.setName(userName);
                                         viewHolder.setImage(userThumb,getContext());
                                         viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 CharSequence options[]=new CharSequence[]{"Open Profile","Send Message"};
                                                 AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                 builder.setTitle("Select An Option");
                                                 builder.setItems(options, new DialogInterface.OnClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog, int i) {
                                                         //Click Event for each item
                                                         if (i==0)
                                                         {
                                                             Intent profileIntent=new Intent(getContext(),ProfileActivity.class);
                                                             profileIntent.putExtra("user_id",list_user_id);
                                                             startActivity(profileIntent);
                                                         }
                                                         if (i==1)
                                                         {
                                                             Intent chatIntent=new Intent(getContext(),ChatActivity.class);
                                                             chatIntent.putExtra("user_id",list_user_id);
                                                             chatIntent.putExtra("user_name",userName);
                                                             startActivity(chatIntent);
                                                         }

                                                     }
                                                 });
                                                 builder.show();

                                             }
                                         });


                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 }
                         );
            }
        };
        mFriendslist.setAdapter(friendsRecyclerviewAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setDate(String date)
        {
            TextView userStatusView=mView.findViewById(R.id.users_single_status);
            userStatusView.setText(date);
        }
        public void setName(String name){
            TextView userNameView=mView.findViewById(R.id.users_single_name);
            userNameView.setText(name);
        }
        public void setImage(String image, Context context)
        {
            CircleImageView imageView=mView.findViewById(R.id.users_single_image);
            Picasso.get().load(image).placeholder(R.drawable.contacts_icon).into(imageView);


        }
        public void setUserOnline(String online_status){
            ImageView userOnlineView=mView.findViewById(R.id.users_single_online_icon);
            if (online_status.equals("true"))
            {
              userOnlineView.setVisibility(View.VISIBLE);
            }
            else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }

        }
    }
}
