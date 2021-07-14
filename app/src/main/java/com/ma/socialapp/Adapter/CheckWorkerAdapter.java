package com.ma.socialapp.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
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
import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.R;
import com.ma.socialapp.WorkersDataDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckWorkerAdapter extends RecyclerView.Adapter<CheckWorkerAdapter.myViewHolder> {

    private Context context;
    private ArrayList<DataUsers> dataUsersArrayList;

    public CheckWorkerAdapter(Context context, ArrayList<DataUsers> dataUsersArrayList) {
        this.context = context;
        this.dataUsersArrayList = dataUsersArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.accept_or_refuse_admin, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {


        DataUsers dataUsers = dataUsersArrayList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(dataUsers.getUserId()).child("accepted").exists()){

                    holder.accepted_tv.setVisibility(View.VISIBLE);
                    holder.accepted_image_view.setVisibility(View.VISIBLE);
                }
                else {
                    holder.accept_worker.setVisibility(View.VISIBLE);
                    holder.remover_worker.setVisibility(View.VISIBLE);
                    holder.view_data_worker.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        if (!dataUsers.getSpecification().equals("noSpecification")){

            holder.card_view_accept.setVisibility(View.VISIBLE);

            holder.career_tv_check_account.setText(dataUsers.getCareer());
            holder.username_tv_check_account.setText(dataUsers.getFullName());
            Picasso.get().load(dataUsers.getProfileImage()).into(holder.image_worker_profile);

        }

        holder.accept_worker.setOnClickListener(v -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Accept Worker");
            builder.setMessage("Are you sure you want to accept worker?");
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    progressDialog.setTitle("Add Worker");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("accepted", "accepted");

                    databaseReference.child(dataUsers.getUserId()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            progressDialog.dismiss();

                            Toast.makeText(context, "Worker is accepted", Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        holder.remover_worker.setOnClickListener(v -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Remove Worker");
            builder.setMessage("Are you sure you want to remove worker?");

            builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    progressDialog.setTitle("Remove Worker");
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    databaseReference.child(dataUsers.getUserId()).removeValue();

                    progressDialog.dismiss();

                    Toast.makeText(context, "Data Worker is removed", Toast.LENGTH_LONG).show();

                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

    }

    @Override
    public int getItemCount() {
        return dataUsersArrayList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

       CircleImageView image_worker_profile;
       TextView username_tv_check_account, career_tv_check_account, accepted_tv;
       ImageButton accept_worker, remover_worker, view_data_worker;
       CardView card_view_accept;

       ImageView accepted_image_view;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            image_worker_profile = itemView.findViewById(R.id.image_worker_profile);
            username_tv_check_account = itemView.findViewById(R.id.username_tv_check_account);
            career_tv_check_account = itemView.findViewById(R.id.career_tv_check_account);
            accept_worker = itemView.findViewById(R.id.accept_worker);
            remover_worker = itemView.findViewById(R.id.remover_worker);
            view_data_worker = itemView.findViewById(R.id.view_data_worker);

            card_view_accept = itemView.findViewById(R.id.card_view_accept);
            accepted_tv = itemView.findViewById(R.id.accepted_tv);
            accepted_image_view = itemView.findViewById(R.id.accepted_image_view);

            card_view_accept.setVisibility(View.GONE);

            accept_worker.setVisibility(View.GONE);
            remover_worker.setVisibility(View.GONE);
            view_data_worker.setVisibility(View.GONE);
            accepted_tv.setVisibility(View.GONE);
            accepted_image_view.setVisibility(View.GONE);

            view_data_worker.setOnClickListener(v -> {

                Intent intent = new Intent(context, WorkersDataDetailsActivity.class);

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
