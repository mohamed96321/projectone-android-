package com.ma.socialapp;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Adapter.SearchAdapters;
import com.ma.socialapp.Adapter.SearchUsersAdminAdapter;
import com.ma.socialapp.Model.DataUsers;

import java.util.ArrayList;

public class FragmentSearchAccountsAdmin extends Fragment {

    public FragmentSearchAccountsAdmin() {
        // Required empty public constructor
    }

    View view;

    private RecyclerView recyclerViewUsers;

    private TextView notFoundUser;

    private DatabaseReference databaseReferenceUsers;

    ArrayList<DataUsers> dataUsersArrayList;

    SearchUsersAdminAdapter searchUsersAdminAdapter;

    String search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_search_accounts_admin, container, false);

        recyclerViewUsers = view.findViewById(R.id.all_search_accounts_users);

        notFoundUser = view.findViewById(R.id.text_user_not_found);

        notFoundUser.setVisibility(View.GONE);

        search = getResources().getString(R.string.search_user);

        recyclerViewUsers.setHasFixedSize(true);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("Users");

        dataUsersArrayList = new ArrayList<>();

        searchUsersAdminAdapter = new SearchUsersAdminAdapter(getActivity(), dataUsersArrayList);

        recyclerViewUsers.setAdapter(searchUsersAdminAdapter);

        allUsers();

        return view;
    }

    private void allUsers() {

        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dataUsersArrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()){

                    DataUsers dataUsers = ds.getValue(DataUsers.class);
                    dataUsersArrayList.add(dataUsers);
                    searchUsersAdminAdapter.notifyDataSetChanged();

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
        inflater.inflate(R.menu.search_toolbar, menu);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                if (!TextUtils.isEmpty(query.trim())){
                    searchUsers(query);
                }

                else {
                    allUsers();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (!TextUtils.isEmpty(newText.trim())){
                    searchUsers(newText);
                }

                else {
                    allUsers();
                }

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchUsers(String search) {

        databaseReferenceUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                dataUsersArrayList.clear();

                for (DataSnapshot ds : snapshot.getChildren()){

                    DataUsers dataUsers = ds.getValue(DataUsers.class);

                    if (dataUsers.getCareer().toLowerCase().contains(search.toLowerCase())||
                            dataUsers.getFullName().toLowerCase().contains(search.toLowerCase()) ||
                            dataUsers.getSpecification().toLowerCase().contains(search.toLowerCase()) ||
                            dataUsers.getCity().toLowerCase().contains(search.toLowerCase()) ||
                            dataUsers.getAddress().toLowerCase().contains(search.toLowerCase()) ||
                            dataUsers.getCountry().toLowerCase().contains(search.toLowerCase())){

                        notFoundUser.setVisibility(View.GONE);
                        dataUsersArrayList.add(dataUsers);

                    }
                    else {

                        notFoundUser.setVisibility(View.VISIBLE);
                    }

                    searchUsersAdminAdapter = new SearchUsersAdminAdapter(getActivity(), dataUsersArrayList);

                    searchUsersAdminAdapter.notifyDataSetChanged();
                    recyclerViewUsers.setAdapter(searchUsersAdminAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}