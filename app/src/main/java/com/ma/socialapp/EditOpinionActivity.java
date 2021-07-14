package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditOpinionActivity extends AppCompatActivity {

    private ImageButton arrowBtn;

    private Button saveOpinion;
    private Toolbar toolbar;

    private EditText editTextOpinion;

    private RatingBar ratingBarOpinion;

    String toUserId, contentOpinion, fromUserId;

    float ratingOpinion;

    private ProgressDialog progressDialog;

    private DatabaseReference opinionRef, allRateRef;

    String edit, wait, update, change, empty, rat;

    boolean isPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_opinion);

        toUserId = getIntent().getExtras().getString("toUserId");
        fromUserId = getIntent().getExtras().getString("fromUserId");
        contentOpinion = getIntent().getExtras().getString("opinionContent");
        ratingOpinion = getIntent().getExtras().getFloat("rating");

        toolbar = findViewById(R.id.toolbar_edit_opinion);

        arrowBtn = findViewById(R.id.arrow_btn_edit_opinion);
        saveOpinion = findViewById(R.id.edit_opinion_btn);
        editTextOpinion = findViewById(R.id.edit_content_opinion);
        ratingBarOpinion = findViewById(R.id.rating_worker_edit);

        progressDialog = new ProgressDialog(this);

        wait = getResources().getString(R.string.please_wait);
        change = getResources().getString(R.string.not_change_opinion);
        edit = getResources().getString(R.string.edit_opinion);
        update = getResources().getString(R.string.update_opinion);
        empty = getResources().getString(R.string.empty_opinion_toast);
        rat = getResources().getString(R.string.rating_must);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(edit);

        editTextOpinion.setText(contentOpinion);

        ratingBarOpinion.setRating(ratingOpinion);

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        opinionRef = FirebaseDatabase.getInstance().getReference("Opinions");

        allRateRef = FirebaseDatabase.getInstance().getReference("Total Rating");

        ratingBarOpinion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                saveData(rating);
            }
        });

    }

    private void saveData(float rating) {

        saveOpinion.setOnClickListener(v -> {

            String value = editTextOpinion.getText().toString();
            
            if (TextUtils.isEmpty(value)) {

                Toast.makeText(this, empty, Toast.LENGTH_LONG).show();
            }

            else if (rating == 0) {
                Toast.makeText(this, rat, Toast.LENGTH_LONG).show();
            }

            else if (value.equals(contentOpinion) && ratingBarOpinion.getRating() == ratingOpinion){

                Toast.makeText(this, change, Toast.LENGTH_LONG).show();

            }

            else {

                progressDialog.setTitle(edit);
                progressDialog.setMessage(wait);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("opinionContent", value);

                hashMap.put("rating", rating);

                opinionRef.child(toUserId).child(fromUserId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        onBackPressed();
                        saveRatingOfData(ratingOpinion, rating, toUserId);
                        Toast.makeText(EditOpinionActivity.this, update, Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        onBackPressed();
                        Toast.makeText(EditOpinionActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                });

            }

        });

    }

    private void saveRatingOfData(final float ratingOpinion,
                                  final float rating, final String toUserId) {


        isPressed = true;

        String str = "";

        allRateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (isPressed){
                    String rate = "" + snapshot.child(toUserId).child("ratingCount").getValue();
                    float count = (Float.parseFloat(rate) - ratingOpinion) + rating;
                    allRateRef.child(toUserId).child("ratingCount").setValue(count + str);
                    isPressed = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(EditOpinionActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}