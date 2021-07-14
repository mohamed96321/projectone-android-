package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    FirebaseUser myUser;
    FirebaseAuth myAuth;
    DatabaseReference myRef;

    String check, check2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();

        check = getResources().getString(R.string.admin_message_one);
        check2 = getResources().getString(R.string.admin_message_two);

        myRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Runnable runnable = () -> {

            if (myUser != null && Objects.equals(myUser.getEmail(), "admin@admin.com")){
                Intent intent = new Intent(SplashActivity.this, AdminControlAccountsActivity.class);
                startActivity(intent);
                finish();
            }

            else if (myUser != null){

                myRef.child(myUser.getUid()).child("career").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(SplashActivity.this, SetupActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SplashActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            else {

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(runnable, 3000);
    }
}