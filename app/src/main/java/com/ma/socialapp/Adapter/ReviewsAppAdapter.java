package com.ma.socialapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.Model.OpinionUsers;
import com.ma.socialapp.Model.ReviewUsersApp;
import com.ma.socialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsAppAdapter extends RecyclerView.Adapter<ReviewsAppAdapter.myViewHolder> {

    private Context context;
    private ArrayList<ReviewUsersApp> reviewUsersAppArrayList;

    public ReviewsAppAdapter(Context context, ArrayList<ReviewUsersApp> reviewUsersAppArrayList) {
        this.context = context;
        this.reviewUsersAppArrayList = reviewUsersAppArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_app_result, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        ReviewUsersApp reviewUsersApp = reviewUsersAppArrayList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    DataUsers dataUsers = ds.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(reviewUsersApp.getUserId())){
                        if (dataUsers.getCareer().equals("noCareer")){
                            holder.career.setVisibility(View.GONE);
                        }
                        else {
                            holder.career.setText(dataUsers.getCareer());
                        }
                        holder.username.setText(dataUsers.getFullName());
                        Picasso.get().load(dataUsers.getProfileImage()).into(holder.imageUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        holder.tvContent.setText(reviewUsersApp.getReviewContent());

        if (reviewUsersApp.getRating() == 0){
            holder.ratingBarMe.setVisibility(View.GONE);
        }
        else {
            holder.ratingBarMe.setRating(reviewUsersApp.getRating());
        }
    }

    @Override
    public int getItemCount() {
        return reviewUsersAppArrayList.size();
    }

   class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageUser;

        TextView username;

        TextView tvContent;

        RatingBar ratingBarMe;

        TextView career;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imageUser = itemView.findViewById(R.id.image_user_review_app);
            username = itemView.findViewById(R.id.tv__username_review_app);
            tvContent = itemView.findViewById(R.id.tv_review_app);
            ratingBarMe = itemView.findViewById(R.id.rating_review_app);

            career = itemView.findViewById(R.id.tv_career_review_app);


        }
    }
}
