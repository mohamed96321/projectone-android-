package com.ma.socialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImagePostActivity extends AppCompatActivity {

    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_post);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ImageView imageViewPost = findViewById(R.id.image_full_screen_post);
        ImageButton imageArrowBtn = findViewById(R.id.arrow_btn);

        image = getIntent().getExtras().getString("imagePost");
        Picasso.get().load(image).into(imageViewPost);

        imageArrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}