package com.example.user.tomorrowchat.Memo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;

import com.example.user.tomorrowchat.Chat.ChatActivity;
import com.example.user.tomorrowchat.ProfileActivity;
import com.example.user.tomorrowchat.R;
import com.example.user.tomorrowchat.models.Memo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MemoActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private String selectedMemoKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_memo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("메모장");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        listView = (ListView) findViewById(R.id.memo_list);
        adapter = new ArrayAdapter<>(this, R.layout.memo_list, R.id.memo_content);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        firebaseDatabase.getReference("Memos/" + firebaseUser.getUid())
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Memo memo = dataSnapshot.getValue(Memo.class);
                        memo.setKey(dataSnapshot.getKey());
                        adapter.add(memo.getTitle());

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Memo memo = dataSnapshot.getValue(Memo.class);
                        memo.setKey(dataSnapshot.getKey());
                        adapter.remove(memo.getTitle());

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Memo memo = dataSnapshot.getValue(Memo.class);
                        memo.setKey(dataSnapshot.getKey());
                        adapter.remove(memo.getTitle());

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        // 리스트 아이템 삭제
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                CharSequence options[] = new CharSequence[]{"Delete", "SHOW"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MemoActivity.this);

                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (which == 0) { // 메모 삭제
                            firebaseDatabase.getReference("Memos/" + firebaseUser.getUid())
                                    .orderByChild("title")
                                    .equalTo((String) listView.getItemAtPosition(position))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                                                firstChild.getRef().removeValue();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                            adapter.notifyDataSetChanged();
                        }
                        if (which == 1) { // 메모 자세히
                            Memo memo = (Memo)listView.getItemAtPosition(position);
                            String key = memo.getKey();
                            Log.i("MemoActivyt=====>", key);
                            Intent modifyIntent = new Intent(MemoActivity.this, MemoModify.class);
                            modifyIntent.putExtra("user_id", firebaseUser.getUid());
                            modifyIntent.putExtra("key", key);
                            startActivity(modifyIntent);


                        }

                    }
                });
                builder.show();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.write);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MemoActivity.this, WriteMemo.class);
                startActivity(intent);
            }
        });


    }


}