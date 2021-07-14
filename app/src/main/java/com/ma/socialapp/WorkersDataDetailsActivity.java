package com.ma.socialapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkersDataDetailsActivity extends AppCompatActivity {

    private CircleImageView image_profile_worker_details;
    private ImageButton arrow_btn_details_worker;
    private ImageView image_career;
    private TextView detail_name, country_worker_details, city_worker_details, address_worker_details,
            phone_worker_details, specialization_worker_details, career_worker_details, full_name__worker_details;

    String fullName, career, specification, imageProfile,
            imageCareer, city, country, address, phone, userId;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workers_data_details);

        fullName = getIntent().getExtras().getString("fullName");
        career = getIntent().getExtras().getString("career");
        specification = getIntent().getExtras().getString("specification");
        phone = getIntent().getExtras().getString("phoneNumber");
        address = getIntent().getExtras().getString("address");
        city = getIntent().getExtras().getString("city");
        country = getIntent().getExtras().getString("country");
        imageProfile = getIntent().getExtras().getString("profileImage");
        imageCareer = getIntent().getExtras().getString("careerImage");
        userId = getIntent().getExtras().getString("userId");

        toolbar = findViewById(R.id.toolbar_workers_details);

        image_career = findViewById(R.id.image_career);
        image_profile_worker_details = findViewById(R.id.image_profile_worker_details);
        arrow_btn_details_worker = findViewById(R.id.arrow_btn_details_worker);
        country_worker_details = findViewById(R.id.country_worker_details);

        city_worker_details = findViewById(R.id.city_worker_details);
        address_worker_details = findViewById(R.id.address_worker_details);
        phone_worker_details = findViewById(R.id.phone_worker_details);
        specialization_worker_details = findViewById(R.id.specialization_worker_details);
        career_worker_details = findViewById(R.id.career_worker_details);
        full_name__worker_details = findViewById(R.id.full_name__worker_details);

        String workerDetail =getResources().getString(R.string.worker_detail);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(workerDetail + " " + fullName);


        String careerStrLan =getResources().getString(R.string.career);
        String specificationStrLan = getResources().getString(R.string.specification);
        String phoneStrLan = getResources().getString(R.string.phone);
        String cityStrLan = getResources().getString(R.string.city);
        String addressStrLan = getResources().getString(R.string.address);
        String countryStrLan = getResources().getString(R.string.country);



        career_worker_details.setText(careerStrLan+" : "+career);
        full_name__worker_details.setText(fullName);
        city_worker_details.setText(cityStrLan+" : "+city);
        country_worker_details.setText(countryStrLan+" : "+country);
        address_worker_details.setText(addressStrLan+" : "+address);
        specialization_worker_details.setText(specificationStrLan+" : "+specification);
        phone_worker_details.setText(phoneStrLan+" : "+phone);
        Picasso.get().load(imageProfile).into(image_profile_worker_details);
        Picasso.get().load(imageCareer).into(image_career);

        arrow_btn_details_worker.setOnClickListener(v -> {
            onBackPressed();
        });

    }
}