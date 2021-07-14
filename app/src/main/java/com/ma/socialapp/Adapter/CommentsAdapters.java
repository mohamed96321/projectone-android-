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
import com.hbb20.CountryCodePicker;
import com.ma.socialapp.EditCommentActivity;
import com.ma.socialapp.Model.CommentModel;
import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapters extends RecyclerView.Adapter<CommentsAdapters.myView> {

    Context context;
    ArrayList<CommentModel> commentModelsArray;

    String cancel;

    boolean ismPrecessComment = false;

    public CommentsAdapters(Context context, ArrayList<CommentModel> commentModelsArray) {
        this.context = context;
        this.commentModelsArray = commentModelsArray;
    }

    @NonNull
    @Override
    public myView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.frag_comments_result, parent, false);
        return new myView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myView holder, int position) {

        CommentModel commentModel = commentModelsArray.get(position);

        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);

        cancel = holder.itemView.getResources().getString(R.string.cancel);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String currentId = user.getUid();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    DataUsers dataUsers = ds.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(commentModel.getUserId())){
                        holder.userNameTv.setText(dataUsers.getFullName());
                        Picasso.get().load(dataUsers.getProfileImage()).into(holder.circleImageViewProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (currentId.equals(commentModel.getUserId())){

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


        holder.textViewComment.setText(commentModel.getCommentContent());
        holder.commentDateTv.setText(commentModel.getCommentDate());
        holder.commentTimeTv.setText(commentModel.getCommentTime());


        if (commentModel.getImageComment().equals("noImage")){

            holder.imageCommentAdapter.setVisibility(View.GONE);
        }

        else {

            try {

                Picasso.get().load(commentModel.getImageComment()).into(holder.imageCommentAdapter);

                holder.imageCommentAdapter.setVisibility(View.VISIBLE);

            }
            catch (Exception e){
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        holder.report.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            String reportComment = holder.itemView.getResources().getString(R.string.report_comment);

            String why = holder.itemView.getResources().getString(R.string.why_report_comment);

            String report = holder.itemView.getResources().getString(R.string.report_user);

            String require = holder.itemView.getResources().getString(R.string.required);

            String save = holder.itemView.getResources().getString(R.string.save_report);

            String wait = holder.itemView.getResources().getString(R.string.please_wait);

            String thanks = holder.itemView.getResources().getString(R.string.thanks_report);

            builder.setTitle(reportComment);

            LinearLayout linearLayout = new LinearLayout(context);

            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            EditText editTextReport = new EditText(context);

            linearLayout.addView(editTextReport);

            editTextReport.setHint(why);

            builder.setView(linearLayout);

            builder.setPositiveButton(report, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String value = editTextReport.getText().toString();
                    if (TextUtils.isEmpty(value)){
                        editTextReport.setError(require);
                    }

                    else {

                        progressDialog.setTitle(save);
                        progressDialog.setMessage(wait);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        String reportId = String.valueOf(System.currentTimeMillis());

                        DatabaseReference databaseReferenceReport = FirebaseDatabase.getInstance().getReference().child("Report Comments");
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        FirebaseUser user1 = auth.getCurrentUser();
                        String currentId =  user1.getUid();

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("reportId", reportId);
                        hashMap.put("FromUserId", currentId);
                        hashMap.put("ToUserId", commentModel.getUserId());
                        hashMap.put("postId", commentModel.getPostId());
                        hashMap.put("commentId", commentModel.getCommentId());
                        hashMap.put("reportContent", value);

                        databaseReferenceReport.child(reportId)
                                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(context, thanks, Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            });

            builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });

        holder.delete.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            String delete = holder.itemView.getResources().getString(R.string.delete_comment_name);

            String sure = holder.itemView.getResources().getString(R.string.sure_delete_comment);

            String delete2 = holder.itemView.getResources().getString(R.string.delete);
            String already = holder.itemView.getResources().getString(R.string.is_already_deleted);
            String cannot = holder.itemView.getResources().getString(R.string.cannot_edie);

            builder.setTitle(delete);

            builder.setMessage(sure);


            builder.setPositiveButton(delete2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ismPrecessComment = true;

                    String str= "";
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(commentModel.getPostId());

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String comments = "" + snapshot.child("commentsNum").getValue();

                                    if (comments.equals("0")){

                                        holder.delete.setOnClickListener(v1 -> {
                                            Toast.makeText(context, already, Toast.LENGTH_SHORT).show();
                                        });

                                        holder.edit.setOnClickListener(v1 -> {
                                            Toast.makeText(context, cannot, Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                    else {

                                        if ((ismPrecessComment)){

                                            int count = Integer.parseInt(comments) - 1;
                                            ref.child("commentsNum").setValue(count + str);
                                        }

                                        ismPrecessComment = false;

                                    }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    String delete3 = holder.itemView.getResources().getString(R.string.delete_comment);

                    DatabaseReference databaseReferenceComment = FirebaseDatabase.getInstance().getReference("Comments");

                    databaseReferenceComment.child(commentModel.getPostId()).child(commentModel.getCommentId()).removeValue();

                    Toast.makeText(context, delete3, Toast.LENGTH_SHORT).show();

                }
            });

            builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
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
        return commentModelsArray.size();
    }

    class myView extends RecyclerView.ViewHolder {

        TextView textViewComment;
        TextView userNameTv;
        TextView commentTimeTv;
        TextView commentDateTv;
        ImageView imageViewTime;

        ImageView imageViewDelete;
        ImageView imageViewEdit;
        ImageView imageViewReport;

        ImageView imageCommentAdapter;

        CircleImageView circleImageViewProfile;

        TextView delete, report, edit;

        CardView cardComment;

        public myView(@NonNull View itemView) {
            super(itemView);

            textViewComment = itemView.findViewById(R.id.tv_comment);

            circleImageViewProfile = itemView.findViewById(R.id.image_profile_comment_result);

            commentTimeTv = itemView.findViewById(R.id.time_comment);

            imageViewTime = itemView.findViewById(R.id.image_time_comment);

            userNameTv = itemView.findViewById(R.id.tv_comment_username);
            commentDateTv = itemView.findViewById(R.id.date_comment);

            cardComment = itemView.findViewById(R.id.card_view_comment);

            imageCommentAdapter = itemView.findViewById(R.id.image_comment_adapter);

            delete = itemView.findViewById(R.id.view_delete_comment);

            report = itemView.findViewById(R.id.view_report_comment);
            edit = itemView.findViewById(R.id.view_edit_comment);

            imageViewDelete = itemView.findViewById(R.id.image_delete);

            imageViewEdit = itemView.findViewById(R.id.image_edit);
            imageViewReport = itemView.findViewById(R.id.image_report);

            edit.setOnClickListener(v -> {

                Intent intent = new Intent(context, EditCommentActivity.class);
                int i = getAdapterPosition();
                intent.putExtra("commentId", commentModelsArray.get(i).getCommentId());
                intent.putExtra("commentContent", commentModelsArray.get(i).getCommentContent());
                intent.putExtra("userId", commentModelsArray.get(i).getUserId());
                intent.putExtra("postId", commentModelsArray.get(i).getPostId());
                intent.putExtra("imageComment", commentModelsArray.get(i).getImageComment());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            });

        }
    }
}
