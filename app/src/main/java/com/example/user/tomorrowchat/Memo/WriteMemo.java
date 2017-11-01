package com.example.user.tomorrowchat.Memo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.user.tomorrowchat.R;
import com.example.user.tomorrowchat.models.Memo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class WriteMemo extends AppCompatActivity {

    private EditText editText;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_memo);
        editText = (EditText)findViewById(R.id.content_memo);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_write_memo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("메모쓰기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton save = (FloatingActionButton) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                SaveMemo();
                finish();
            }
        });

        FloatingActionButton init = (FloatingActionButton)findViewById(R.id.init);
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniMemo();
            }
        });
    }
    private void SaveMemo(){
        String text = editText.getText().toString();

        // 메모장에 입력한것이 없으면
        if(text.isEmpty()){
            return;
        }

        Memo memo = new Memo();
        memo.setTxt(text);
        memo.setCreateDate(new Date().getTime());

        firebaseDatabase.getReference("Memos/"+firebaseUser.getUid())
                .push()
                .setValue(memo)
                .addOnSuccessListener(WriteMemo.this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(editText,"메모가 저장되었습니다.",Snackbar.LENGTH_LONG).show();
                    }
                });

    }
    private void iniMemo(){
        editText.setText("");
    }


}
