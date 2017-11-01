package com.example.user.tomorrowchat.Memo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.user.tomorrowchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MemoModify extends AppCompatActivity {

    private DatabaseReference mMemoDatabase;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_modify);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toobar_app_memo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("메모내용");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView = (TextView)findViewById(R.id.content_memo_text);
        String user_id = getIntent().getStringExtra("user_id");
        String key = getIntent().getStringExtra("key");

        mMemoDatabase = FirebaseDatabase.getInstance().getReference().child("Memos").child(user_id);

        mMemoDatabase.orderByChild("title")
                .equalTo(key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            String txt = dataSnapshot.getValue().toString().trim();
                            textView.setText(txt);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

    }
}
