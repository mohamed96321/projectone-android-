package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class SetupWorkerActivity extends AppCompatActivity {

    private AutoCompleteTextView tvCareer;
    private EditText edSpecification;
    private Button btnSave, btnLoadingImage;
    private ImageView imageCareer;
    private ProgressDialog loadingBar;
    private static final int REQUEST_CODE = 101;
    Uri imageUri;
    private FirebaseUser myUser;
    private FirebaseAuth myAuth;
    private DatabaseReference myRefWorker, allRateRef;
    private StorageReference storageReferenceImage;

    String[] careers;

    String save, add, wait, attach, require, check, check2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_worker);

        edSpecification = findViewById(R.id.ed_specialization_setup);
        btnSave = findViewById(R.id.save_profile_btn_worker);
        btnLoadingImage = findViewById(R.id.btn_check_worker);
        imageCareer = findViewById(R.id.image_check_worker);
        loadingBar = new ProgressDialog(this);

        save = getResources().getString(R.string.save_data);
        add = getResources().getString(R.string.data_is_saved);
        attach = getResources().getString(R.string.attach_image_career);
        wait = getResources().getString(R.string.please_wait);
        require = getResources().getString(R.string.required);
        check = getResources().getString(R.string.admin_message_one);
        check2 = getResources().getString(R.string.admin_message_two);

        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();
        myRefWorker = FirebaseDatabase.getInstance().getReference().child("Users");
        allRateRef = FirebaseDatabase.getInstance().getReference().child("Total Rating");
        storageReferenceImage = FirebaseStorage.getInstance().getReference().child("Workers Career Images");

        careers = getResources().getStringArray(R.array.careers);
        tvCareer = findViewById(R.id.ed_career_setup);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, careers);
        tvCareer.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            SaveWorkerDataProfile();
        });

        btnLoadingImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageCareer.setImageURI(imageUri);
        }
    }

    private void SaveWorkerDataProfile() {
        String career = tvCareer.getText().toString();
        String specification = edSpecification.getText().toString();

        if (TextUtils.isEmpty(career)) {
            tvCareer.setError(require);
        } else if (TextUtils.isEmpty(specification)) {
            edSpecification.setError(require);
        } else if (imageUri == null) {
            Toast.makeText(this, attach, Toast.LENGTH_LONG).show();
        }
        else {

            loadingBar.setTitle(save);
            loadingBar.setMessage(wait);
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);

            storageReferenceImage.child(myUser.getUid()).putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    storageReferenceImage.child(myUser.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("career", career);
                        userMap.put("specification", specification);
                        userMap.put("careerImage", uri.toString());

                        myRefWorker.child(myUser.getUid()).updateChildren(userMap).addOnSuccessListener(o -> {

                            loadingBar.dismiss();
                            Intent intent = new Intent(SetupWorkerActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            String userId = myUser.getUid();

                            saveOpinionCount(userId);

                            Toast.makeText(SetupWorkerActivity.this, add , Toast.LENGTH_LONG).show();


                        }).addOnFailureListener(e -> {
                            loadingBar.dismiss();
                            Toast.makeText(SetupWorkerActivity.this, e.toString(), Toast.LENGTH_LONG).show();

                        });
                    });

                } else {
                    String message = task.getException().getMessage();
                    loadingBar.dismiss();
                    Toast.makeText(this, "Error Occurred : " + message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void saveOpinionCount(final String userId) {

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("ratingCount", "0");
        hashMap.put("userId", userId);

        allRateRef.child(userId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(SetupWorkerActivity.this, add, Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetupWorkerActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}