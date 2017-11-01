package com.example.user.tomorrowchat.FindBoard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.tomorrowchat.ProfileActivity;
import com.example.user.tomorrowchat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BlogSingleActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String mPost_key = null;
    private ImageView mBlogSingleImage;
    private TextView mBlogTitle;
    private TextView mBlogDesc;
    private TextView mBlogWriter;

    private FirebaseAuth mAuth;
    private Button mSingleRemoveBtn;
    private Button mAddFriendBtn;
    private DatabaseReference mFrieds;
    @Override

    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_single);
        Toolbar toolbar = (Toolbar)findViewById(R.id.single_app_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("동행글");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");
        mFrieds = FirebaseDatabase.getInstance().getReference().child("Friends");

        mAuth = FirebaseAuth.getInstance();

        // 얻어온 키로 조사
        mPost_key = getIntent().getExtras().getString("blog_id");

        mBlogDesc = (TextView)findViewById(R.id.blog_desc);
        mBlogSingleImage = (ImageView)findViewById(R.id.blog_image);
        mBlogTitle = (TextView)findViewById(R.id.blog_title);
        mBlogWriter =(TextView)findViewById(R.id.blog_writer);
        mSingleRemoveBtn = (Button)findViewById(R.id.romoveBtn);
        mAddFriendBtn = (Button)findViewById(R.id.Add_friendBtn);

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String)dataSnapshot.child("desc").getValue();
                String post_image = (String)dataSnapshot.child("image").getValue();
                final String post_uid = (String)dataSnapshot.child("uid").getValue();
                final String post_writer =(String)dataSnapshot.child("name").getValue();


                mBlogWriter.setText(post_writer);
                mBlogTitle.setText(post_title);
                mBlogDesc.setText(post_desc);


                Picasso.with(BlogSingleActivity.this).load(post_image).placeholder(R.mipmap.add_btn).into(mBlogSingleImage);

                if (mAuth.getCurrentUser().getUid().equals(post_uid)){
                    Toast.makeText(BlogSingleActivity.this, post_title,Toast.LENGTH_SHORT).show();
                    mSingleRemoveBtn.setVisibility(View.VISIBLE);
                }


                // 유저 클릭시

                if (!mAuth.getCurrentUser().getUid().equals(post_uid)){
                    mAddFriendBtn.setVisibility(View.VISIBLE);


                    mAddFriendBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mFrieds.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Intent profileIntent = new Intent(BlogSingleActivity.this, ProfileActivity.class);
                                    profileIntent.putExtra("user_id",post_uid);
                                    startActivity(profileIntent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // POST 삭제 버튼 , 글쓴이만 삭제 가능
        mSingleRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(mPost_key).removeValue();
                //Intent mainIntent = new Intent(BlogSingleActivity.this, Find.class);
                //startActivity(mainIntent);
                finish();
            }
        });


    }
}