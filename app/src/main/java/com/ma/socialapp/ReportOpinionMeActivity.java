package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Model.DataUsers;

import java.util.HashMap;

public class ReportOpinionMeActivity extends AppCompatActivity {

    private EditText edReport;
    private Button sendBtn;
    private ImageButton arrowBtn;
    private TextView username;
    private CoordinatorLayout coordinatorLayout;

    String fromUserId, opinionId;
    private Toolbar toolbar;

    private ProgressDialog progressDialog;

    DatabaseReference userRef, reportRef;

    private FirebaseAuth auth;
    private FirebaseUser user;

    String require, thanks, save, wait, remove, undo, userId, report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_opinion_me);

        fromUserId = getIntent().getExtras().getString("fromUserId");
        opinionId = getIntent().getExtras().getString("opinionId");

        arrowBtn = findViewById(R.id.arrow_btn_report_opinion_me);
        edReport = findViewById(R.id.ed_report_opinion_me);
        username = findViewById(R.id.username_report_opinion_me);
        sendBtn = findViewById(R.id.report_btn_opinion_me);
        coordinatorLayout = findViewById(R.id.coordinator_report_opinion_me);

        toolbar = findViewById(R.id.toolbar_report_opinion_me);

        save = getResources().getString(R.string.save_report);
        thanks = getResources().getString(R.string.thanks_report);
        remove = getResources().getString(R.string.remove_report);
        wait = getResources().getString(R.string.please_wait);
        undo = getResources().getString(R.string.undo);
        require = getResources().getString(R.string.required);
        report = getResources().getString(R.string.report_opinion);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(report);

        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userId = user.getUid();

        reportRef = FirebaseDatabase.getInstance().getReference().child("Report Opinions Me");
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    DataUsers dataUsers = dataSnapshot.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(fromUserId)){
                        username.setText(dataUsers.getFullName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportOpinionMeActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        sendBtn.setOnClickListener(v -> {

            String value = edReport.getText().toString();

            if (TextUtils.isEmpty(value)){

                edReport.setError(require);

            }

            else {

                progressDialog.setTitle(save);
                progressDialog.setMessage(wait);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String reportId = String.valueOf(System.currentTimeMillis());

                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("reportContent", value);
                hashMap.put("toUserId", fromUserId);
                hashMap.put("fromUserId", userId);
                hashMap.put("reportId", reportId);

                reportRef.child(reportId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        progressDialog.dismiss();

                        Snackbar snackbar = Snackbar.make(coordinatorLayout, thanks, Snackbar.LENGTH_SHORT)
                                .setAction(undo, v1 -> {
                                    removeReport(reportId);
                                });

                        snackbar.show();

                        edReport.setText("");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();

                        Toast.makeText(ReportOpinionMeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                    }
                });

            }

        });

    }

    private void removeReport(final String reportId) {

        reportRef.child(reportId).removeValue();

        Toast.makeText(ReportOpinionMeActivity.this, remove, Toast.LENGTH_SHORT).show();

    }
}