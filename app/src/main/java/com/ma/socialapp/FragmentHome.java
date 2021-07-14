package com.ma.socialapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Adapter.PostProfileAdapter;
import com.ma.socialapp.Adapter.SearchAdapters;
import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.Model.Posts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FragmentHome extends Fragment {

    public FragmentHome(){
        // require empty constructor
    }

    View view;

    private FloatingActionButton btnAddPost;
    private RecyclerView rcAllUsersPost;

    private TextView postNotFound;

    private DatabaseReference postRef;

    PostProfileAdapter postProfileAdapter;

    ArrayList<Posts> postsList;

    String search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_home, container, false);

        btnAddPost = view.findViewById(R.id.add_post);
        rcAllUsersPost = view.findViewById(R.id.all_users_post);

        postNotFound = view.findViewById(R.id.text_post_not_found);

        postNotFound.setVisibility(View.GONE);

        search = getResources().getString(R.string.search_post);

        rcAllUsersPost.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcAllUsersPost.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        postsList = new ArrayList<>();

        postProfileAdapter = new PostProfileAdapter(getContext(), postsList);

        rcAllUsersPost.setAdapter(postProfileAdapter);

        postRef = FirebaseDatabase.getInstance().getReference("Posts");

        allPosts();

        btnAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PostActivity.class);
            startActivity(intent);
        });


        return view;
    }


    private void allPosts() {

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postsList.clear();

                for (DataSnapshot ds: snapshot.getChildren()){

                    Posts posts = ds.getValue(Posts.class);

                    postsList.add(posts);

                    postProfileAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error occurred : " + error, Toast.LENGTH_SHORT).show();
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
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem item = menu.findItem(R.id.search_home);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!TextUtils.isEmpty(query.trim())){
                    searchPost(query);
                }

                else {
                    allPosts();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                if (!TextUtils.isEmpty(newText.trim())){
                    searchPost(newText);
                }

                else {
                    allPosts();
                }

                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void searchPost(String search) {

        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postsList.clear();

                for (DataSnapshot ds: snapshot.getChildren()){

                    Posts posts = ds.getValue(Posts.class);

                    assert posts != null;
                    if (posts.getPostDescription().contains(search)){

                        postsList.add(posts);

                        postNotFound.setVisibility(View.GONE);

                    }
                    else {
                        postNotFound.setVisibility(View.VISIBLE);
                    }

                    postProfileAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error occurred : " + error, Toast.LENGTH_SHORT).show();
            }
        });


    }


}
