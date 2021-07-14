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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;
import com.ma.socialapp.Model.DataUsers;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 300;

    private CountryCodePicker ccb;
    private ImageButton arrow_btn_edit_profile, btn_edit_profile_user;
    private CircleImageView image_profile_edit;
    private EditText edit_profile_name_ed, ed_phone_edit_profile, edit_profile_address_ed;
    private AutoCompleteTextView edit_profile_city_ed, edit_profile_country_ed;
    private Button edit_profile_btn;

    private Toolbar toolbar;

    String name, phone, city, address, country, image;

    String edit, require, wait, profile;

    Uri imageUri;

    private ProgressDialog progressDialog;

    private DatabaseReference userRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private StorageReference storageReferenceImageProfile;

    String[] cities, countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ed_phone_edit_profile = findViewById(R.id.ed_phone_edit_profile);
        edit_profile_address_ed = findViewById(R.id.edit_profile_address_ed);
        edit_profile_name_ed = findViewById(R.id.edit_profile_name_ed);

        arrow_btn_edit_profile = findViewById(R.id.arrow_btn_edit_profile);
        btn_edit_profile_user = findViewById(R.id.btn_edit_profile_user);
        image_profile_edit = findViewById(R.id.image_profile_edit);
        edit_profile_btn = findViewById(R.id.edit_profile_btn);

        toolbar = findViewById(R.id.toolbar_edit_profile);

        profile = getResources().getString(R.string.edit_profile);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(profile);

        progressDialog = new ProgressDialog(this);

        ccb = findViewById(R.id.ccp_edit_profile);

        edit = getResources().getString(R.string.edit_data);
        wait = getResources().getString(R.string.please_wait);
        require = getResources().getString(R.string.required);

        countries = getResources().getStringArray(R.array.countries);
        edit_profile_country_ed = findViewById(R.id.edit_profile_country_ed);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countries);

        edit_profile_country_ed.setAdapter(adapter);

        cities = getResources().getStringArray(R.array.cities);
        edit_profile_city_ed = findViewById(R.id.edit_profile_city_ed);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        edit_profile_city_ed.setAdapter(adapter1);

        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        userRef = FirebaseDatabase.getInstance().getReference("Users");

        storageReferenceImageProfile = FirebaseStorage.getInstance().getReference().child("Profile Images");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    DataUsers dataUsers = ds.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(user.getUid())){
                        name = dataUsers.getFullName();
                        phone = dataUsers.getPhoneNumber();
                        city = dataUsers.getCity();
                        country = dataUsers.getCountry();
                        address = dataUsers.getAddress();
                        image = dataUsers.getProfileImage();
                        edit_profile_name_ed.setText(name);
                        edit_profile_country_ed.setText(country);
                        edit_profile_city_ed.setText(city);
                        edit_profile_address_ed.setText(address);
                        ed_phone_edit_profile.setText(phone);
                        Picasso.get().load(image).into(image_profile_edit);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        arrow_btn_edit_profile.setOnClickListener(v -> {
            onBackPressed();
        });

        btn_edit_profile_user.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, REQUEST_CODE);
        });

        edit_profile_btn.setOnClickListener(v -> {
            saveDataProfile();
        });

    }

    private void saveDataProfile() {

        String ed_name = edit_profile_name_ed.getText().toString();
        String phone_only = ed_phone_edit_profile.getText().toString();
        String ed_city = edit_profile_city_ed.getText().toString();
        String ed_country = edit_profile_country_ed.getText().toString();
        String ed_address = edit_profile_address_ed.getText().toString();

        ccb.registerCarrierNumberEditText(ed_phone_edit_profile);

        String ed_phone = ccb.getFullNumberWithPlus();

        if (TextUtils.isEmpty(ed_name)){

            edit_profile_name_ed.setError(require);
        }

        else if (TextUtils.isEmpty(phone_only)){

            ed_phone_edit_profile.setError(require);
        }

        else if (TextUtils.isEmpty(ed_address)){

            edit_profile_address_ed.setError(require);
        }

        else if (TextUtils.isEmpty(ed_city)){

            edit_profile_city_ed.setError(require);
        }

        else if (TextUtils.isEmpty(ed_country)){

            edit_profile_country_ed.setError(require);
        }

        else if (!phone_only.equals(phone)){

            progressDialog.setTitle(edit);
            progressDialog.setMessage(wait);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            HashMap<String, Object> userMap = new HashMap<>();

            userMap.put("phoneNumber", ed_phone);
            userRef.child(user.getUid()).updateChildren(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if (!ed_name.equals(name)){

            progressDialog.setTitle(edit);
            progressDialog.setMessage(wait);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            HashMap<String, Object> userMap = new HashMap<>();

            userMap.put("fullName", ed_name);

            userRef.child(user.getUid()).updateChildren(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                }
            });
        }


        else if (!ed_city.equals(city)){

            progressDialog.setTitle(edit);
            progressDialog.setMessage(wait);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            HashMap<String, Object> userMap = new HashMap<>();

            userMap.put("city", ed_city);

            userRef.child(user.getUid()).updateChildren(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                }
            });
        }

        else if (!ed_country.equals(country)){

            progressDialog.setTitle(edit);
            progressDialog.setMessage(wait);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            HashMap<String, Object> userMap = new HashMap<>();

            userMap.put("country", ed_country);

            userRef.child(user.getUid()).updateChildren(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                }
            });
        }


        else if (!ed_address.equals(address)){

            progressDialog.setTitle(edit);
            progressDialog.setMessage(wait);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            HashMap<String, Object> userMap = new HashMap<>();

            userMap.put("address", ed_address);

            userRef.child(user.getUid()).updateChildren(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                }
            });
        }

        else if (imageUri != null){

            progressDialog.setTitle(edit);
            progressDialog.setMessage(wait);
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);

            storageReferenceImageProfile.child(user.getUid()).putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    storageReferenceImageProfile.child(user.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
                        HashMap<String, Object> userMap = new HashMap<>();

                        userMap.put("profileImage", uri.toString());

                        userRef.child(user.getUid()).updateChildren(userMap).addOnSuccessListener(o -> {

                            progressDialog.dismiss();
                            onBackPressed();

                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        });
                    });
                }
                else {
                    String message = task.getException().getMessage();
                    progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "Error Occurred : " + message, Toast.LENGTH_LONG).show();
                }
            });
        }


        else {

            HashMap<String, Object> userMap = new HashMap<>();

            userMap.put("fullName", name);
            userMap.put("phoneNumber", phone);
            userMap.put("address", address);
            userMap.put("city", city);
            userMap.put("country", country);
            userMap.put("profileImage", image);


            userRef.child(user.getUid()).updateChildren(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(EditProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();

                }
            });

        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            image_profile_edit.setImageURI(imageUri);
        }
    }
}