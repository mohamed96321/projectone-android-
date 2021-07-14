package com.ma.socialapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.ProfileSearchAdminAccountsActivity;
import com.ma.socialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchUsersAdminAdapter extends RecyclerView.Adapter<SearchUsersAdminAdapter.myViewHolder> {

    private Context context;
    private ArrayList<DataUsers> dataUsersArrayList;

    public SearchUsersAdminAdapter(Context context, ArrayList<DataUsers> dataUsersArrayList) {
        this.context = context;
        this.dataUsersArrayList = dataUsersArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_users_admin_result, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {


        DataUsers dataUsers = dataUsersArrayList.get(position);


        if (dataUsers.getSpecification().equals("noSpecification")){

            holder.career.setVisibility(View.GONE);
        }

        else {

            holder.career.setVisibility(View.VISIBLE);

        }
        holder.career.setText(dataUsers.getCareer());
        holder.username.setText(dataUsers.getFullName());
        Picasso.get().load(dataUsers.getProfileImage()).into(holder.imageProfile);

    }

    @Override
    public int getItemCount() {
        return dataUsersArrayList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

       CircleImageView imageProfile;
       TextView username, career;
       ImageButton seeDetailsBtn;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_users_search_admin);
            username = itemView.findViewById(R.id.username_tv_users_search_admin);
            career = itemView.findViewById(R.id.career_tv_users_search_admin);
            seeDetailsBtn = itemView.findViewById(R.id.see_details_btn_users_search_admin);


            seeDetailsBtn.setOnClickListener(v -> {

                Intent intent = new Intent(context, ProfileSearchAdminAccountsActivity.class);

                int i = getAdapterPosition();

                intent.putExtra("fullName", dataUsersArrayList.get(i).getFullName());
                intent.putExtra("career", dataUsersArrayList.get(i).getCareer());
                intent.putExtra("specification", dataUsersArrayList.get(i).getSpecification());
                intent.putExtra("phoneNumber", dataUsersArrayList.get(i).getPhoneNumber());
                intent.putExtra("address", dataUsersArrayList.get(i).getAddress());
                intent.putExtra("city", dataUsersArrayList.get(i).getCity());
                intent.putExtra("country", dataUsersArrayList.get(i).getCountry());
                intent.putExtra("profileImage", dataUsersArrayList.get(i).getProfileImage());
                intent.putExtra("careerImage", dataUsersArrayList.get(i).getCareerImage());
                intent.putExtra("userId", dataUsersArrayList.get(i).getUserId());

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);


            });

        }
    }
}
