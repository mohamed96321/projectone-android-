package com.ma.socialapp.Adapter;

import android.content.Context;
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
import com.ma.socialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReportAllUsersAdapter extends RecyclerView.Adapter<ReportAllUsersAdapter.myView> {

    private Context context;

    private ArrayList<ReportsModel> reportAllUsersArrayList;

    public ReportAllUsersAdapter(Context context, ArrayList<ReportsModel> reportAllUsersArrayList) {
        this.context = context;
        this.reportAllUsersArrayList = reportAllUsersArrayList;
    }

    String toUserId, fromUserId, reportId, reportContent;

    @NonNull
    @Override
    public myView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.report_users_result, parent, false);
        return new myView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myView holder, int position) {

        ReportsModel reportAllUsers = reportAllUsersArrayList.get(position);

        toUserId = reportAllUsers.getToUserId();
        fromUserId = reportAllUsers.getFromUserId();
        reportId = reportAllUsers.getReportId();
        reportContent = reportAllUsers.getReportContent();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    DataUsers dataUsers = dataSnapshot.getValue(DataUsers.class);
                    assert dataUsers != null;
                    if (dataUsers.getUserId().equals(fromUserId)){
                        if (dataUsers.getCareer().equals("noCareer")){
                            holder.careerFrom.setVisibility(View.GONE);
                        }
                        else {
                            holder.careerFrom.setText(dataUsers.getCareer());
                        }
                        holder.usernameFrom.setText(dataUsers.getFullName());
                        Picasso.get().load(dataUsers.getProfileImage()).into(holder.circleImageViewProfile);
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
        return reportAllUsersArrayList.size();
    }

    public class myView extends RecyclerView.ViewHolder {

        CircleImageView circleImageViewProfile;

        TextView usernameFrom, careerFrom;

        ImageButton btnDetails;

        public myView(@NonNull View itemView) {
            super(itemView);

            circleImageViewProfile = itemView.findViewById(R.id.image_report_users_from);

            usernameFrom = itemView.findViewById(R.id.username_tv_report_users_from);

            careerFrom = itemView.findViewById(R.id.career_tv_report_users_from);

            btnDetails = itemView.findViewById(R.id.see_details_btn_report_users);

        }
    }
}
