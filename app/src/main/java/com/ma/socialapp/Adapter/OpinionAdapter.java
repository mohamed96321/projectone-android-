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
import com.ma.socialapp.DeleteOpinionActivity;
import com.ma.socialapp.EditOpinionActivity;
import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.Model.OpinionUsers;
import com.ma.socialapp.R;
import com.ma.socialapp.ReportOpinionActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class OpinionAdapter extends RecyclerView.Adapter<OpinionAdapter.myViewHolder> {


    private Context context;
    private ArrayList<OpinionUsers> opinionUsersArrayList;

    String userId, currentId, deleteMessage, editMessage;

    public OpinionAdapter(Context context, ArrayList<OpinionUsers> opinionUsersArrayList) {
        this.context = context;
        this.opinionUsersArrayList = opinionUsersArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.opinion_view, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        OpinionUsers opinionUsers = opinionUsersArrayList.get(position);

        userId = opinionUsers.getToUserId();

        deleteMessage = holder.itemView.getResources().getString(R.string.opinion_not_deleted);
        editMessage = holder.itemView.getResources().getString(R.string.opinion_not_edit);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        currentId = user.getUid();

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

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (currentId.equals(opinionUsers.getFromUserId())){

                    holder.report.setVisibility(View.INVISIBLE);
                    holder.edit.setVisibility(View.VISIBLE);
                    holder.delete.setVisibility(View.VISIBLE);

                    holder.imageViewReport.setVisibility(View.INVISIBLE);
                    holder.imageViewEdit.setVisibility(View.VISIBLE);
                    holder.imageViewDelete.setVisibility(View.VISIBLE);


                }
                else {

                    holder.report.setVisibility(View.VISIBLE);
                    holder.edit.setVisibility(View.INVISIBLE);
                    holder.delete.setVisibility(View.INVISIBLE);

                    holder.imageViewReport.setVisibility(View.VISIBLE);
                    holder.imageViewEdit.setVisibility(View.INVISIBLE);
                    holder.imageViewDelete.setVisibility(View.INVISIBLE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }

        });

    }

    @Override
    public int getItemCount() {
        return opinionUsersArrayList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageUser;

        TextView username;

        TextView tvContent;

        TextView edit;

        TextView report;
        TextView delete;


        ImageView imageViewDelete;
        ImageView imageViewEdit;
        ImageView imageViewReport;

        RatingBar ratingBarMe;

        DatabaseReference databaseReference;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imageUser = itemView.findViewById(R.id.image_user_opinion);
            username = itemView.findViewById(R.id.tv__username_opinion);
            tvContent = itemView.findViewById(R.id.tv_opinion);
            ratingBarMe = itemView.findViewById(R.id.rating_opinion_view);

            delete = itemView.findViewById(R.id.view_delete_opinion);

            report = itemView.findViewById(R.id.view_report_opinion);
            edit = itemView.findViewById(R.id.view_edit_opinion);
            imageViewDelete = itemView.findViewById(R.id.image_delete_opinion);

            imageViewEdit = itemView.findViewById(R.id.image_edit_opinion);
            imageViewReport = itemView.findViewById(R.id.image_report_opinion);

            databaseReference = FirebaseDatabase.getInstance().getReference("Opinions");

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(userId).child(currentId).exists()){

                        edit.setOnClickListener(v -> {

                            Intent intent = new Intent(context, EditOpinionActivity.class);

                            int i = getAdapterPosition();

                            intent.putExtra("opinionContent", opinionUsersArrayList.get(i).getOpinionContent());
                            intent.putExtra("rating", opinionUsersArrayList.get(i).getRating());
                            intent.putExtra("toUserId", opinionUsersArrayList.get(i).getToUserId());
                            intent.putExtra("fromUserId", opinionUsersArrayList.get(i).getFromUserId());

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            context.startActivity(intent);


                        });


                        delete.setOnClickListener(v -> {

                            Intent intent = new Intent(context, DeleteOpinionActivity.class);

                            int i = getAdapterPosition();

                            intent.putExtra("opinionContent", opinionUsersArrayList.get(i).getOpinionContent());
                            intent.putExtra("rating", opinionUsersArrayList.get(i).getRating());
                            intent.putExtra("toUserId", opinionUsersArrayList.get(i).getToUserId());
                            intent.putExtra("fromUserId", opinionUsersArrayList.get(i).getFromUserId());
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            context.startActivity(intent);

                        });
                    }

                    else {

                        edit.setOnClickListener(v -> {
                            Toast.makeText(context, deleteMessage, Toast.LENGTH_SHORT).show();
                        });

                        delete.setOnClickListener(v -> {
                            Toast.makeText(context, editMessage, Toast.LENGTH_SHORT).show();
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });


            report.setOnClickListener(v -> {

                Intent intent = new Intent(context, ReportOpinionActivity.class);

                int i = getAdapterPosition();

                intent.putExtra("fromUserId", opinionUsersArrayList.get(i).getFromUserId());

                context.startActivity(intent);

            });


        }
    }
}
