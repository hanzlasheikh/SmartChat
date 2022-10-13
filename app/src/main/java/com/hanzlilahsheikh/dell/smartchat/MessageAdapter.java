package com.hanzlilahsheikh.dell.smartchat;

import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private FirebaseAuth mAuth;



    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.message_single_layout,viewGroup,false);
        return new MessageViewHolder(view);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageSenderText;
        public ImageView messageImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            messageSenderText =itemView.findViewById(R.id.message_text_sender_layout);
            messageImage=itemView.findViewById(R.id.message_image_layout);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i) {
        mAuth=FirebaseAuth.getInstance();
        String current_user_id=mAuth.getCurrentUser().getUid();

        Messages c=mMessageList.get(i);
        String from_user=c.getFrom();
        String message_type=c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if(message_type.equals("text")) {
            messageViewHolder.messageImage.setVisibility(View.INVISIBLE);

            if (from_user.equals(current_user_id))
            {messageViewHolder.messageSenderText.setBackgroundResource(R.drawable.message_text_sneder_background);
            messageViewHolder.messageSenderText.setTextColor(Color.WHITE);
            messageViewHolder.messageSenderText.setText(c.getMessage()); }
            else {

                messageViewHolder.messageSenderText.setBackgroundResource(R.drawable.message_text_receiver_background);
                messageViewHolder.messageSenderText.setTextColor(Color.BLACK);
                messageViewHolder.messageSenderText.setText(c.getMessage());

            }



        }
        else {
            messageViewHolder.messageSenderText.setVisibility(View.INVISIBLE);
           Picasso.get().load(c.getMessage()).placeholder(R.drawable.contacts_picture).into(messageViewHolder.messageImage);


        }


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }



}
