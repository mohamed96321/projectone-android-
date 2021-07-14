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
import com.ma.socialapp.Adapter.ReportWorkerAdapter;
import com.ma.socialapp.Model.ReportsModel;

import java.util.ArrayList;


public class FragmentReportsWorkersAdmin extends Fragment {

    public FragmentReportsWorkersAdmin() {
        // Required empty public constructor
    }

    View view;

    private RecyclerView allReportsWorkers;

    private DatabaseReference databaseReferenceReport;

    ArrayList<ReportsModel> reportsModelArrayList;

    ReportWorkerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_reports_workers_admin, container, false);

        allReportsWorkers = view.findViewById(R.id.all_reports_workers);

        allReportsWorkers.setHasFixedSize(true);
        allReportsWorkers.setLayoutManager(new LinearLayoutManager(getContext()));

        reportsModelArrayList = new ArrayList<>();

        adapter = new ReportWorkerAdapter(getActivity(), reportsModelArrayList);

        allReportsWorkers.setAdapter(adapter);

        databaseReferenceReport = FirebaseDatabase.getInstance().getReference("Report Workers");

        showReport();

        return view;
    }

    private void showReport() {

        databaseReferenceReport.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                reportsModelArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    ReportsModel reportsModel = dataSnapshot.getValue(ReportsModel.class);

                    reportsModelArrayList.add(reportsModel);

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