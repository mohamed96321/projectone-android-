package com.ma.socialapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Adapter.OpinionAdapter;
import com.ma.socialapp.Adapter.OpinionViewMeAdapter;
import com.ma.socialapp.Model.OpinionUsers;

import java.util.ArrayList;

public class ViewOpinionMeActivity extends AppCompatActivity {


    private RecyclerView recyclerViewOpinion;

    private FirebaseAuth auth;

    private FirebaseUser user;

    private Toolbar toolbar;

    String currentId, view;

    private DatabaseReference opinionRef;
    private ImageButton arrowBtn;

    ArrayList<OpinionUsers> opinionUsersArrayList;

    OpinionViewMeAdapter opinionViewMeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_opinion_me);

        arrowBtn = findViewById(R.id.arrow_btn_view_opinion_me);

        recyclerViewOpinion = findViewById(R.id.all_opinion_for_worker_about_me);

        recyclerViewOpinion.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewOpinion.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        toolbar = findViewById(R.id.toolbar_view_opinion_me);

        view = getResources().getString(R.string.view_opinion);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(view);

        opinionUsersArrayList = new ArrayList<>();

        opinionViewMeAdapter = new OpinionViewMeAdapter(getApplicationContext(), opinionUsersArrayList);

        recyclerViewOpinion.setAdapter(opinionViewMeAdapter);


        auth = FirebaseAuth.getInstance();

        user = auth.getCurrentUser();

        currentId = user.getUid();

        opinionRef = FirebaseDatabase.getInstance().getReference("Opinions").child(currentId);

        opinionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                opinionUsersArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    OpinionUsers opinionUsers = ds.getValue(OpinionUsers.class);

                    opinionUsersArrayList.add(opinionUsers);

                    opinionViewMeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        arrowBtn.setOnClickListener(v -> {
            onBackPressed();
        });

    }
}