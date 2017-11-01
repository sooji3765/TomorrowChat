package com.example.user.tomorrowchat.Chat;

import android.graphics.Color;
import android.icu.text.DateFormat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.tomorrowchat.R;
import com.example.user.tomorrowchat.models.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by USER on 2017-10-23.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    
    public MessageAdapter(List<Messages> mMessageList){
        Log.i("TAG","==MessageAdapter 생성자 =>>"+ mMessageList);
        this.mMessageList = mMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("TAG","==onCreateViewHolder =>>"+ viewType);
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout,parent,false);
        
        return new MessageViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText;
        public CircleImageView messageProfile;
        public TextView messageName;
        public TextView messageTime;
        public ImageView messageImage;

        public MessageViewHolder(View view){
            super(view);

            messageName = (TextView)view.findViewById(R.id.name_text_layout);
            messageText = (TextView)view.findViewById(R.id.message_text_layout);
            messageTime = (TextView)view.findViewById(R.id.time_text_layout);
            messageProfile = (CircleImageView) view.findViewById(R.id.message_profile_image);
            messageImage = (ImageView)view.findViewById(R.id.message_image_layout);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, int position) {
        Log.i("TAG","==onBindViewHolder===>> "+position);

        Messages c = mMessageList.get(position);

        String from_user = c.getFrom();
        String message_type = c.getType();


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("TAG","==onDataChange===>> "+dataSnapshot.child("name").getValue().toString());
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("thumb_image").getValue().toString();

                holder.messageName.setText(name);


                Picasso.with(holder.messageProfile.getContext()).load(image)
                        .placeholder(R.drawable.default_person).into(holder.messageProfile);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (message_type.equals("text")){ // text 입력일때
            holder.messageText.setText(c.getMessage());
            holder.messageImage.setVisibility(View.GONE);
            holder.messageTime.setText(c.getTime());

        }else { // 이미지 입력
            holder.messageText.setVisibility(View.INVISIBLE);
            holder.messageImage.setVisibility(View.VISIBLE);
            holder.messageTime.setText(c.getTime());
            Picasso.with(holder.messageImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.white).into(holder.messageImage);
        }
    }

    @Override
    public long getItemId(int position) {return mMessageList.size();}
}
