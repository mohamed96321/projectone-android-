package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Adapter.PostProfileAdapter;
import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.Model.OpinionUsers;
import com.ma.socialapp.Model.Posts;
import com.ma.socialapp.Model.TotalRate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePostActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private TextView tvfullName, tvcareer, tvphone, tvspecification,
            tvcity, tvaddress, tvcountry, tvRating;
    private RecyclerView rvUserProfilePost;
    private ImageButton arrowBtn;
    private ImageButton IBtn;
    private ImageView coverImageProfile;
    private Toolbar toolbar;

    private CoordinatorLayout coordinatorLayout;

    private ProgressDialog progressDialog;

    ArrayList<Posts> postsArrayList;

    String name;

    PostProfileAdapter postProfileAdapter;
    private RatingBar ratingBar;

    private DatabaseReference postRef, userRef, allUserIsRated, allRateRef;
    private FirebaseAuth auth;
    private FirebaseUser user;

    String userId, currentId;

    String save, report, wait, thanks, remove, require, why, undo, cancel, countRating;

    float totalCount, count, countUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_post);

        userId = getIntent().getExtras().getString("userId");

        coverImageProfile = findViewById(R.id.image_cover_profile_other);

        arrowBtn = findViewById(R.id.arrow_btn_post_profile);

        toolbar = findViewById(R.id.toolbar_profile_post);

        coordinatorLayout = findViewById(R.id.coordinator_profile_post);

        ratingBar = findViewById(R.id.rating_total_worker_post);

        tvRating = findViewById(R.id.tv_rating_post);

        ratingBar.setVisibility(View.GONE);
        tvRating.setVisibility(View.GONE);

        IBtn = findViewById(R.id.settings_post_profile);

        circleImageView = findViewById(R.id.image_post_profile);
        tvfullName = findViewById(R.id.full_name_post_profile);
        tvcareer = findViewById(R.id.career_post_profile);
        tvphone = findViewById(R.id.phone_post_profile);
        coverImageProfile = findViewById(R.id.image_cover_post_profile);

        tvspecification = findViewById(R.id.specialization_post_profile);
        tvcity = findViewById(R.id.city_post_profile);
        tvaddress = findViewById(R.id.address_post_profile);
        tvcountry = findViewById(R.id.country_post_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        currentId =  user.getUid();


        String careerStr = getResources().getString(R.string.career);
        String specificationStr = getResources().getString(R.string.specification);
        String phoneStr = getResources().getString(R.string.phone);
        String cityStr = getResources().getString(R.string.city);
        String addressStr = getResources().getString(R.string.address);
        String countryStr = getResources().getString(R.string.country);
        String profile = getResources().getString(R.string.profile_worker);

        save = getResources().getString(R.string.save_report);
        thanks = getResources().getString(R.string.thanks_report);
        remove = getResources().getString(R.string.remove_report);
        wait = getResources().getString(R.string.please_wait);
        report = getResources().getString(R.string.report_user);
        why = getResources().getString(R.string.why_report_user);
        require = getResources().getString(R.string.required);
        undo = getResources().getString(R.string.undo);
        cancel = getResources().getString(R.string.cancel);

        rvUserProfilePost = findViewById(R.id.all_user_post_profile);

        allUserIsRated = FirebaseDatabase.getInstance().getReference().child("Total Users");

        allRateRef = FirebaseDatabase.getInstance().getReference("Total Rating");


        allUserIsRated.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.child(userId).exists()){
                    ratingBar.setVisibility(View.GONE);
                    tvRating.setVisibility(View.GONE);
                }
                else {

                    ratingBar.setVisibility(View.VISIBLE);
                    tvRating.setVisibility(View.VISIBLE);
                }

                countUser = (float) snapshot.child(userId).getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfilePostActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        allRateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TotalRate totalRate = dataSnapshot.getValue(TotalRate.class);
                    if (totalRate.getUserId().equals(userId)){
                        if (totalRate.getRatingCount().equals("0")){
                            ratingBar.setRating(0);
                            ratingBar.setVisibility(View.GONE);
                            tvRating.setVisibility(View.GONE);
                        }
                        else {

                            ratingBar.setVisibility(View.VISIBLE);
                            tvRating.setVisibility(View.VISIBLE);

                            countRating = totalRate.getRatingCount();

                            count = Float.parseFloat(countRating);

                            try {

                                totalCount = count / countUser;

                                ratingBar.setRating(totalCount);

                            }
                            catch (Exception e) {
                                Toast.makeText(ProfilePostActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfilePostActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        progressDialog = new ProgressDialog(this);

        rvUserProfilePost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvUserProfilePost.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        postsArrayList = new ArrayList<>();

        postProfileAdapter = new PostProfileAdapter(this, postsArrayList);

        rvUserProfilePost.setAdapter(postProfileAdapter);

        postRef = FirebaseDatabase.getInstance().getReference("Posts");

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Posts posts = ds.getValue(Posts.class);
                    if (posts.getUserId().equals(userId)){
                        postsArrayList.add(posts);
                    }
                    postProfileAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfilePostActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        userRef = FirebaseDatabase.getInstance().getReference("Users");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){

                    DataUsers dataUsers = ds.getValue(DataUsers.class);

                    if (dataUsers.getUserId().equals(userId)){

                        if (dataUsers.getCareer().equals("noCareer")){
                            tvcareer.setVisibility(View.GONE);
                            tvspecification.setVisibility(View.GONE);
                            ratingBar.setVisibility(View.GONE);
                            tvRating.setVisibility(View.GONE);
                        }
                        else {
                            tvcareer.setText(careerStr + " : " + dataUsers.getCareer());
                            tvspecification.setText(specificationStr + " : " + dataUsers.getSpecification());
                        }

                        name = dataUsers.getFullName();
                        tvfullName.setText(name);
                        tvaddress.setText(addressStr + " : " + dataUsers.getAddress());
                        tvphone.setText(phoneStr + " : " + dataUsers.getPhoneNumber());
                        tvcity.setText(cityStr + " : " + dataUsers.getCity());
                        tvcountry.setText(countryStr + " : " + dataUsers.getCountry());
                        Picasso.get().load(dataUsers.getProfileImage()).into(circleImageView);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfilePostActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(profile + " " + name);


        IBtn.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfilePostActivity.this);

            builder.setTitle(report + " " + name);

            LinearLayout linearLayout = new LinearLayout(ProfilePostActivity.this);

            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            EditText editTextReport = new EditText(ProfilePostActivity.this);

            editTextReport.setHint(why);

            linearLayout.addView(editTextReport);

            builder.setView(linearLayout);

            builder.setPositiveButton(report, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String value = editTextReport.getText().toString();
                    if (TextUtils.isEmpty(value)){
                        editTextReport.setError(require);
                    }

                    else {

                        progressDialog.setTitle(save);
                        progressDialog.setMessage(wait);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        String reportId = String.valueOf(System.currentTimeMillis());

                        DatabaseReference databaseReferenceReport = FirebaseDatabase.getInstance().getReference().child("Reports Users");

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("reportId", reportId);
                        hashMap.put("FromUserId", currentId);
                        hashMap.put("ToUserId", userId);
                        hashMap.put("reportContent", value);

                        databaseReferenceReport.child(reportId)
                                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();

                                Snackbar snackbar = Snackbar.make(coordinatorLayout, thanks, Snackbar.LENGTH_LONG)
                                        .setAction(undo, v1 -> {
                                            removeOpinion(reportId);
                                        });

                                snackbar.show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ProfilePostActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });

            builder.setNegativeButton(cancel, (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });
    }

    private void removeOpinion(final String reportId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Report All Users");

        reference.child(reportId).removeValue();

        Toast.makeText(this, remove, Toast.LENGTH_SHORT).show();

    }
}