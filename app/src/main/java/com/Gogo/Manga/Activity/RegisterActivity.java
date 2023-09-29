package com.Gogo.Manga.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Gogo.Manga.R;
import com.Gogo.Manga.model.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.sdk.adsbase.StartAppAd;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private TextInputEditText edtEmail, edtPassword;
    private CheckBox checkbox;
    SweetAlertDialog pDialog  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StartAppAd.disableSplash();
        mDatabase = FirebaseDatabase.getInstance("https://manga-ko-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        edtPassword = findViewById(R.id.edtPassword);
        edtEmail = findViewById(R.id.edtEmail);
        checkbox = findViewById(R.id.checkbox);
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
    }

    UserModel userModel = new UserModel();
    public void registe(View view) {
        if (edtEmail.getText().toString().isEmpty()){
            edtEmail.setError("Email Masih Kosong");
            return;
        }
        if (edtPassword.getText().toString().isEmpty()){
            edtPassword.setError("Password Masih Kosong");
            return;
        }

        if (!checkbox.isChecked()) {
            Toast.makeText(this, "You need to agree with our agreement", Toast.LENGTH_SHORT).show();
            return;
        }
        userModel.setEmail(edtEmail.getText().toString().trim());
        userModel.setPassword(edtPassword.getText().toString().trim());

        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        checkUserAvailaible();

    }

    private void checkUserAvailaible() {
        mDatabase.child("list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    UserModel u =  snapshot1.getValue(UserModel.class);
                    if (u.getEmail().equals(edtEmail.getText().toString()))    {
                           saveAccount(snapshot1.getKey() );
                        return;
                    }
                }

                String key = mDatabase.child("list").push().getKey();
                saveAccount(key);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
    }

    private void saveAccount(String key) {
        mDatabase.child("list").child(key).setValue(userModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pDialog.dismissWithAnimation();
                        //Constant.setKey(RegisterActivity.this,key);
                        Toast.makeText(RegisterActivity.this, "Succes.. You can login now", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }

    public void toPriv(View view) {
        startActivity(new Intent(this, PrivacyActivity.class));
    }

    public void toBack(View view) {
        finish();
    }

}