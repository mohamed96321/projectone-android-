package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Adapter.PostProfileAdapter;
import com.ma.socialapp.Model.OpinionUsers;
import com.ma.socialapp.Model.Posts;
import com.ma.socialapp.Model.TotalRate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSearchResultActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private TextView tvfullName, tvcareer, tvphone, tvspecification,
            tvcity, tvaddress, tvcountry, tvRating;
    private RecyclerView rcUserSearchPost;
    private ImageButton arrowBtn;
    private ImageButton IBtn;
    private ImageView coverImageProfile;

    private Toolbar toolbar;

    private RatingBar ratingBar;

    ArrayList<Posts> postsArrayList;

    PostProfileAdapter postProfileAdapter;

    private DatabaseReference postRef, allUserIsRated, allRateRef;

    String fullName, career, specification, profileImage, phoneNumber, city, address, country, userId, countRating;

    String report, add, view, cancel, profile;

    float totalCount, count, countUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_search_result);

        fullName = getIntent().getExtras().getString("fullName");
        career = getIntent().getExtras().getString("career");
        specification = getIntent().getExtras().getString("specification");
        phoneNumber = getIntent().getExtras().getString("phoneNumber");
        city = getIntent().getExtras().getString("city");
        address = getIntent().getExtras().getString("address");
        country = getIntent().getExtras().getString("country");
        profileImage = getIntent().getExtras().getString("profileImage");
        userId = getIntent().getExtras().getString("userId");

        toolbar = findViewById(R.id.toolbar_profile_search);

        add = getResources().getString(R.string.add_opinion);
        report = getResources().getString(R.string.report_user);
        view = getResources().getString(R.string.view_opinion);
        cancel = getResources().getString(R.string.cancel);
        profile = getResources().getString(R.string.profile_worker);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(profile + " " + fullName);

        coverImageProfile = findViewById(R.id.image_cover_profile_other);

        arrowBtn = findViewById(R.id.arrow_btn_profile);

        rcUserSearchPost = findViewById(R.id.all_user_post_other);

        ratingBar = findViewById(R.id.rating_total_worker);

        tvRating = findViewById(R.id.tv_rating);

        ratingBar.setVisibility(View.GONE);
        tvRating.setVisibility(View.GONE);

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
                Toast.makeText(ProfileSearchResultActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ProfileSearchResultActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileSearchResultActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        rcUserSearchPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcUserSearchPost.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        postsArrayList = new ArrayList<>();

        postProfileAdapter = new PostProfileAdapter(this, postsArrayList);

        rcUserSearchPost.setAdapter(postProfileAdapter);

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
                Toast.makeText(ProfileSearchResultActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        IBtn = findViewById(R.id.settings_profile_search);

        IBtn.setOnClickListener(v -> {

            String[] options = {report, add, view};

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSearchResultActivity.this);

            builder.setNegativeButton(cancel, (dialog, which) -> dialog.cancel());

            builder.setItems(options, (dialog, which) -> {

                if (which == 0){
                    Intent intent = new Intent(ProfileSearchResultActivity.this, ReportWorkerActivity.class);
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }

                if (which == 1){
                    Intent intent = new Intent(ProfileSearchResultActivity.this, AddOpinionActivity.class);
                    intent.putExtra("fullName", fullName);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
                if (which == 2) {
                    Intent intent = new Intent(ProfileSearchResultActivity.this, ViewOpinionsActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("fullName", fullName);
                    startActivity(intent);
                }

            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        circleImageView = findViewById(R.id.image_profile_other);
        tvfullName = findViewById(R.id.profile_full_name_other);
        tvcareer = findViewById(R.id.profile_career_other);
        tvphone = findViewById(R.id.profile_phone_other);
        coverImageProfile = findViewById(R.id.image_cover_profile_other);
        tvspecification = findViewById(R.id.profile_specialization_other);
        tvcity = findViewById(R.id.profile_city_other);
        tvaddress = findViewById(R.id.profile_address_other);
        tvcountry = findViewById(R.id.profile_country_other);

        String careerStr = getResources().getString(R.string.career);
        String specificationStr = getResources().getString(R.string.specification);
        String phoneStr = getResources().getString(R.string.phone);
        String cityStr = getResources().getString(R.string.city);
        String addressStr = getResources().getString(R.string.address);
        String countryStr = getResources().getString(R.string.country);


        tvfullName.setText(fullName);
        tvcareer.setText(careerStr+" : "+career);
        tvspecification.setText(specificationStr+" : "+specification);
        tvphone.setText(phoneStr+" : "+phoneNumber);
        tvcity.setText(cityStr+" : "+city);
        tvaddress.setText(addressStr+" : "+address);
        tvcountry.setText(countryStr+" : "+country);
        Picasso.get().load(profileImage).into(circleImageView);

    }
}