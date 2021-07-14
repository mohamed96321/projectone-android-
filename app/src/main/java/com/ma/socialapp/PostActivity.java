package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ma.socialapp.Model.DataUsers;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private Button postBtn;
    private EditText edWriteSomething;
    private ImageView imageViewPost;
    private ImageView imageBtn;
    private CircleImageView ciImageProfile;
    private TextView tvCareer, tvUsername;
    private Toolbar toolbar;

    ImageButton arrowBtn, deleteImageBtn;

    private String saveTime, saveDate;

    private ProgressDialog loadingBar;

    private static final int REQUEST_CODE = 101;

    Uri imageUri = null;

    private FirebaseAuth myAuth;
    private DatabaseReference userRef;
    private DatabaseReference postRef;
    private FirebaseUser myUser;
    private StorageReference storageReferencePostImages;

    String valid, empty, wait, save, add, post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postBtn = findViewById(R.id.post_btn);
        edWriteSomething = findViewById(R.id.post_ed_text);
        imageViewPost = findViewById(R.id.image_view);
        imageBtn = findViewById(R.id.add_image_post);
        loadingBar = new ProgressDialog(this);
        tvCareer = findViewById(R.id.career_post);
        tvUsername = findViewById(R.id.username_post);
        ciImageProfile = findViewById(R.id.image_profile_post);
        toolbar = findViewById(R.id.toolbar_posts);

        post = getResources().getString(R.string.post);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(post);

        save = getResources().getString(R.string.save_post);
        valid = getResources().getString(R.string.post_not_valid);
        empty = getResources().getString(R.string.empty_post_toast);
        wait = getResources().getString(R.string.please_wait);
        add = getResources().getString(R.string.post_is_added);

        deleteImageBtn = findViewById(R.id.delete_image_btn_post);

        arrowBtn = findViewById(R.id.arrow_btn_post);

        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        storageReferencePostImages = FirebaseStorage.getInstance().getReference().child("Post Images");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               for (DataSnapshot ds: dataSnapshot.getChildren()){
                   DataUsers dataUsers = ds.getValue(DataUsers.class);
                   if (dataUsers.getUserId().equals(myUser.getUid())) {
                       if (dataUsers.getCareer().equals("noCareer")){
                           tvCareer.setVisibility(View.GONE);
                       }
                       else {

                           tvCareer.setText(dataUsers.getCareer());
                       }
                       tvUsername.setText(dataUsers.getFullName());
                       Picasso.get().load(dataUsers.getProfileImage()).into(ciImageProfile);
                   }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        postBtn.setOnClickListener(v -> {

            SavePostDataUser();

        });

        deleteImageBtn.setVisibility(View.GONE);

        deleteImageBtn.setOnClickListener(v -> {
            imageViewPost.setImageURI(null);
            imageUri = null;
            deleteImageBtn.setVisibility(View.GONE);
        });

        imageBtn.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(galleryIntent, REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageViewPost.setImageURI(imageUri);
            deleteImageBtn.setVisibility(View.VISIBLE);
        }
    }


    private void SavePostDataUser() {

        String textPost = edWriteSomething.getText().toString();

        if (TextUtils.isEmpty(textPost)){
            Toast.makeText(this, empty, Toast.LENGTH_SHORT).show();
        }
        else if (textPost.length() <= 10 ){
            Toast.makeText(this, valid, Toast.LENGTH_SHORT).show();
        }
        else if (imageUri == null){

            loadingBar.setTitle(save);
            loadingBar.setMessage(wait);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Calendar cDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
            saveDate = currentDate.format(cDate.getTime());

            Calendar cTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveTime = currentTime.format(cTime.getTime());

            String strDate = String.valueOf(System.currentTimeMillis());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("datePost", saveDate);
            hashMap.put("timePost", saveTime);
            hashMap.put("postDescription", textPost);
            hashMap.put("imagePost", "noImage");
            hashMap.put("postId", strDate);
            hashMap.put("userId", myUser.getUid());
            hashMap.put("commentsNum", "0");

            postRef.child(strDate).updateChildren(hashMap).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()){
                    loadingBar.dismiss();
                    edWriteSomething.setText("");
                    imageViewPost.setImageURI(null);
                    imageUri = null;
                    Intent intent = new Intent(PostActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(PostActivity.this, add, Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.dismiss();
                    String message = task1.getException().getMessage();
                    Toast.makeText(PostActivity.this,
                            "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        else {

            loadingBar.setTitle(save);
            loadingBar.setMessage(wait);
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Calendar cDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
            saveDate = currentDate.format(cDate.getTime());

            Calendar cTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveTime = currentTime.format(cTime.getTime());

            String strDate = String.valueOf(System.currentTimeMillis());

            storageReferencePostImages.child(strDate).putFile(imageUri).addOnCompleteListener(task -> {

                if (task.isSuccessful()){

                    storageReferencePostImages.child(strDate).getDownloadUrl().addOnSuccessListener(uri -> {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("datePost", saveDate);
                        hashMap.put("timePost", saveTime);
                        hashMap.put("postDescription", textPost);
                        hashMap.put("imagePost", uri.toString());
                        hashMap.put("postId", strDate);
                        hashMap.put("userId", myUser.getUid());
                        hashMap.put("commentsNum", "0");

                        postRef.child(strDate).updateChildren(hashMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()){
                                loadingBar.dismiss();
                                Intent intent = new Intent(PostActivity.this, MainActivity.class);
                                startActivity(intent);
                                edWriteSomething.setText("");
                                imageViewPost.setImageURI(null);
                                imageUri = null;
                                Toast.makeText(PostActivity.this, add, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                loadingBar.dismiss();
                                String message = task1.getException().getMessage();
                                Toast.makeText(PostActivity.this,
                                        "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }
                else {
                    loadingBar.dismiss();
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}