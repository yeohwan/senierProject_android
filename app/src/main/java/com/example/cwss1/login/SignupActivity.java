package com.example.cwss1.login;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cwss1.login.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private EditText email;
    private EditText name;
    private EditText password;
    private Button signup;
    private String splash_background;
    private CheckBox c1,c2,c3,c4,d1,d2,d3,d4,d5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }




        c1 = (CheckBox) findViewById(R.id.checkBox1);
        c2 = (CheckBox) findViewById(R.id.checkBox2);
        c3 = (CheckBox) findViewById(R.id.checkBox3);
        c4 = (CheckBox) findViewById(R.id.checkBox4);
        d1 = (CheckBox) findViewById(R.id.chheckBox1);
        d2 = (CheckBox) findViewById(R.id.chheckBox2);
        d3 = (CheckBox) findViewById(R.id.chheckBox3);
        d4 = (CheckBox) findViewById(R.id.chheckBox4);
        d5 = (CheckBox) findViewById(R.id.chheckBox5);


        email = (EditText) findViewById(R.id.signupActivity_edittext_email);
        name = (EditText) findViewById(R.id.signupActivity_edittext_name);
        password = (EditText) findViewById(R.id.signupActivity_edittext_password);
        signup = (Button) findViewById(R.id.signupActivity_button_signup);
        signup.setBackgroundColor(Color.parseColor(splash_background));


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (email.getText().toString() == null || name.getText().toString() ==null || password.getText().toString() == null){
                    return;
                }

                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final String uid = task.getResult().getUser().getUid();
                                UserModel userModel = new UserModel();
                                userModel.userName = name.getText().toString();

                                if(c1.isChecked()){
                                    userModel.physical = "태양인";
                                }else if(c2.isChecked()){
                                    userModel.physical = "태음인";
                                }else if(c3.isChecked()){
                                    userModel.physical = "소양인";
                                }else if(c4.isChecked()){
                                    userModel.physical = "소음인";
                                }

                                if(d1.isChecked()){
                                    userModel.list = "감기치료 한약";
                                }else if(d2.isChecked()){
                                    userModel.list = "소화기 한약";
                                }else if(d3.isChecked()){
                                    userModel.list = "천식치료 한약";
                                }else if(d4.isChecked()){
                                    userModel.list = "아토피 치료 한약";
                                }else if(d4.isChecked()){
                                    userModel.list = "열이나 피부 관련 질환";
                                }

                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignupActivity.this, "가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }
                        });
            }
        });
    }


}