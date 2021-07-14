 package com.ma.socialapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Adapter.SearchAdapters;
import com.ma.socialapp.Model.DataUsers;

import java.util.ArrayList;


 public class FragmentSearchWorker extends Fragment {

    public FragmentSearchWorker(){
        // require empty constructor
    }

    private RecyclerView rcWorkerProfileList;

    private TextView careerNotFound, textSearch;

    private DatabaseReference userRef;
    private ImageView imageSearch;

    private FirebaseUser user;

    SearchAdapters adapters;

    String search;

    ArrayList<DataUsers> workerDataArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_search_worker, container, false);

        rcWorkerProfileList = view.findViewById(R.id.all_careers_profile);

        userRef = FirebaseDatabase.getInstance().getReference("Users");

        user = FirebaseAuth.getInstance().getCurrentUser();

        imageSearch = view.findViewById(R.id.image_search_icon);
        textSearch = view.findViewById(R.id.text_search_career);
        rcWorkerProfileList.setHasFixedSize(true);

        rcWorkerProfileList.setLayoutManager(new LinearLayoutManager(getContext()));

        careerNotFound = view.findViewById(R.id.text_career_not_found);
        careerNotFound.setVisibility(View.GONE);
        search = getResources().getString(R.string.search_worker);

        workerDataArrayList = new ArrayList<>();

        adapters = new SearchAdapters(getActivity(), workerDataArrayList);

        rcWorkerProfileList.setAdapter(adapters);

        return view;
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

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchWorker(newText);

                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

     private void searchWorker(String str) {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workerDataArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    DataUsers workerData = ds.getValue(DataUsers.class);
                    assert workerData != null;
                    if (!workerData.getUserId().equals(user.getUid())){
                        if (workerData.getCareer().contains(str)||
                                workerData.getFullName().contains(str) ||
                                workerData.getSpecification().contains(str) ||
                                workerData.getCity().contains(str) ||
                                workerData.getAddress().contains(str) ||
                                workerData.getCountry().contains(str)){
                            careerNotFound.setVisibility(View.GONE);
                            imageSearch.setVisibility(View.GONE);
                            textSearch.setVisibility(View.GONE);
                            workerDataArrayList.add(workerData);
                        }
                        else {
                            careerNotFound.setVisibility(View.VISIBLE);
                            imageSearch.setVisibility(View.GONE);
                            textSearch.setVisibility(View.GONE);
                        }
                    }

                    adapters.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

     }
 }

