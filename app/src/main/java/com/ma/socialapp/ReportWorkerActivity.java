package com.ma.socialapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ReportWorkerActivity extends AppCompatActivity {

    private TextView username;

    private EditText edReport;

    private CoordinatorLayout coordinatorLayout;

    private Button proveReportBtn, btnSend;
    private Toolbar toolbar;

    private ImageView imageReport;

    String fullName, userId;

    private DatabaseReference reportRef;
    private FirebaseAuth auth;
    private ImageButton arrowBtn;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    private static final int REQUEST_CODE = 101;

    String questionMark;

    Uri imageUri = null;

    private StorageReference storageReferenceReport;

    String save, undo, wait, thanks, remove, require, attach, report;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_worker);

        username = findViewById(R.id.username_report);
        edReport = findViewById(R.id.report_worker);
        proveReportBtn = findViewById(R.id.btn_report_prove);
        imageReport = findViewById(R.id.image_report_worker);
        btnSend = findViewById(R.id.send_btn_report_worker);
        progressDialog = new ProgressDialog(this);

        arrowBtn = findViewById(R.id.arrow_btn_report);

        toolbar = findViewById(R.id.toolbar_report_worker);
        coordinatorLayout = findViewById(R.id.coordinator_report);

        save = getResources().getString(R.string.save_report);
        thanks = getResources().getString(R.string.thanks_report);
        remove = getResources().getString(R.string.remove_report);
        wait = getResources().getString(R.string.please_wait);
        undo = getResources().getString(R.string.undo);
        require = getResources().getString(R.string.required);
        attach = getResources().getString(R.string.attach_image_report);
        report = getResources().getString(R.string.report_user);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(report);


        reportRef = FirebaseDatabase.getInstance().getReference().child("Report Workers");

        storageReferenceReport = FirebaseStorage.getInstance().getReference().child("Report Workers Images");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        fullName = getIntent().getExtras().getString("fullName");

        userId = getIntent().getExtras().getString("userId");

        String happen = getResources().getString(R.string.happen);

        questionMark = getResources().getString(R.string.question);

        username.setText(happen + " " + fullName +". "+ questionMark);

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        proveReportBtn.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, REQUEST_CODE);
        });

        btnSend.setOnClickListener(v -> {

            String report = edReport.getText().toString();

            if (TextUtils.isEmpty(report)) {
                edReport.setError(require);
            }
            else if (imageUri == null){
                Toast.makeText(this, attach, Toast.LENGTH_SHORT).show();
            }
            else {
                progressDialog.setTitle(save);
                progressDialog.setMessage(wait);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String reportId = String.valueOf(System.currentTimeMillis());

                String currentId = user.getUid();

                storageReferenceReport.child(reportId).putFile(imageUri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storageReferenceReport.child(reportId).getDownloadUrl().addOnSuccessListener(uri -> {

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("reportContent", report);
                            hashMap.put("toUserId", userId);
                            hashMap.put("reportId", reportId);
                            hashMap.put("fromUserId", currentId);
                            hashMap.put("imageReport", uri.toString());

                            reportRef.child(reportId).updateChildren(hashMap).addOnSuccessListener(o -> {

                                progressDialog.dismiss();


                                Snackbar snackbar = Snackbar.make(coordinatorLayout, thanks, Snackbar.LENGTH_SHORT)
                                        .setAction(undo, v1 -> {
                                            removeReport(reportId);
                                        });

                                snackbar.show();


                                edReport.setText("");

                                imageReport.setImageURI(null);
                                
                                imageUri = null;

                            }).addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(ReportWorkerActivity.this, e.toString(), Toast.LENGTH_LONG).show();

                            });
                        });
                    } else {
                        String message = task.getException().getMessage();
                        progressDialog.dismiss();
                        Toast.makeText(this, "Error Occurred : " + message, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

    }

    private void removeReport(final String reportId) {

        reportRef.child(reportId).removeValue();

        Toast.makeText(this, remove, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageReport.setImageURI(imageUri);
        }
    }
}