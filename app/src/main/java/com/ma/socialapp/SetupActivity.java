package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CountryCodePicker ccp;

    private EditText edFullNameSetup, edPhoneSetup, edAddressSetup;
    private AutoCompleteTextView tvCity, tvCountry;
    private Spinner spSwitchAccount;
    private Button btnSaveProfile;
    private ProgressDialog loadingBar;
    private CircleImageView ProfileImage;
    private static final int REQUEST_CODE = 101;

    Uri imageUri;

    private DatabaseReference myRefUser, allRateRef;
    private StorageReference storageReferenceImage;
    private FirebaseAuth myAuth;
    private FirebaseUser myUser;
    String uid;

    String[] cities, countries;

    String save, attach, wait, add, require;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        edFullNameSetup = findViewById(R.id.ed_full_name_setup);
        edPhoneSetup = findViewById(R.id.ed_phone_setup);
        edAddressSetup = findViewById(R.id.ed_address_setup);
        ProfileImage = findViewById(R.id.image_profile);
        spSwitchAccount = findViewById(R.id.sp_switch);
        ccp = findViewById(R.id.ccp);

        ccp.registerCarrierNumberEditText(edPhoneSetup);

        btnSaveProfile = findViewById(R.id.save_profile_btn);

        loadingBar = new ProgressDialog(this);

        save = getResources().getString(R.string.save_data);
        attach = getResources().getString(R.string.attach_image_setup);
        wait = getResources().getString(R.string.please_wait);
        add = getResources().getString(R.string.data_is_saved);
        require = getResources().getString(R.string.required);

        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();

        uid = myUser.getUid();

        myRefUser = FirebaseDatabase.getInstance().getReference().child("Users");
        allRateRef = FirebaseDatabase.getInstance().getReference().child("Total Rating");
        storageReferenceImage = FirebaseStorage.getInstance().getReference().child("Profile Images");

        countries = getResources().getStringArray(R.array.countries);
        tvCountry = findViewById(R.id.ed_country_setup);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countries);

        tvCountry.setAdapter(adapter);

        cities = getResources().getStringArray(R.array.cities);
        tvCity = findViewById(R.id.ed_city_setup);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
        tvCity.setAdapter(adapter1);

        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(
                this,
                R.array.switch_account,
                R.layout.color_spinner_layout
        );

        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spSwitchAccount.setAdapter(adapter2);

        spSwitchAccount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        ProfileImage.setVisibility(View.GONE);
                        edPhoneSetup.setVisibility(View.GONE);
                        edFullNameSetup.setVisibility(View.GONE);
                        edAddressSetup.setVisibility(View.GONE);
                        tvCountry.setVisibility(View.GONE);
                        tvCity.setVisibility(View.GONE);
                        ccp.setVisibility(View.GONE);
                        btnSaveProfile.setVisibility(View.GONE);
                        break;
                    case 1:
                        String save = getResources().getString(R.string.btn_save_profile);
                        btnSaveProfile.setText(save);
                        ProfileImage.setVisibility(View.VISIBLE);
                        edPhoneSetup.setVisibility(View.VISIBLE);
                        edFullNameSetup.setVisibility(View.VISIBLE);
                        edAddressSetup.setVisibility(View.VISIBLE);
                        tvCountry.setVisibility(View.VISIBLE);
                        tvCity.setVisibility(View.VISIBLE);
                        ccp.setVisibility(View.VISIBLE);
                        btnSaveProfile.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        String next = getResources().getString(R.string.btn_next_profile);
                        btnSaveProfile.setText(next);
                        ProfileImage.setVisibility(View.VISIBLE);
                        edPhoneSetup.setVisibility(View.VISIBLE);
                        edFullNameSetup.setVisibility(View.VISIBLE);
                        edAddressSetup.setVisibility(View.VISIBLE);
                        tvCountry.setVisibility(View.VISIBLE);
                        tvCity.setVisibility(View.VISIBLE);
                        ccp.setVisibility(View.VISIBLE);
                        btnSaveProfile.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSaveProfile.setOnClickListener(v -> {
            int index = spSwitchAccount.getSelectedItemPosition();
            switch (index){
                case 1:
                    SaveProfileInfoToUser();
                    break;
                case 2:
                    SaveProfileInfoToWorker();
            }
        });

        ProfileImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, REQUEST_CODE);
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            ProfileImage.setImageURI(imageUri);
        }
    }

    private void SaveProfileInfoToUser() {

        String fullName = edFullNameSetup.getText().toString();
        String phone = edPhoneSetup.getText().toString();
        String city = tvCity.getText().toString();
        String address = edAddressSetup.getText().toString();
        String country = tvCountry.getText().toString();

        if (TextUtils.isEmpty(fullName)) {
            edFullNameSetup.setError(require);
        } else if (TextUtils.isEmpty(city)) {
            tvCity.setError(require);
        } else if (TextUtils.isEmpty(address)) {
            edAddressSetup.setError(require);
        } else if (TextUtils.isEmpty(phone)) {
            edPhoneSetup.setError(require);
        } else if (TextUtils.isEmpty(country)) {
            tvCountry.setError(require);
        }

        else if (imageUri == null) {
            Toast.makeText(this, attach, Toast.LENGTH_LONG).show();
        }
        else {

            loadingBar.setTitle(save);
            loadingBar.setMessage(wait);
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);

            storageReferenceImage.child(myUser.getUid()).putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    storageReferenceImage.child(myUser.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {

                        String phoneNumber = ccp.getFullNumberWithPlus();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("fullName", fullName);
                        userMap.put("phoneNumber", phoneNumber);
                        userMap.put("address", address);
                        userMap.put("userId", uid);
                        userMap.put("city", city);
                        userMap.put("specification", "noSpecification");
                        userMap.put("career", "noCareer");
                        userMap.put("country", country);
                        userMap.put("profileImage", uri.toString());

                        myRefUser.child(myUser.getUid()).updateChildren(userMap).addOnSuccessListener(o -> {

                            loadingBar.dismiss();
                            Intent intent = new Intent(SetupActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                            saveOpinionCount(uid);

                            Toast.makeText(SetupActivity.this, add, Toast.LENGTH_LONG).show();

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingBar.dismiss();
                                Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dismiss();
                            Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    String message = task.getException().getMessage();
                    loadingBar.dismiss();
                    Toast.makeText(SetupActivity.this, "Error Occurred : " + message, Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private void saveOpinionCount(final String uid) {

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("ratingCount", "0");
        hashMap.put("userId", uid);

        allRateRef.child(uid).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(SetupActivity.this, add, Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void SaveProfileInfoToWorker() {

        String fullName = edFullNameSetup.getText().toString();
        String phone = edPhoneSetup.getText().toString();
        String city = tvCity.getText().toString();
        String address = edAddressSetup.getText().toString();
        String country = tvCountry.getText().toString();

        if (TextUtils.isEmpty(fullName)) {
            edFullNameSetup.setError(require);
        } else if (TextUtils.isEmpty(city)) {
            tvCity.setError(require);
        } else if (TextUtils.isEmpty(address)) {
            edAddressSetup.setError(require);
        } else if (TextUtils.isEmpty(phone)) {
            edPhoneSetup.setError(require);
        } else if (TextUtils.isEmpty(country)) {
            tvCountry.setError(require);
        }

        else if (imageUri == null) {
            Toast.makeText(this, attach, Toast.LENGTH_LONG).show();
        }
        else {
            loadingBar.setTitle(save);
            loadingBar.setMessage(wait);
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);

            storageReferenceImage.child(myUser.getUid()).putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    storageReferenceImage.child(myUser.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {

                        String phoneNumber = ccp.getFullNumberWithPlus();

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("fullName", fullName);
                        userMap.put("phoneNumber", phoneNumber);
                        userMap.put("address", address);
                        userMap.put("userId", uid);
                        userMap.put("city", city);
                        userMap.put("country", country);
                        userMap.put("profileImage", uri.toString());

                        myRefUser.child(myUser.getUid()).updateChildren(userMap).addOnSuccessListener(o -> {

                            loadingBar.dismiss();

                            Intent intent = new Intent(SetupActivity.this, SetupWorkerActivity.class);
                            startActivity(intent);
                            finish();

                            Toast.makeText(SetupActivity.this, add, Toast.LENGTH_LONG).show();

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingBar.dismiss();
                                Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dismiss();
                            Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    String message = task.getException().getMessage();
                    loadingBar.dismiss();
                    Toast.makeText(SetupActivity.this, "Error Occurred : " + message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}