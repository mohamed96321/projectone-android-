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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ma.socialapp.Model.CommentModel;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditCommentActivity extends AppCompatActivity {

    private EditText edComment;

    private Button btnSave;
    private ImageButton arrowBtn, deleteImageBtn, cameraBtn;

    private ProgressDialog progressDialog;

    private ImageView imageEditComment;

    private static final int REQUEST_CODE = 300;

    private DatabaseReference commentRef;
    private StorageReference storageReferenceComment;

    private Toolbar toolbar;

    Uri imageUri;

    String userId, commentId, postId, commentContent, imageCommentIg;

    String edit, update, wait, change, empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_comment);

        userId = getIntent().getExtras().getString("userId");
        commentId = getIntent().getExtras().getString("commentId");
        commentContent = getIntent().getExtras().getString("commentContent");
        postId = getIntent().getExtras().getString("postId");
        imageCommentIg = getIntent().getExtras().getString("imageComment");

        edComment = findViewById(R.id.edit_comment_ed_text);
        btnSave = findViewById(R.id.edit_comment_btn);
        arrowBtn = findViewById(R.id.arrow_btn_edit_comment);

        cameraBtn = findViewById(R.id.camera_btn_edit_comment);

        deleteImageBtn = findViewById(R.id.delete_image_btn_edit_comment);

        imageEditComment = findViewById(R.id.image_view_edit_comment);

        toolbar = findViewById(R.id.toolbar_edit_comment);


        edit = getResources().getString(R.string.edit_comment);
        empty = getResources().getString(R.string.empty_comment_toast);
        change = getResources().getString(R.string.not_change_comment);
        wait = getResources().getString(R.string.please_wait);
        update = getResources().getString(R.string.update_comment);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(edit);

        if (imageCommentIg.equals("noImage")){

            deleteImageBtn.setVisibility(View.GONE);

            imageEditComment.setVisibility(View.GONE);
        }

        else {

            Picasso.get().load(imageCommentIg).into(imageEditComment);
            deleteImageBtn.setVisibility(View.VISIBLE);
        }

        deleteImageBtn = findViewById(R.id.delete_image_btn_edit_comment);

        deleteImageBtn.setVisibility(View.GONE);

        commentRef = FirebaseDatabase.getInstance().getReference("Comments");

        storageReferenceComment = FirebaseStorage.getInstance().getReference().child("Comment Images");

        progressDialog = new ProgressDialog(this);

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });

        deleteImageBtn.setOnClickListener(v -> {

            imageUri = null;

            if (imageCommentIg.equals("noImage")){

                imageEditComment.setVisibility(View.GONE);
            }

            else {

                Picasso.get().load(imageCommentIg).into(imageEditComment);

            }

            deleteImageBtn.setVisibility(View.GONE);

        });

        edComment.setText(commentContent);


        cameraBtn.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(galleryIntent, REQUEST_CODE);
        });


        btnSave.setOnClickListener(v -> {

            String value = edComment.getText().toString();

            if (value.equals(commentContent) && imageUri == null){
                Toast.makeText(EditCommentActivity.this, change, Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(value)){
                Toast.makeText(this, empty, Toast.LENGTH_SHORT).show();
            }
            else if (imageUri == null || !value.equals(commentContent)){

                progressDialog.setTitle(edit);
                progressDialog.setMessage(wait);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("commentContent", value);
                hashMap.put("imageComment", imageCommentIg);

                commentRef.child(postId).child(commentId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        onBackPressed();
                        Toast.makeText(EditCommentActivity.this, update, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        onBackPressed();
                        Toast.makeText(EditCommentActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            else {

                progressDialog.setTitle(edit);
                progressDialog.setMessage(wait);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();


                storageReferenceComment.child(postId).child(commentId).putFile(imageUri).addOnCompleteListener(task -> {

                    if (task.isSuccessful()){

                        storageReferenceComment.child(postId).child(commentId).getDownloadUrl().addOnSuccessListener(uri -> {

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("commentContent", value);
                            hashMap.put("imageComment", uri.toString());

                            commentRef.child(postId).child(commentId).updateChildren(hashMap).addOnSuccessListener(o -> {

                                progressDialog.dismiss();
                                onBackPressed();
                                Toast.makeText(EditCommentActivity.this, update, Toast.LENGTH_LONG).show();

                            }).addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                onBackPressed();
                                Toast.makeText(EditCommentActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                            });
                        });
                    }
                    else {
                        String message = task.getException().getMessage();
                        progressDialog.dismiss();
                        Toast.makeText(EditCommentActivity.this, "Error Occurred : " + message, Toast.LENGTH_LONG).show();
                    }
                });


            }


        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();

            imageEditComment.setImageURI(imageUri);

            deleteImageBtn.setVisibility(View.VISIBLE);
        }
    }

}