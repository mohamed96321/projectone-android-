package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class AddOpinionActivity extends AppCompatActivity {

    private TextView username, userRating;

    private EditText edOpinion;

    private CoordinatorLayout coordinatorLayout;

    private Button btnSend;
    private Toolbar toolbar;

    private RatingBar ratingBarWorker;

    String fullName, userId, save, thanks, wait, remove, require, undo, rat, opinion, currentId;

    private DatabaseReference opinionRef, allRateRef, allUserIsRated;
    private FirebaseAuth auth;
    private ImageButton arrowBtn;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    boolean ismPrecessOpinion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_opinion);


        toolbar = findViewById(R.id.toolbar_add_opinion);

        opinion = getResources().getString(R.string.add_opinion);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(opinion);

        username = findViewById(R.id.username_rate);

        edOpinion = findViewById(R.id.ed_opinion);

        btnSend = findViewById(R.id.send_btn_opinion);
        userRating = findViewById(R.id.user_rating);

        save = getResources().getString(R.string.save_opinion);
        wait = getResources().getString(R.string.please_wait);
        thanks = getResources().getString(R.string.thanks_opinion);
        remove = getResources().getString(R.string.delete_opinion);
        require = getResources().getString(R.string.required);
        undo = getResources().getString(R.string.undo);
        rat = getResources().getString(R.string.rating_must);

        coordinatorLayout = findViewById(R.id.coordinator);

        ratingBarWorker = findViewById(R.id.rating_worker);

        progressDialog = new ProgressDialog(this);

        arrowBtn = findViewById(R.id.arrow_btn_opinion);

        opinionRef = FirebaseDatabase.getInstance().getReference().child("Opinions");

        allUserIsRated = FirebaseDatabase.getInstance().getReference().child("Total Users");

        userId = getIntent().getExtras().getString("userId");

        allRateRef = FirebaseDatabase.getInstance().getReference("Total Rating");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        currentId = user.getUid();

        fullName = getIntent().getExtras().getString("fullName");

        String rate = getResources().getString(R.string.rate_worker);

        String ratingName = getResources().getString(R.string.rating);

        username.setText(rate + " " + fullName);
        userRating.setText(ratingName + " " + fullName);

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });


        opinionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.child(userId).hasChild(currentId)){

                    ratingBarWorker.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                            saveDataOpinion(rating);
                        }
                    });


                }

                else {

                    String can = getResources().getString(R.string.cannot_add_opinion);

                    btnSend.setOnClickListener(v -> {

                        onBackPressed();

                        Toast.makeText(AddOpinionActivity.this, can, Toast.LENGTH_LONG).show();

                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddOpinionActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void saveDataOpinion(float rating) {

        btnSend.setOnClickListener(v -> {

            String opinion = edOpinion.getText().toString();

            if (TextUtils.isEmpty(opinion)) {
                edOpinion.setError(require);
            }

            else if (rating == 0) {
                Toast.makeText(AddOpinionActivity.this, rat, Toast.LENGTH_LONG).show();
            }

            else {

                progressDialog.setTitle(save);
                progressDialog.setMessage(wait);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("opinionContent", opinion);
                hashMap.put("toUserId", userId);
                hashMap.put("fromUserId", currentId);
                hashMap.put("rating", rating);

                opinionRef.child(userId).child(currentId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {

                    progressDialog.dismiss();


                    Snackbar snackbar = Snackbar.make(coordinatorLayout, thanks, Snackbar.LENGTH_LONG)
                            .setAction(undo, v1 -> {
                                removeOpinion(userId, currentId, rating);
                            });

                    snackbar.show();

                    edOpinion.setText("");

                    saveCountOfData(userId, rating, currentId);

                    ratingBarWorker.setRating((float) 0.5);


                }).addOnFailureListener(e -> {

                    progressDialog.dismiss();

                    Toast.makeText(AddOpinionActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                });

            }


        });

    }

    private void saveCountOfData(final String userId, final float rating, final String currentId) {


        ismPrecessOpinion = true;
        String str= "";

        allRateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (ismPrecessOpinion){
                    String rate = "" + snapshot.child(userId).child("ratingCount").getValue();
                    float count = Float.parseFloat(rate) + rating;
                    allRateRef.child(userId).child("ratingCount").setValue(count + str);
                    allUserIsRated.child(userId).child(currentId).setValue(true);
                    ismPrecessOpinion = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddOpinionActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void removeOpinion(final String userId, final String currentId, final float rating) {

        ismPrecessOpinion = true;
        String str= "";

        allRateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String rate = "" + snapshot.child(userId).child("ratingCount").getValue();

                if (rate.equals("0")){

                    Toast.makeText(AddOpinionActivity.this, remove, Toast.LENGTH_SHORT).show();
                }

                else {

                    if (ismPrecessOpinion){
                        float count = Float.parseFloat(rate) - rating;
                        allRateRef.child(userId).child("ratingCount").setValue(count + str);
                        opinionRef.child(userId).child(currentId).removeValue();
                        allUserIsRated.child(userId).child(currentId).removeValue();
                        Toast.makeText(AddOpinionActivity.this, remove, Toast.LENGTH_LONG).show();
                    }

                    ismPrecessOpinion = false;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddOpinionActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }


}