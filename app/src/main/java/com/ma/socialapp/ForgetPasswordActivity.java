package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText edEmail;
    private Button btnReset;
    private ProgressDialog loadingBar;
    private ImageButton arrowBtn;
    private FirebaseAuth myAuth;

    private Toolbar toolbar;

    String link, send, wait, require, valid, wrong, reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        edEmail = findViewById(R.id.ed_email_fg);

        loadingBar = new ProgressDialog(this);

        toolbar = findViewById(R.id.toolbar_reset_password);

        reset = getResources().getString(R.string.reset_password);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(reset);

        btnReset = findViewById(R.id.reset_password_btn);

        arrowBtn = findViewById(R.id.arrow_btn_forget_pass);

        myAuth = FirebaseAuth.getInstance();

        link = getResources().getString(R.string.send_link);
        send = getResources().getString(R.string.send_link_text);
        wait = getResources().getString(R.string.send_link);
        valid = getResources().getString(R.string.valid_email);
        require = getResources().getString(R.string.required);
        wrong = getResources().getString(R.string.email_wrong);

        btnReset.setOnClickListener(v -> {
            ResetPassword();
        });

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void ResetPassword() {
        String email = edEmail.getText().toString();

        if (TextUtils.isEmpty(email)) {
            edEmail.setError(require);
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edEmail.setError(valid);
        }
        else {

            loadingBar.setTitle(link);
            loadingBar.setMessage(wait);
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);

            myAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                loadingBar.dismiss();
                Toast.makeText(ForgetPasswordActivity.this, send, Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> {
                loadingBar.dismiss();
                Toast.makeText(ForgetPasswordActivity.this, wrong, Toast.LENGTH_LONG).show();
            });

        }
    }

}