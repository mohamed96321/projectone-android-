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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditPostActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 300;

    private ImageButton deleteImageBtn, arrow_btn_edit_post;
    private Button edit_post_btn;
    private ImageView add_image_edit_post, image_view_edit_post;
    private EditText edit_post_ed_text;

    private Toolbar toolbar;

    String postId, postDescription, imagePost;
    private ProgressDialog progressDialog;

    Uri imageUri;

    String edit, update, wait, change, empty;

    private DatabaseReference databaseReferencePost;
    private StorageReference storageReferenceImagePost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        postDescription = getIntent().getExtras().getString("postDescription");
        postId = getIntent().getExtras().getString("postId");
        imagePost = getIntent().getExtras().getString("imagePost");

        toolbar = findViewById(R.id.toolbar_edit_post);

        deleteImageBtn = findViewById(R.id.delete_image_btn_edit_post);
        arrow_btn_edit_post = findViewById(R.id.arrow_btn_edit_post);
        edit_post_btn = findViewById(R.id.edit_post_btn);
        add_image_edit_post = findViewById(R.id.add_image_edit_post);
        image_view_edit_post = findViewById(R.id.image_view_edit_post);
        edit_post_ed_text = findViewById(R.id.edit_post_ed_text);

        progressDialog = new ProgressDialog(this);

        edit = getResources().getString(R.string.edit_post);
        update = getResources().getString(R.string.update_post);
        wait = getResources().getString(R.string.please_wait);
        change = getResources().getString(R.string.not_change_post);
        empty = getResources().getString(R.string.empty_post_toast);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(edit);

        databaseReferencePost = FirebaseDatabase.getInstance().getReference("Posts");
        storageReferenceImagePost = FirebaseStorage.getInstance().getReference().child("Post Images");

        if (imagePost.equals("noImage")){

            deleteImageBtn.setVisibility(View.GONE);
            image_view_edit_post.setVisibility(View.GONE);
        }

        else {

            Picasso.get().load(imagePost).into(image_view_edit_post);
            deleteImageBtn.setVisibility(View.VISIBLE);
        }

        edit_post_ed_text.setText(postDescription);

        deleteImageBtn.setVisibility(View.GONE);

        arrow_btn_edit_post.setOnClickListener(v -> {
            onBackPressed();
        });

        edit_post_btn.setOnClickListener(v -> {
            editPost();
        });

        deleteImageBtn.setOnClickListener(v -> {

            imageUri = null;

            if (imagePost.equals("noImage")){

                image_view_edit_post.setVisibility(View.GONE);
            }

            else {

                Picasso.get().load(imagePost).into(image_view_edit_post);

            }

            deleteImageBtn.setVisibility(View.GONE);

        });

        add_image_edit_post.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, REQUEST_CODE);
        });

    }

    private void editPost() {

        String value = edit_post_ed_text.getText().toString();

        if (TextUtils.isEmpty(value)){
            Toast.makeText(this, empty, Toast.LENGTH_SHORT).show();
        }
        else if (imageUri == null && value.equals(postDescription)){

            Toast.makeText(this, change, Toast.LENGTH_SHORT).show();
        }


        else if (imageUri == null || !value.equals(postDescription)){


            progressDialog.setTitle(edit);
            progressDialog.setMessage(wait);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("postDescription", value);
            hashMap.put("imagePost", imagePost);

            databaseReferencePost.child(postId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    onBackPressed();
                    Toast.makeText(EditPostActivity.this, update, Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    onBackPressed();
                    Toast.makeText(EditPostActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });


        }

        else {

            progressDialog.setTitle(edit);
            progressDialog.setMessage(wait);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            storageReferenceImagePost.child(postId).putFile(imageUri).addOnCompleteListener(task -> {

                if (task.isSuccessful()){

                    storageReferenceImagePost.child(postId).getDownloadUrl().addOnSuccessListener(uri -> {

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("postDescription", value);
                        hashMap.put("imagePost", uri.toString());

                        databaseReferencePost.child(postId).updateChildren(hashMap).addOnSuccessListener(o -> {

                            progressDialog.dismiss();
                            onBackPressed();
                            Toast.makeText(EditPostActivity.this, update, Toast.LENGTH_LONG).show();

                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            onBackPressed();
                            Toast.makeText(EditPostActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        });
                    });
                }
                else {
                    String message = task.getException().getMessage();
                    progressDialog.dismiss();
                    Toast.makeText(EditPostActivity.this, "Error Occurred : " + message, Toast.LENGTH_LONG).show();
                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            image_view_edit_post.setImageURI(imageUri);

            deleteImageBtn.setVisibility(View.VISIBLE);
        }
    }

}