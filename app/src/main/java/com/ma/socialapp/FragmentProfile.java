package com.ma.socialapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.ma.socialapp.Adapter.PostProfileAdapter;
import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.Model.OpinionUsers;
import com.ma.socialapp.Model.Posts;
import com.ma.socialapp.Model.TotalRate;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class FragmentProfile extends Fragment {

    public FragmentProfile(){
        // require empty constructor
    }

    View view;

    private TextView tvProfileFullName, tvProfileCareer, tvProfileSpecification,
            tvProfilePhone, tvProfileAddress, tvProfileCity, tvProfileCountry, tvRating;
    private CircleImageView circleImageProfile;

    private Button opinionBtn;
    private ImageView coverProfile;

    private RecyclerView rcUserPost;

    private RatingBar ratingBar;

    private DatabaseReference myRefUser, postRef, allUserIsRated, allRateRef;
    private FirebaseAuth myAuth;
    private FirebaseUser myUser;

    String no, yes, sign, sure, countRating, currentId;

    float totalCount, count, countUser;

    PostProfileAdapter postProfileAdapter;

    ArrayList<Posts> postsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_profile, container, false);

        tvProfileFullName = view.findViewById(R.id.profile_full_name);
        tvProfileSpecification = view.findViewById(R.id.profile_specialization);
        tvProfileCareer = view.findViewById(R.id.profile_career);
        tvProfilePhone = view.findViewById(R.id.profile_phone);
        tvProfileAddress = view.findViewById(R.id.profile_address);
        tvProfileCity = view.findViewById(R.id.profile_city);
        tvProfileCountry = view.findViewById(R.id.profile_country);
        opinionBtn = view.findViewById(R.id.view_opinion_about_you_btn);

        rcUserPost = view.findViewById(R.id.all_user_post);

        ratingBar = view.findViewById(R.id.rating_total_worker_profile);

        tvRating = view.findViewById(R.id.tv_rating_profile);

        ratingBar.setVisibility(View.GONE);
        tvRating.setVisibility(View.GONE);

        circleImageProfile = view.findViewById(R.id.image_profile);

        coverProfile = view.findViewById(R.id.image_cover);

        String careerStr = getResources().getString(R.string.career);
        String specificationStr = getResources().getString(R.string.specification);
        String phoneStr = getResources().getString(R.string.phone);
        String cityStr = getResources().getString(R.string.city);
        String addressStr = getResources().getString(R.string.address);
        String countryStr = getResources().getString(R.string.country);

        yes = getResources().getString(R.string.yes);
        no = getResources().getString(R.string.no);
        sign = getResources().getString(R.string.sign_out);
        sure = getResources().getString(R.string.sure_sign_out);

        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();

        currentId = myUser.getUid();

        myRefUser = FirebaseDatabase.getInstance().getReference().child("Users");

        allUserIsRated = FirebaseDatabase.getInstance().getReference().child("Total Users");

        allRateRef = FirebaseDatabase.getInstance().getReference("Total Rating");

        allUserIsRated.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.child(myUser.getUid()).exists()){
                    ratingBar.setVisibility(View.GONE);
                    tvRating.setVisibility(View.GONE);
                }
                else {

                    ratingBar.setVisibility(View.VISIBLE);
                    tvRating.setVisibility(View.VISIBLE);
                }

                countUser = (float) snapshot.child(myUser.getUid()).getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        allRateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TotalRate totalRate = dataSnapshot.getValue(TotalRate.class);
                    if (totalRate.getUserId().equals(currentId)){
                        if (totalRate.getRatingCount().equals("0")){
                            ratingBar.setRating(0);
                            ratingBar.setVisibility(View.GONE);
                            tvRating.setVisibility(View.GONE);
                        }
                        else {

                            ratingBar.setVisibility(View.VISIBLE);
                            tvRating.setVisibility(View.VISIBLE);

                            countRating = totalRate.getRatingCount();

                            count = Float.parseFloat(countRating);

                            try {

                                totalCount = count / countUser;

                                ratingBar.setRating(totalCount);

                            }
                            catch (Exception e) {
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        rcUserPost.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        rcUserPost.setLayoutManager(linearLayoutManager);

        linearLayoutManager.setReverseLayout(true);

        linearLayoutManager.setStackFromEnd(true);

        postsList = new ArrayList<>();

        postProfileAdapter = new PostProfileAdapter(getContext(), postsList);

        rcUserPost.setAdapter(postProfileAdapter);

        postRef = FirebaseDatabase.getInstance().getReference("Posts");

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    Posts posts = ds.getValue(Posts.class);
                    if (myUser.getUid().equals(posts.getUserId())){
                        postsList.add(posts);
                    }
                    postProfileAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error occurred : " + error, Toast.LENGTH_SHORT).show();
            }
        });

        opinionBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ViewOpinionMeActivity.class);
            startActivity(intent);
        });

        myRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    DataUsers dataUsers = ds.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(myUser.getUid())){

                        if (dataUsers.getCareer().equals("noCareer")){

                            tvProfileSpecification.setVisibility(View.GONE);
                            tvProfileCareer.setVisibility(View.GONE);
                            opinionBtn.setVisibility(View.GONE);
                            ratingBar.setVisibility(View.GONE);
                            tvRating.setVisibility(View.GONE);
                        }
                        else {

                            tvProfileCareer.setText(careerStr + " : " + dataUsers.getCareer());
                            tvProfileSpecification.setText(specificationStr + " : " +dataUsers.getSpecification());
                            opinionBtn.setVisibility(View.VISIBLE);
                        }
                        tvProfileFullName.setText(dataUsers.getFullName());
                        tvProfilePhone.setText(phoneStr + " : " +dataUsers.getPhoneNumber());
                        tvProfileAddress.setText(addressStr + " : " +dataUsers.getAddress());
                        tvProfileCity.setText(cityStr + " : " +dataUsers.getCity());
                        tvProfileCountry.setText(countryStr + " : " +dataUsers.getCountry());
                        Picasso.get().load(dataUsers.getProfileImage()).into(circleImageProfile);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit){
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        }

        if (id == R.id.logout_profile) {
            confirmLogout();
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmLogout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(sign);
        builder.setMessage(sure);
        builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        builder.setPositiveButton(yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                myAuth.signOut();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
