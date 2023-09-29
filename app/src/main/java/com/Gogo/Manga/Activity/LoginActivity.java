package com.Gogo.Manga.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Gogo.Manga.R;
import com.Gogo.Manga.Utils.Constant;
import com.Gogo.Manga.model.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.sdk.adsbase.StartAppAd;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    private String TAG ="LoginActivityTAG";

    private DatabaseReference mDatabase;
    private TextInputEditText edtEmail, edtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StartAppAd.disableSplash();
        mDatabase = FirebaseDatabase.getInstance("https://manga-ko-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);

        if (!getIntent().hasExtra("isDetail")){
            if (!Constant.IsFirtTime(this)){
                if (!getIntent().hasExtra("isHome")){
                    startActivity(new Intent(this, SplashActivity.class));
                }

            }
        }

        Constant.setNotFirstTime(this, false);
        //  printHashKey();
    }

    public void login(View view) {

        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        if (edtEmail.getText().toString().isEmpty()){
            edtEmail.setError("Email Masih Kosong");
            return;
        }
        if (edtPassword.getText().toString().isEmpty()){
            edtPassword.setError("Password Masih Kosong");
            return;
        }
        mDatabase.child("list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    UserModel u =  snapshot1.getValue(UserModel.class);
                    if (u.getEmail().equals(edtEmail.getText().toString()) &&
                            u.getPassword().equals(edtPassword.getText().toString()))    {
                        Constant.setKey(LoginActivity.this, snapshot1.getKey());
                        Constant.setName(LoginActivity.this, edtEmail.getText().toString());
                        startActivity(new Intent(LoginActivity.this, SplashActivity.class));
                        return;
                    }
                }
                Toast.makeText(LoginActivity.this, "Cant find any account, may wrong email or password", Toast.LENGTH_SHORT).show();
                pDialog.dismissWithAnimation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pDialog.dismissWithAnimation();

            }
        });

    }
    public void toSplash(View view) {
        if (getIntent().hasExtra("isDetail")){
            finish();
        }else {
            if (!getIntent().hasExtra("isHome")){
                startActivity(new Intent(this, SplashActivity.class));
            }else {
                finish();
            }
        }
    }


    public void toRegis(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }


}