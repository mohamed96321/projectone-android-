package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=\\S+$)" +
                    ".{6,}" +
                    "$"
            );

    private EditText edEmail, edPassword, edConfirmPassword;

    private Button btnRegister;
    private FirebaseAuth myAuth;
    private ProgressDialog loadingBar;

    String register, valid, upper, wait, require, confirm, create, check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edEmail = findViewById(R.id.ed_email_rg);
        edPassword = findViewById(R.id.ed_password_rg);
        edConfirmPassword = findViewById(R.id.ed_con_password_rg);

        loadingBar = new ProgressDialog(this);

        btnRegister = findViewById(R.id.register_btn);

        register = getResources().getString(R.string.register_account_success);
        valid = getResources().getString(R.string.valid_email);
        upper = getResources().getString(R.string.valid_password);
        wait = getResources().getString(R.string.please_wait);
        confirm = getResources().getString(R.string.confirm_password_error);
        require = getResources().getString(R.string.required);
        create = getResources().getString(R.string.create_account);
        check = getResources().getString(R.string.email_exist);

        myAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> {
            CreateNewAccount();
        });

    }

    private void CreateNewAccount() {
        String email = edEmail.getText().toString();
        String password = edPassword.getText().toString();
        String confirmPassword = edConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            edEmail.setError(require);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError(valid);
        } else if (TextUtils.isEmpty(password)) {
            edPassword.setError(require);
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            edPassword.setError(upper);
        } else if (!password.equals(confirmPassword)) {
            edConfirmPassword.setError(confirm);
        } else {

            loadingBar.setTitle(create);
            loadingBar.setMessage(wait);
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
            myAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, register, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegisterActivity.this, SetupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, check, Toast.LENGTH_LONG).show();

                }
            });
        }
    }
}