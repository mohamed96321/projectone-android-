package com.ma.socialapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.ma.socialapp.Model.ReportsModel;
import com.ma.socialapp.ProfileSearchAdminAccountsActivity;
import com.ma.socialapp.R;
import com.ma.socialapp.ReportWorkersDetailsActivity;
import com.ma.socialapp.WorkersDataDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportWorkerAdapter extends RecyclerView.Adapter<ReportWorkerAdapter.myViewHolder> {

    private Context context;
    private ArrayList<ReportsModel> reportsModelArrayList;

    public ReportWorkerAdapter(Context context, ArrayList<ReportsModel> reportsModelArrayList) {
        this.context = context;
        this.reportsModelArrayList = reportsModelArrayList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_workers_result, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        ReportsModel reportsModel = reportsModelArrayList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    DataUsers dataUsers = dataSnapshot.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(reportsModel.getFromUserId())){

                        if (dataUsers.getCareer().equals("noCareer")){

                            holder.career.setVisibility(View.INVISIBLE);
                        }

                        else {

                            holder.career.setText(dataUsers.getCareer());
                        }

                        holder.username.setText(dataUsers.getFullName());
                        Picasso.get().load(dataUsers.getProfileImage()).into(holder.imageProfile);
                    }
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
        return reportsModelArrayList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        CircleImageView imageProfile;
        TextView username, career;
        ImageButton removerReport, showReport, sendReport;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_report_profile_from);
            username = itemView.findViewById(R.id.username_tv_report_worker_from);
            career = itemView.findViewById(R.id.career_tv_report_worker_from);
            removerReport = itemView.findViewById(R.id.remover_worker_report);
            showReport = itemView.findViewById(R.id.view_data_worker_report);
            sendReport = itemView.findViewById(R.id.accept_worker_report);

            showReport.setOnClickListener(v -> {

                Intent intent = new Intent(context, ReportWorkersDetailsActivity.class);

                int i = getAdapterPosition();

                intent.putExtra("fromUserId", reportsModelArrayList.get(i).getFromUserId());
                intent.putExtra("reportContent", reportsModelArrayList.get(i).getReportContent());
                intent.putExtra("toUserId", reportsModelArrayList.get(i).getToUserId());
                intent.putExtra("reportId", reportsModelArrayList.get(i).getReportId());
                intent.putExtra("imageReport", reportsModelArrayList.get(i).getImageReport());

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);


            });


        }
    }

}
