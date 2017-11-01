package com.example.user.tomorrowchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private EditText editName;
    private EditText editEmail;
    private EditText editPassword;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;

    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        registerBtn = (Button)findViewById(R.id.registeredBtn);
        editName =(EditText)findViewById(R.id.editName);
        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim().toString();
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (!TextUtils.isEmpty(name)||!TextUtils.isEmpty(email)||!TextUtils.isEmpty(password)) {

                    progressDialog.setTitle("Registering User");
                    progressDialog.setMessage("Please wait while  we create your account");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    register_user(name, email, password);
                }

            }
        });

    }

    private void register_user(final String name, final String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            progressDialog.dismiss();

                            FirebaseUser current_user = mAuth.getCurrentUser();
                            String uid = current_user.getUid();

                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                            HashMap<String,String> userMap = new HashMap<String, String>();
                            userMap.put("name",name);
                            userMap.put("email",email);
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");


                            databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Intent mainIntent = new Intent(RegisterActivity.this,StartActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                }
                            });



                        }else {
                            progressDialog.hide();
                            Toast.makeText(RegisterActivity.this,"You got some error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}
