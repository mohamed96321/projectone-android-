package com.ma.socialapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Adapter.OpinionAdapter;
import com.ma.socialapp.Adapter.ReviewsAppAdapter;
import com.ma.socialapp.Model.ReviewUsersApp;

import java.util.ArrayList;

public class FragmentReviewsAdmin extends Fragment {

    public FragmentReviewsAdmin() {
        // Required empty public constructor
    }

    View view;

    private RecyclerView allReviewsApp;

    private DatabaseReference databaseReference;

    ArrayList<ReviewUsersApp> reviewUsersAppArrayList;

    ReviewsAppAdapter appAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view =  inflater.inflate(R.layout.fragment_reviews_admin, container, false);

        allReviewsApp = view.findViewById(R.id.all_reviews_app);

        allReviewsApp.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        allReviewsApp.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        reviewUsersAppArrayList = new ArrayList<>();

        appAdapter = new ReviewsAppAdapter(getActivity(), reviewUsersAppArrayList);

        allReviewsApp.setAdapter(appAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Reviews");

        showAllReviews();

        return view;
    }

    private void showAllReviews() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewUsersAppArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    ReviewUsersApp app = dataSnapshot.getValue(ReviewUsersApp.class);

                    reviewUsersAppArrayList.add(app);

                    appAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}