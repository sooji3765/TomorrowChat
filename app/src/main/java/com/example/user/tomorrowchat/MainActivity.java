package com.example.user.tomorrowchat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.RequestManager;
import com.example.user.tomorrowchat.Board.BoardActivity;
import com.example.user.tomorrowchat.Chat.TmwChat;
import com.example.user.tomorrowchat.FindBoard.BlogSingleActivity;
import com.example.user.tomorrowchat.FindBoard.FindActivity;
import com.example.user.tomorrowchat.Map.MapsActivity;
import com.example.user.tomorrowchat.Memo.MemoActivity;

import com.example.user.tomorrowchat.Util.ViewPaperAdapter;
import com.example.user.tomorrowchat.models.Blog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private RecyclerView mList;

    final String TAG = MainActivity.class.getName();
    public RequestManager mGlideRequestManager;

    private ViewPager viewPager;
    private List<ImageView> indexes;

    private View headerView;
    private View contentView;

    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mBlogbase;
    private DatabaseReference mUserbase;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private TextView viewName;
    private CircleImageView viewImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("TRAIN TOMORROW");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null) {
            String current_uid = mFirebaseUser.getUid().toString();

            mUserbase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);


            // 동행 게시판에서 내용 불러오기
            mBlogbase = FirebaseDatabase.getInstance().getReference().child("Blog");
            mBlogbase.keepSynced(true);

            ////view pager 선언, 타임
            viewPager = (ViewPager)findViewById(R.id.viewPaperImage);
            ViewPaperAdapter viewPaperAdapter = new ViewPaperAdapter(this);
            viewPager.setAdapter(viewPaperAdapter);

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new MyTimerTask(),2000,5000);


            /// 이미지 버튼
            imageButton1 = (ImageButton)findViewById(R.id.checkbox1);
            imageButton2 = (ImageButton)findViewById(R.id.checkbox2);
            imageButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, TmwChat.class);
                    startActivity(intent);

                }
            });

            imageButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,MapsActivity.class );
                    startActivity(intent);
                    finish();
                }
            });




            //------------ recycleview

            mList = (RecyclerView) findViewById(R.id.recent_find);
            mList.setHasFixedSize(true);
            mList.setLayoutManager(new LinearLayoutManager(this));

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            headerView = navigationView.getHeaderView(0);

            viewName = (TextView) headerView.findViewById(R.id.nameText);
            viewImage = (CircleImageView) headerView.findViewById(R.id.imageUser);

            mUserbase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                    viewName.setText(name);
                    Picasso.with(MainActivity.this).load(thumb_image).into(viewImage);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout){

            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeIntent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(homeIntent);
        } else if (id == R.id.nav_find) {
            Intent findIntent = new Intent(MainActivity.this, FindActivity.class);
            startActivity(findIntent);

        } else if (id == R.id.nav_chat) {
            Intent chatIntent = new Intent(MainActivity.this, TmwChat.class);
            startActivity(chatIntent);

        } else if (id == R.id.nav_manage) {
            Intent profileIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(profileIntent);

        } else if (id == R.id.nav_memo) {
            Intent memoIntent = new Intent(MainActivity.this, MemoActivity.class);
            startActivity(memoIntent);

        } else if (id == R.id.nav_map) {
            // 최다씨 코드 캡쳐
            if (isServicesOK()) {
                Intent mapIntent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(mapIntent);
            }

        } else if (id == R.id.nav_review) {
            Intent reviewIntent = new Intent(MainActivity.this, BoardActivity.class);
            startActivity(reviewIntent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

        if (currentUser == null){
            sendToStart();

        }else {

            mUserbase.child("online").setValue(true);
        }

        FirebaseRecyclerAdapter<Blog, MainActivity.RecentViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, RecentViewHolder>(
                Blog.class,
                R.layout.recent_list,
                MainActivity.RecentViewHolder.class,
                mBlogbase

        ) {
            @Override
            protected void populateViewHolder(MainActivity.RecentViewHolder viewHolder, Blog model, int position) {
                final String post_key = getRef(position).getKey();
                viewHolder.setDisplayTitle(model.getTitle());

                viewHolder.setDisplayDesc(model.getDesc());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(MainActivity.this, BlogSingleActivity.class);
                        profileIntent.putExtra("blog_id", post_key);
                        startActivity(profileIntent);

                    }
                });
            }
        };
        mList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();

        if(currentUser !=null) {
            mUserbase.child("online").setValue(false);
            mUserbase.child("lastSeen").setValue(ServerValue.TIMESTAMP);

        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }

    public static class RecentViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public RecentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDisplayTitle(String title) {
            TextView titleView = (TextView) mView.findViewById(R.id.recent_title);
            titleView.setText(title);
        }

        public void setDisplayDesc(String desc) {
            TextView nameView = (TextView) mView.findViewById(R.id.recent_author);
            nameView.setText(desc);
        }

    }
    public class MyTimerTask extends TimerTask{

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem()==0){
                        viewPager.setCurrentItem(1);
                    }else if(viewPager.getCurrentItem()==1){
                        viewPager.setCurrentItem(2);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


}
