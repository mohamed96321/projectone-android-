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
import com.ma.socialapp.Adapter.ReportAllUsersAdapter;
import com.ma.socialapp.Model.ReportsModel;

import java.util.ArrayList;

public class FragmentReportsUsersAdmin extends Fragment {

    public FragmentReportsUsersAdmin() {
        // Required empty public constructor
    }

    View view;

    private RecyclerView allReportUsers;

    private DatabaseReference databaseReference;

    ReportAllUsersAdapter adapter;

    ArrayList<ReportsModel> reportAllUsersArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_reports_users_admin, container, false);

        allReportUsers = view.findViewById(R.id.all_reports_users);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        allReportUsers.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        allReportUsers.setHasFixedSize(true);

        reportAllUsersArrayList = new ArrayList<>();

        adapter = new ReportAllUsersAdapter(getActivity(), reportAllUsersArrayList);

        allReportUsers.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("UserReports");

        showAllReports();

        return view;
    }

    private void showAllReports() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportAllUsersArrayList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    ReportsModel reportAllUsers = dataSnapshot.getValue(ReportsModel.class);
                    reportAllUsersArrayList.add(reportAllUsers);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}