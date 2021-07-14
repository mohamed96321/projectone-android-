package com.ma.socialapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Adapter.CheckWorkerAdapter;
import com.ma.socialapp.Adapter.SearchAdapters;
import com.ma.socialapp.Adapter.SearchUsersAdminAdapter;
import com.ma.socialapp.Model.DataUsers;

import java.util.ArrayList;

public class FragmentAdmin extends Fragment {


    public FragmentAdmin() {
        // Required empty public constructor
    }

    View view;

    private RecyclerView rcAllWorkers;

    private DatabaseReference databaseReferenceUsers;
    private FirebaseAuth myAuth;

    private TextView notFoundUser;

    ArrayList<DataUsers> dataUsersArrayList;

    CheckWorkerAdapter checkWorkerAdapter;

    String no, search, yes, sign, sure;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin, container, false);

        rcAllWorkers = view.findViewById(R.id.all_workers_check_info);

        notFoundUser = view.findViewById(R.id.text_user_not_found_admin);
        notFoundUser.setVisibility(View.GONE);

        yes = getResources().getString(R.string.yes);
        no = getResources().getString(R.string.no);
        sign = getResources().getString(R.string.sign_out);
        sure = getResources().getString(R.string.sure_sign_out);
        search =getResources().getString(R.string.search_worker);

        rcAllWorkers.setHasFixedSize(true);
        rcAllWorkers.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");

        myAuth = FirebaseAuth.getInstance();

        dataUsersArrayList = new ArrayList<>();

        checkWorkerAdapter = new CheckWorkerAdapter(getActivity(), dataUsersArrayList);

        rcAllWorkers.setAdapter(checkWorkerAdapter);

        showAllUsers();

        return view;
    }

    private void showAllUsers() {

        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dataUsersArrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()){
                    DataUsers dataUsers = ds.getValue(DataUsers.class);
                    dataUsersArrayList.add(dataUsers);
                    checkWorkerAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.log_out_admin, menu);

        MenuItem item = menu.findItem(R.id.search_admin);

        SearchView searchView = (SearchView) item.getActionView();

        searchView.setQueryHint(search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!TextUtils.isEmpty(query.trim())){
                    searchWorker(query);
                }

                else {
                    showAllUsers();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                if (!TextUtils.isEmpty(newText.trim())){
                    searchWorker(newText);
                }

                else {
                    showAllUsers();
                }

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);

    }


    private void searchWorker(String search) {

        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dataUsersArrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()){

                    DataUsers dataUsers = ds.getValue(DataUsers.class);

                    if (dataUsers.getCareer().toLowerCase().contains(search.toLowerCase())||
                            dataUsers.getFullName().toLowerCase().contains(search.toLowerCase())){

                        notFoundUser.setVisibility(View.GONE);
                        dataUsersArrayList.add(dataUsers);

                    }
                    else {

                        notFoundUser.setVisibility(View.VISIBLE);
                    }

                    checkWorkerAdapter = new CheckWorkerAdapter(getActivity(), dataUsersArrayList);

                    checkWorkerAdapter.notifyDataSetChanged();

                    rcAllWorkers.setAdapter(checkWorkerAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout_admin) {
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