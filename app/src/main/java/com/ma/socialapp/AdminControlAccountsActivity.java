package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminControlAccountsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_control_accounts);

        toolbar = findViewById(R.id.toolbar_admin);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout_admin);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = findViewById(R.id.nav_view_admin);

        navigationView.setNavigationItemSelectedListener(this);


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container_admin,
                    new FragmentAdmin()).commit();
            navigationView.setCheckedItem(R.id.nav_admin);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.nav_admin:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container_admin,
                        new FragmentAdmin()).commit();
                String admin = getResources().getString(R.string.admin);
                getSupportActionBar().setTitle(admin);
                break;

            case R.id.nav_search_accounts_admin:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container_admin,
                        new FragmentSearchAccountsAdmin()).commit();
                String accounts = getResources().getString(R.string.search_accounts);
                getSupportActionBar().setTitle(accounts);
                break;

            case R.id.nav_review_admin:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container_admin,
                        new FragmentReviewsAdmin()).commit();
                String review = getResources().getString(R.string.reviews);
                getSupportActionBar().setTitle(review);
                break;

            case R.id.reports_workers:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container_admin,
                        new FragmentReportsWorkersAdmin()).commit();
                String report = getResources().getString(R.string.reports_workers_admin);
                getSupportActionBar().setTitle(report);
                break;

            case R.id.reports_all_users:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container_admin,
                        new FragmentReportsUsersAdmin()).commit();
                String report1 = getResources().getString(R.string.reports_users_admin);
                getSupportActionBar().setTitle(report1);
                break;

            case R.id.reports_app:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container_admin,
                        new FragmentReportsAppAdmin()).commit();
                String report2 = getResources().getString(R.string.reports_app_admin);
                getSupportActionBar().setTitle(report2);
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