package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.ma.socialapp.Model.DataUsers;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Toolbar toolbar;

    private CircleImageView ciImageProfile;
    private TextView tvUserName, career;

    String check, check2;

    private DatabaseReference myRefUser;
    private FirebaseAuth myAuth;
    private FirebaseUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        View view = navigationView.inflateHeaderView(R.layout.nav_header);

        ciImageProfile = view.findViewById(R.id.home_profile_image);
        tvUserName = view.findViewById(R.id.home_user_full_name);
        career = view.findViewById(R.id.home_user_career);



        myAuth = FirebaseAuth.getInstance();
        myUser = myAuth.getCurrentUser();

        myRefUser = FirebaseDatabase.getInstance().getReference("Users");

        navigationView.setNavigationItemSelectedListener(this);

        check = getResources().getString(R.string.admin_message_one);
        check2 = getResources().getString(R.string.admin_message_two);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                    new FragmentHome()).commit();
            navigationView.setCheckedItem(R.id.home);
        }

        myRefUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    DataUsers dataUsers = ds.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(myUser.getUid())){

                        if (dataUsers.getCareer().equals("noCareer")){
                            career.setVisibility(View.GONE);
                        }
                        else {

                            career.setText(dataUsers.getCareer());
                        }
                        tvUserName.setText(dataUsers.getFullName());
                        Picasso.get().load(dataUsers.getProfileImage()).into(ciImageProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (myUser == null){
            SendUserToLoginActivity();
        }

    }

    private void SendUserToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        new FragmentHome()).commit();
                String home = getResources().getString(R.string.menu_home);
                getSupportActionBar().setTitle(home);
                break;

            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        new FragmentProfile()).commit();
                String profile = getResources().getString(R.string.menu_profile);
                getSupportActionBar().setTitle(profile);
                break;

            case R.id.search:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        new FragmentSearchWorker()).commit();
                String search = getResources().getString(R.string.menu_search);
                getSupportActionBar().setTitle(search);
                break;

            case R.id.review:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        new FragmentReview()).commit();
                String review = getResources().getString(R.string.menu_review);
                getSupportActionBar().setTitle(review);
                break;

            case R.id.report:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,
                        new FragmentReport()).commit();
                String report = getResources().getString(R.string.menu_report);
                getSupportActionBar().setTitle(report);
                break;


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }
}