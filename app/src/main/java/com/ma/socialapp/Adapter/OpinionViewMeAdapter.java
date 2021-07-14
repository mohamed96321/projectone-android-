package com.ma.socialapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ma.socialapp.EditOpinionActivity;
import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.Model.OpinionUsers;
import com.ma.socialapp.R;
import com.ma.socialapp.ReportOpinionMeActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class OpinionViewMeAdapter extends RecyclerView.Adapter<OpinionViewMeAdapter.myViewHolder> {

    private Context context;
    private ArrayList<OpinionUsers> opinionUsersArrayList;

    String cancel;

    public OpinionViewMeAdapter(Context context, ArrayList<OpinionUsers> opinionUsersArrayList) {
        this.context = context;
        this.opinionUsersArrayList = opinionUsersArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.opinion_me_view, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        OpinionUsers opinionUsers = opinionUsersArrayList.get(position);

        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);

        cancel = holder.itemView.getResources().getString(R.string.cancel);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    DataUsers dataUsers = ds.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(opinionUsers.getFromUserId())){
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


        holder.tvContent.setText(opinionUsers.getOpinionContent());

        if (opinionUsers.getRating() == 0){
            holder.ratingBarMe.setVisibility(View.GONE);
        }
        else {
            holder.ratingBarMe.setRating(opinionUsers.getRating());
        }

    }

    @Override
    public int getItemCount() {
        return opinionUsersArrayList.size();
    }

   class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageUser;

        TextView username;

        TextView tvContent;

        RatingBar ratingBarMe;

        TextView report;
        ImageView imageReport;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imageUser = itemView.findViewById(R.id.image_user_opinion_me);
            username = itemView.findViewById(R.id.tv__username_opinion_me);
            tvContent = itemView.findViewById(R.id.tv_opinion_me);
            ratingBarMe = itemView.findViewById(R.id.rating_opinion_view_me);

            report = itemView.findViewById(R.id.view_report_opinion_me);
            imageReport = itemView.findViewById(R.id.image_report_opinion_me);

            report.setOnClickListener(v -> {

                Intent intent = new Intent(context, ReportOpinionMeActivity.class);

                int i = getAdapterPosition();

                intent.putExtra("fromUserId", opinionUsersArrayList.get(i).getFromUserId());

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);

            });

        }
    }
}
