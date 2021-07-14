package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ma.socialapp.Adapter.CommentsAdapters;
import com.ma.socialapp.Model.CommentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {

    private EditText edComment;
    private ImageButton commentBtn, cameraBtn, arrowBtn, deleteImageBtn;
    private RecyclerView rcAllComments;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private ImageView imageCamera, imageComment, imageAdd;

    String saveTime, saveDate, postId;

    private static final int REQUEST_CODE = 101;

    private FirebaseAuth myAuth;
    private FirebaseUser user;

    ArrayList<CommentModel> commentUserArrayList;

    CommentsAdapters commentAdapter;

    String save, add, wait, write, comment;

    Uri imageUri;

    private DatabaseReference databaseReference, ref, refComment;

    private StorageReference storageReferenceComment;

    boolean ismPrecessComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postId = getIntent().getExtras().getString("postId");

        toolbar = findViewById(R.id.toolbar_comments);

        comment = getResources().getString(R.string.comment_of_post);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(comment);

        edComment = findViewById(R.id.ed_comment);
        commentBtn = findViewById(R.id.add_comment);
        rcAllComments = findViewById(R.id.all_post_comments);

        arrowBtn = findViewById(R.id.arrow_btn_comment);

        cameraBtn = findViewById(R.id.camera_btn);
        imageAdd = findViewById(R.id.image_plus_icon);
        imageComment = findViewById(R.id.image_comments_icon);

        deleteImageBtn = findViewById(R.id.delete_image_btn_comment);

        deleteImageBtn.setVisibility(View.GONE);

        imageAdd.setVisibility(View.GONE);
        imageComment.setVisibility(View.GONE);

        imageCamera = findViewById(R.id.image_camera);

        imageCamera.setVisibility(View.GONE);

        save = getResources().getString(R.string.save_comment);
        write = getResources().getString(R.string.write_something_please);
        wait = getResources().getString(R.string.please_wait);
        add = getResources().getString(R.string.comment_is_added);

        progressDialog = new ProgressDialog(this);

        rcAllComments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcAllComments.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        commentUserArrayList = new ArrayList<>();

        commentAdapter = new CommentsAdapters(this, commentUserArrayList);
        rcAllComments.setAdapter(commentAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        storageReferenceComment = FirebaseStorage.getInstance().getReference().child("Comment Images");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentUserArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    CommentModel commentModel = ds.getValue(CommentModel.class);
                    commentUserArrayList.add(commentModel);
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });


        deleteImageBtn.setOnClickListener(v -> {
            imageCamera.setImageURI(null);
            imageUri = null;
            deleteImageBtn.setVisibility(View.GONE);
            imageCamera.setVisibility(View.GONE);
        });

        cameraBtn.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(galleryIntent, REQUEST_CODE);
        });

        refComment = FirebaseDatabase.getInstance().getReference("Comments");

        refComment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()){
                    imageAdd.setVisibility(View.GONE);
                    imageComment.setVisibility(View.GONE);
                }

                else {
                    imageAdd.setVisibility(View.VISIBLE);
                    imageComment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        myAuth = FirebaseAuth.getInstance();
        user = myAuth.getCurrentUser();


        commentBtn.setOnClickListener(v -> {

            String comment = edComment.getText().toString();

            if (TextUtils.isEmpty(comment)){
                Toast.makeText(this, write, Toast.LENGTH_SHORT).show();
            }

            else if (imageUri == null){

                progressDialog.setTitle(save);
                progressDialog.setMessage(wait);

                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);


                Calendar cDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                saveDate = currentDate.format(cDate.getTime());

                Calendar cTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
                saveTime = currentTime.format(cTime.getTime());

                String commentId = String.valueOf(System.currentTimeMillis());

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("commentContent", comment);
                hashMap.put("commentId", commentId);
                hashMap.put("postId", postId);
                hashMap.put("imageComment", "noImage");
                hashMap.put("userId", user.getUid());
                hashMap.put("commentDate", saveDate);
                hashMap.put("commentTime", saveTime);

                databaseReference.child(commentId).setValue(hashMap).addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(CommentsActivity.this, add, Toast.LENGTH_SHORT).show();
                    edComment.setText("");
                    imageUri = null;
                    imageCamera.setVisibility(View.GONE);
                    imageCamera.setImageURI(null);
                    upDateCommentCount();
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CommentsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        edComment.setText("");
                    }
                });

            }

            else {

                progressDialog.setTitle(save);
                progressDialog.setMessage(wait);

                progressDialog.show();
                progressDialog.setCanceledOnTouchOutside(false);

                databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

                Calendar cDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
                saveDate = currentDate.format(cDate.getTime());

                Calendar cTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
                saveTime = currentTime.format(cTime.getTime());

                String commentId = String.valueOf(System.currentTimeMillis());

                storageReferenceComment.child(postId).child(commentId).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()) {

                            storageReferenceComment.child(postId).child(commentId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("commentContent", comment);
                                    hashMap.put("commentId", commentId);
                                    hashMap.put("postId", postId);
                                    hashMap.put("imageComment", uri.toString());
                                    hashMap.put("userId", user.getUid());
                                    hashMap.put("commentDate", saveDate);
                                    hashMap.put("commentTime", saveTime);

                                    databaseReference.child(commentId).setValue(hashMap).addOnSuccessListener(aVoid -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(CommentsActivity.this, add, Toast.LENGTH_SHORT).show();
                                        edComment.setText("");
                                        imageUri = null;
                                        imageCamera.setVisibility(View.GONE);
                                        imageCamera.setImageURI(null);
                                        upDateCommentCount();
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(CommentsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                            edComment.setText("");
                                        }
                                    });

                                }
                            });

                        }

                        else {
                            progressDialog.dismiss();
                            String message = task.getException().getMessage();
                            Toast.makeText(CommentsActivity.this, "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }

        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageCamera.setImageURI(imageUri);
            imageCamera.setVisibility(View.VISIBLE);
            deleteImageBtn.setVisibility(View.VISIBLE);
        }
    }

    private void upDateCommentCount() {

        ismPrecessComment = true;
        String str= "";
        ref = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (ismPrecessComment){
                    String comments = "" + snapshot.child("commentsNum").getValue();
                    int count = Integer.parseInt(comments) + 1;
                    ref.child("commentsNum").setValue(count + str);
                    ismPrecessComment = false;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CommentsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}