package com.example.user.tomorrowchat.Chat;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.tomorrowchat.ProfileActivity;
import com.example.user.tomorrowchat.R;
import com.example.user.tomorrowchat.models.Chat;
import com.example.user.tomorrowchat.models.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private RecyclerView mRequestList;

    private DatabaseReference mRequestDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_request, container,false);

        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        // 친구 요청을 받을 경우 - Friend_req - current_uid - 보낸 유저 - request.type. received
        mRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        mRequestDatabase.keepSynced(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);


        mRequestList = (RecyclerView) mMainView.findViewById(R.id.request_list);
        mRequestList.setHasFixedSize(true);
        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Request,RequestsViewHolder> requestRecyleViewAdapter = new FirebaseRecyclerAdapter<Request, RequestsViewHolder>(
                Request.class,
                R.layout.users_single_layout,
                RequestsViewHolder.class,
                mRequestDatabase
        ) {
            @Override
            protected void populateViewHolder(final RequestsViewHolder viewHolder, Request model, int position) {

                viewHolder.setDate("Request");

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userImage = dataSnapshot.child("thumb_image").getValue().toString();
                        //String online = dataSnapshot.child("online").getValue().toString();


                        viewHolder.setDisplayName(userName);
                        viewHolder.setDisplayImage(userImage,getContext());


                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                                profileIntent.putExtra("user_id", list_user_id);
                                startActivity(profileIntent);

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };

        mRequestList.setAdapter(requestRecyleViewAdapter);

    }

    public static class RequestsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public RequestsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDisplayName(String name){
            TextView userNameView = (TextView)mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);
        }
        public void setDate(String date) {
            TextView userDateView = (TextView)mView.findViewById(R.id.user_single_email);
            userDateView.setText(date);
        }
        public void setDisplayImage(String image, Context context){
            CircleImageView userImage = (CircleImageView)mView.findViewById(R.id.user_single_image);
            Picasso.with(context).load(image).placeholder(R.drawable.default_person).into(userImage);
        }

    }



}
