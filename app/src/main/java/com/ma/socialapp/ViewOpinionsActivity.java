package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Adapter.OpinionAdapter;
import com.ma.socialapp.Model.OpinionUsers;

import java.util.ArrayList;

public class ViewOpinionsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOpinion;

    String fullName, userId;

    private Toolbar toolbar;

    private DatabaseReference opinionRef;
    private ImageButton arrowBtn;

    ArrayList<OpinionUsers> opinionUsersArrayList;

    OpinionAdapter opinionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_opinions);


        userId = getIntent().getExtras().getString("userId");
        fullName = getIntent().getExtras().getString("fullName");

        arrowBtn = findViewById(R.id.arrow_btn_view_opinion);

        recyclerViewOpinion = findViewById(R.id.all_opinion_for_worker);

        recyclerViewOpinion.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewOpinion.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        opinionUsersArrayList = new ArrayList<>();

        toolbar = findViewById(R.id.toolbar_view_opinion);

        opinionAdapter = new OpinionAdapter(getApplicationContext(), opinionUsersArrayList);

        recyclerViewOpinion.setAdapter(opinionAdapter);

        opinionRef = FirebaseDatabase.getInstance().getReference("Opinions").child(userId);

        opinionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                opinionUsersArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    OpinionUsers opinionUsers = ds.getValue(OpinionUsers.class);
                    opinionUsersArrayList.add(opinionUsers);
                    opinionAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewOpinionsActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        String aboutMe = getResources().getString(R.string.opinion_about);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(aboutMe + " " + fullName);

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}