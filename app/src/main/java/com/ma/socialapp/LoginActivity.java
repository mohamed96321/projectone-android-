package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Model.DataUsers;

public class LoginActivity extends AppCompatActivity {

    private EditText edEmail, edPassword;
    private Button btnLogin;
    private TextView createAccount, forgetPassword;
    private FirebaseAuth myAuth;
    private FirebaseUser myUser;
    private DatabaseReference myRef;
    private ProgressDialog loadingBar;

    String check3, check2;

    String login, wait, admin, emailUser, require, check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edEmail = findViewById(R.id.ed_email_login);
        edPassword = findViewById(R.id.ed_password_login);

        btnLogin = findViewById(R.id.login_btn);
        createAccount = findViewById(R.id.create_account);
        forgetPassword = findViewById(R.id.forget_password);

        myAuth = FirebaseAuth.getInstance();

        login = getResources().getString(R.string.login_account);
        admin = getResources().getString(R.string.email_admin);
        emailUser = getResources().getString(R.string.email_login);
        wait = getResources().getString(R.string.please_wait);
        require = getResources().getString(R.string.required);
        check = getResources().getString(R.string.password_or_email);

        check3 = getResources().getString(R.string.admin_message_one);
        check2 = getResources().getString(R.string.admin_message_two);

        myRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();

        loadingBar = new ProgressDialog(this);

        createAccount.setOnClickListener(v -> {
            Intent accountIntent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(accountIntent);
        });

        forgetPassword.setOnClickListener(v -> {
            Intent accountIntent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            startActivity(accountIntent);
        });


        btnLogin.setOnClickListener(v -> {

            saveData();

        });

    }

    private void saveData() {

        String email = edEmail.getText().toString();

        String password = edPassword.getText().toString();

            if (email.equals("admin@admin.com")){

                loadingBar.setTitle(login);
                loadingBar.setMessage(wait);
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);

                myAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, admin, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, AdminControlAccountsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, check, Toast.LENGTH_LONG).show();

                    }
                });
            }

            else {

                loadingBar.setTitle(login);
                loadingBar.setMessage(wait);
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);

                myAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, emailUser, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        loadingBar.dismiss();
                        Toast.makeText(LoginActivity.this, check, Toast.LENGTH_LONG).show();

                    }
                });

            }
            
        }
}