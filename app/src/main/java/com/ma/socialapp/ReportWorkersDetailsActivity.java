package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Model.DataUsers;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportWorkersDetailsActivity extends AppCompatActivity {

    private ImageButton arrowBtn;
    private CircleImageView imageFrom, imageTo;
    private TextView usernameTo, usernameFrom, careerTo, careerFrom, reportContentTv;
    private ImageView imageReport;

    String userIdTo, userIdFrom, reportContent, imageReportIntent, reportId;

    DatabaseReference databaseReference;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_workers_details);

        reportContent = getIntent().getExtras().getString("reportContent");
        userIdFrom = getIntent().getExtras().getString("fromUserId");
        userIdTo = getIntent().getExtras().getString("toUserId");
        reportId = getIntent().getExtras().getString("reportId");
        imageReportIntent = getIntent().getExtras().getString("imageReport");

        arrowBtn = findViewById(R.id.arrow_btn_report_worker);

        toolbar = findViewById(R.id.toolbar_report_worker_detail);

        String detail = getResources().getString(R.string.report_detail);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(detail);

        usernameFrom = findViewById(R.id.username_tv_report_worker_from);
        imageFrom = findViewById(R.id.image_report_profile_from);
        careerFrom = findViewById(R.id.career_tv_report_worker_from);
        reportContentTv = findViewById(R.id.report_content_worker);
        imageReport = findViewById(R.id.image_worker_report);
        usernameTo = findViewById(R.id.username_tv_report_worker_to);
        careerTo = findViewById(R.id.career_tv_report_worker_to);
        imageTo = findViewById(R.id.image_report_profile_to);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DataUsers dataUsers = dataSnapshot.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(userIdTo)){
                        if (dataUsers.getCareer().equals("noCareer")){
                            careerTo.setVisibility(View.INVISIBLE);
                        }
                        else {
                            careerTo.setText(dataUsers.getCareer());
                        }
                        usernameTo.setText(dataUsers.getFullName());
                        Picasso.get().load(dataUsers.getProfileImage()).into(imageTo);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportWorkersDetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DataUsers dataUsers = dataSnapshot.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(userIdFrom)){
                        if (dataUsers.getCareer().equals("noCareer")){
                            careerFrom.setVisibility(View.INVISIBLE);
                        }
                        else {
                            careerFrom.setText(dataUsers.getCareer());
                        }
                        usernameFrom.setText(dataUsers.getFullName());
                        Picasso.get().load(dataUsers.getProfileImage()).into(imageFrom);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ReportWorkersDetailsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        reportContentTv.setText(reportContent);
        Picasso.get().load(imageReportIntent).into(imageReport);

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });

    }
}