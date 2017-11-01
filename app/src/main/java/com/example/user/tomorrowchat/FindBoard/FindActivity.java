package com.example.user.tomorrowchat.FindBoard;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.tomorrowchat.R;
import com.example.user.tomorrowchat.models.Blog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FindActivity extends AppCompatActivity {

    private RecyclerView mBlogList;
    private boolean mProcessLike = false;
    private FloatingActionButton fBtn;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        toolbar = (Toolbar)findViewById(R.id.find_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("동행 찾기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Blog");

        mAuth = FirebaseAuth.getInstance();
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("users");


        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabaseUsers.keepSynced(true);


        mBlogList =(RecyclerView) findViewById(R.id.bloglist);


        // post 추가 버튼
        fBtn = (FloatingActionButton)findViewById(R.id.add_post);
        fBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindActivity.this, PostFind.class);
                startActivity(intent);
            }
        });

        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Blog,FindActivity.BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, FindActivity.BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                FindActivity.BlogViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(FindActivity.BlogViewHolder viewHolder, Blog model, int position) {
                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(FindActivity.this,model.getImage());

                viewHolder.setLikeBtn(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(FindActivity.this, BlogSingleActivity.class);
                        intent.putExtra("blog_id",post_key);
                        startActivity(intent);

                    }
                });


                // 포스트를 좋다고 클릭했을때.
                viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mProcessLike = true;

                        if (mProcessLike){
                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (mProcessLike) {
                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                            mProcessLike = false;
                                        } else {
                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser()
                                                    .getUid()).setValue("RandomValue");

                                            mProcessLike = false;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });

            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;

        ImageButton mLikebtn;

        public BlogViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
            mLikebtn = (ImageButton)mView.findViewById(R.id.likeBtn);

            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);
        }

        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }
        public void setDesc(String desc){
            TextView post_desc =(TextView)mView.findViewById(R.id.post_text);
            post_desc.setText(desc);
        }
        public void setImage(Context ctx, String image){
            ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);

            Picasso.with(ctx).load(image).into(post_image);
        }

        // 좋아요 누르면 색 변경
        public void setLikeBtn(final String post_key){
            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())){
                        mLikebtn.setImageResource(R.drawable.like_yellow);

                    }else {
                        mLikebtn.setImageResource(R.drawable.ic_star_black_24dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}