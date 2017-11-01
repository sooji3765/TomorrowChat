package com.example.user.tomorrowchat.Chat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.user.tomorrowchat.R;
import com.example.user.tomorrowchat.UsersActivity;
import com.example.user.tomorrowchat.Util.SectionPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class TmwChat extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPaper;
    private SectionPagerAdapter sectionPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mUserbase;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmw_chat);


        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("CHAT");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        String current_uid = firebaseUser.getUid();
        mUserbase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserbase.child("online").setValue("true");

        //Tab
        mViewPaper =(ViewPager)findViewById(R.id.tabPager);
        sectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());

        mViewPaper.setAdapter(sectionPagerAdapter);

        mTabLayout = (TabLayout)findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPaper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.chat_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.all_user){
            Intent intent1 = new Intent(TmwChat.this, UsersActivity.class);
            startActivity(intent1);
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() !=null){
            mUserbase.child("online").setValue("true");
        }
    }
}
