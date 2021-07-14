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
import com.ma.socialapp.Adapter.PostProfileAdapter;
import com.ma.socialapp.Model.Posts;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSearchAdminAccountsActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private TextView tvfullName, tvcareer, tvphone, tvspecification,
            tvcity, tvaddress, tvcountry;
    private RecyclerView rcUserSearchPost;
    private ImageButton arrowBtn;
    private ImageButton IBtn;
    private ImageView coverImageProfile;

    private Toolbar toolbar;

    ArrayList<Posts> postsArrayList;

    PostProfileAdapter postProfileAdapter;

    private DatabaseReference postRef;

    String fullName, career, specification, profileImage, phoneNumber, city, address, country, userId;
    String alert, delete, cancel, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_search_admin_accounts);

        fullName = getIntent().getExtras().getString("fullName");
        career = getIntent().getExtras().getString("career");
        specification = getIntent().getExtras().getString("specification");
        phoneNumber = getIntent().getExtras().getString("phoneNumber");
        city = getIntent().getExtras().getString("city");
        address = getIntent().getExtras().getString("address");
        country = getIntent().getExtras().getString("country");
        profileImage = getIntent().getExtras().getString("profileImage");
        userId = getIntent().getExtras().getString("userId");

        toolbar = findViewById(R.id.toolbar_search_account_admin);

        coverImageProfile = findViewById(R.id.image_cover_profile_search_account);

        arrowBtn = findViewById(R.id.arrow_btn_profile_search_account);

        rcUserSearchPost = findViewById(R.id.all_user_post_admin_search);

        alert = getResources().getString(R.string.alert_user);
        delete = getResources().getString(R.string.delete_user);
        cancel = getResources().getString(R.string.cancel);
        profile = getResources().getString(R.string.profile_worker);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(profile + " " + fullName);


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
                Toast.makeText(ProfileSearchAdminAccountsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        IBtn = findViewById(R.id.alert_delete_users_btn);

        IBtn.setOnClickListener(v -> {

            String[] options = {alert, delete};

            AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSearchAdminAccountsActivity.this);

            builder.setNegativeButton(cancel, (dialog, which) -> dialog.cancel());

            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {

                    }

                    if (which == 1) {

                    }
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        circleImageView = findViewById(R.id.image_profile_search_accounts);
        tvfullName = findViewById(R.id.profile_full_name_admin_search);
        tvcareer = findViewById(R.id.profile_career_admin_search);
        tvphone = findViewById(R.id.profile_phone_admin_search);
        coverImageProfile = findViewById(R.id.image_cover_profile_other);

        profile = getResources().getString(R.string.profile_worker);


        tvspecification = findViewById(R.id.profile_specialization_admin_search);
        tvcity = findViewById(R.id.profile_city_admin_search);
        tvaddress = findViewById(R.id.profile_address_admin_search);
        tvcountry = findViewById(R.id.profile_country_admin_search);

        String careerStr = getResources().getString(R.string.career);
        String specificationStr = getResources().getString(R.string.specification);
        String phoneStr = getResources().getString(R.string.phone);
        String cityStr = getResources().getString(R.string.city);
        String addressStr = getResources().getString(R.string.address);
        String countryStr = getResources().getString(R.string.country);

        if (specification.equals("noSpecification")){
            tvcareer.setVisibility(View.GONE);
            tvspecification.setVisibility(View.GONE);
        }


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