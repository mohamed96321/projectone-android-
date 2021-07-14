package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteOpinionActivity extends AppCompatActivity {

    private TextView opinionTv;
    private RatingBar ratingBarOpinion;
    private Button deleteBtn;
    private ImageButton arrowBtn;
    private Toolbar toolbar;

    String toUserId, opinionContent, fromUserId;
    float ratingOpinion;

    DatabaseReference opinionRef, allRateRef, allUserIsRated;

    String delete, wait, remove, sure, delete2, cancel;

    boolean ismPrecessOpinion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_opinion);

        toUserId = getIntent().getExtras().getString("toUserId");
        fromUserId = getIntent().getExtras().getString("fromUserId");
        opinionContent = getIntent().getExtras().getString("opinionContent");
        ratingOpinion = getIntent().getExtras().getFloat("rating");

        toolbar = findViewById(R.id.toolbar_delete_opinion);

        arrowBtn = findViewById(R.id.arrow_btn_delete_opinion);
        opinionTv = findViewById(R.id.tv_opinion_me_delete);
        deleteBtn = findViewById(R.id.delete_btn);
        ratingBarOpinion = findViewById(R.id.rating_opinion_view_me_delete);

        delete = getResources().getString(R.string.delete_opinion_name);
        wait = getResources().getString(R.string.please_wait);
        remove = getResources().getString(R.string.delete_opinion);
        sure = getResources().getString(R.string.sure_delete_opinion);
        delete2 = getResources().getString(R.string.delete);
        cancel = getResources().getString(R.string.cancel);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(delete);

        ratingBarOpinion.setRating(ratingOpinion);
        opinionTv.setText(opinionContent);

        opinionRef = FirebaseDatabase.getInstance().getReference("Opinions");
        allUserIsRated = FirebaseDatabase.getInstance().getReference().child("Total Users");
        allRateRef = FirebaseDatabase.getInstance().getReference("Total Rating");

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });


        deleteBtn.setOnClickListener(v -> {


            ismPrecessOpinion = true;

            String str= "";

            allRateRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String rate = "" + snapshot.child(toUserId).child("ratingCount").getValue();

                    if (rate.equals("0")){

                        Toast.makeText(DeleteOpinionActivity.this, remove, Toast.LENGTH_SHORT).show();
                    }

                    else {

                        if (ismPrecessOpinion){
                            float count = Float.parseFloat(rate) - ratingOpinion;
                            allRateRef.child(toUserId).child("ratingCount").setValue(count + str);
                            allUserIsRated.child(toUserId).child(fromUserId).removeValue();
                            opinionRef.child(toUserId).child(fromUserId).removeValue();
                            onBackPressed();
                            Toast.makeText(DeleteOpinionActivity.this, remove, Toast.LENGTH_SHORT).show();

                        }

                        ismPrecessOpinion = false;

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(DeleteOpinionActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });


        });

    }
}