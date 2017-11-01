package com.example.user.tomorrowchat;

import android.app.ProgressDialog;
import android.icu.text.DateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView mDisplayID, mDisEmail,mDisCount;

    private ProgressDialog progressDialog;
    private Button mProfileSendBtn;
    private Button mDeclineBtn;
    private FirebaseUser mCurrent_user;

    private DatabaseReference mUsersDatabase;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mRootRef;

    private DatabaseReference mNotificationDatabase;
    private String current_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mDisplayID = (TextView)findViewById(R.id.profile_displayName);
        imageView = (ImageView)findViewById(R.id.profile_imageView);
        mDisEmail = (TextView)findViewById(R.id.profile_email);
        mProfileSendBtn = (Button)findViewById(R.id.request_Btn);
        mDeclineBtn = (Button)findViewById(R.id.profile_decline_btn);

        mDeclineBtn.setVisibility(View.INVISIBLE);
        mDeclineBtn.setEnabled(false);

        current_state = "not_friends";

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data");
        progressDialog.setMessage("Please wait while we load this user profile");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");


        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                String email = (String) dataSnapshot.child("email").getValue();
                String image = (String)dataSnapshot.child("image").getValue();

                mDisplayID.setText(name);
                mDisEmail.setText(email);
                Picasso.with(ProfileActivity.this).load(image).into(imageView);


                //-------------------- 친구 리스트 . 요청 요인

                mFriendReqDatabase.child(mCurrent_user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)){

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")){

                                current_state ="req_received";
                                mProfileSendBtn.setText("Accept Friends Request");


                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(true);

                            }else if(req_type.equals("sent")){

                                current_state ="req_sent";
                                mProfileSendBtn.setText("Cancel Friends Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }

                            progressDialog.dismiss();

                        }else{

                            mFriendDatabase.child(mCurrent_user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)){
                                        current_state ="friends";
                                        mProfileSendBtn.setText("Unfriend this Person");

                                        mDeclineBtn.setVisibility(View.INVISIBLE);
                                        mDeclineBtn.setEnabled(false);
                                    }

                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                    progressDialog.dismiss();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mProfileSendBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                mProfileSendBtn.setEnabled(false);


                //-------------------------- 친구가 아닌 상태
                if (current_state.equals("not_friends")){

                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String,String> notificationData = new HashMap<String, String>();
                    notificationData.put("from",mCurrent_user.getUid());
                    notificationData.put("type","request");


                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/"+mCurrent_user.getUid()+"/"+user_id+"/request_type","sent");
                    requestMap.put("Friend_req/"+user_id+"/"+mCurrent_user.getUid()+"/request_type","received");
                    requestMap.put("notifications/"+user_id+"/"+newNotificationId,notificationData);


                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError !=null){
                                Toast.makeText(ProfileActivity.this,"There was some error",Toast.LENGTH_SHORT).show();
                            }

                            mProfileSendBtn.setEnabled(true);


                            current_state ="req_sent";
                            mProfileSendBtn.setText("Cancel Friend REQUEST");
                        }
                    });

                }

                // ------------Cancel REQUEST STATE-----------

                if (current_state.equals("req_sent")){
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mProfileSendBtn.setEnabled(true);
                                    current_state ="not_friends";
                                    mProfileSendBtn.setText("SEND FRIENDS REQUEST");

                                    mDeclineBtn.setVisibility(View.INVISIBLE);
                                    mDeclineBtn.setEnabled(false);
                                }
                            });
                        }
                    });
                }

                // REQ RECEIVE STATE

                if(current_state.equals("req_received")){
                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap = new HashMap();
                    friendsMap.put("Friends/"+ mCurrent_user.getUid() +"/"+ user_id +"/date",currentDate);
                    friendsMap.put("Friends/"+ user_id +"/"+ mCurrent_user.getUid()+"/date",currentDate);

                    friendsMap.put("Friend_req/"+mCurrent_user.getUid()+"/"+user_id, null);
                    friendsMap.put("Friend_req/"+user_id+"/"+mCurrent_user.getUid(), null);


                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null){
                                mProfileSendBtn.setEnabled(true);
                                current_state ="friends";
                                mProfileSendBtn.setText("Unfriend this Person");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                            else {
                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                }


                //----------------UNFRIEND ---------------
                if (current_state.equals("friends")){

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/"+mCurrent_user.getUid()+"/"+user_id, null);
                    unfriendMap.put("Friends/"+user_id+"/"+mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError==null){

                                current_state ="not_friends";
                                mProfileSendBtn.setText("Send Friend Request");

                                mDeclineBtn.setVisibility(View.INVISIBLE);
                                mDeclineBtn.setEnabled(false);
                            }
                            else {
                                String error = databaseError.getMessage();

                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();

                            }
                            mProfileSendBtn.setEnabled(true);

                        }
                    });

                }




            }
        });



    }
}