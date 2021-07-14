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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.ma.socialapp.CommentsActivity;
import com.ma.socialapp.EditCommentActivity;
import com.ma.socialapp.EditPostActivity;
import com.ma.socialapp.FragmentHome;
import com.ma.socialapp.FragmentProfile;
import com.ma.socialapp.ImagePostActivity;
import com.ma.socialapp.Model.DataUsers;
import com.ma.socialapp.Model.Posts;
import com.ma.socialapp.ProfilePostActivity;
import com.ma.socialapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileAdapter.myHolder> {

    private Context context;
    private ArrayList<Posts> postsList;

    Boolean likeChecker = false;

    String cancel;

    public PostProfileAdapter(Context context, ArrayList<Posts> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public myHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.frag_post_view, parent, false);
        return new myHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myHolder holder, int position) {

        Posts posts = postsList.get(position);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference("Likes");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String currentId = user.getUid();

        ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(context);

        holder.setLikeButton(posts.getPostId());

        holder.btnLike.setOnClickListener(v -> {

            likeChecker = true;

            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (likeChecker.equals(true)){
                        if (snapshot.child(posts.getPostId()).hasChild(currentId)){
                            likeRef.child(posts.getPostId()).child(currentId).removeValue();
                        }
                        else {
                            likeRef.child(posts.getPostId()).child(currentId).setValue(true);
                        }
                        likeChecker = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    DataUsers dataUsers = ds.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(posts.getUserId())){
                        if (dataUsers.getCareer().equals("noCareer")){
                            holder.tvCareer.setVisibility(View.GONE);

                        }
                        else {
                            holder.tvCareer.setVisibility(View.VISIBLE);
                        }
                        holder.tvCareer.setText(dataUsers.getCareer());
                        holder.tvUsername.setText(dataUsers.getFullName());
                        Picasso.get().load(dataUsers.getProfileImage()).into(holder.ciProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        cancel = holder.itemView.getResources().getString(R.string.cancel);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    DataUsers dataUsers = ds.getValue(DataUsers.class);
                    if (dataUsers.getUserId().equals(posts.getUserId())){
                        if (dataUsers.getCareer().equals("noCareer")){
                            holder.tvCareer.setVisibility(View.GONE);

                        }
                        else {
                            holder.tvCareer.setVisibility(View.VISIBLE);
                        }
                        holder.tvCareer.setText(dataUsers.getCareer());
                        holder.tvUsername.setText(dataUsers.getFullName());
                        Picasso.get().load(dataUsers.getProfileImage()).into(holder.ciProfileImage);
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

                    if (currentId.equals(posts.getUserId())){

                        holder.settingPost.setOnClickListener(v -> {

                            String edit = holder.itemView.getResources().getString(R.string.edit_post);
                            String delete = holder.itemView.getResources().getString(R.string.delete_post);
                            String delete2 = holder.itemView.getResources().getString(R.string.delete);
                            String sure = holder.itemView.getResources().getString(R.string.sure_delete_post);
                            String delete3 = holder.itemView.getResources().getString(R.string.delete_post_toast);
                            String wait = holder.itemView.getResources().getString(R.string.please_wait);

                            String[] options = {edit, delete};

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (which == 0){
                                        Intent intent = new Intent(context, EditPostActivity.class);
                                        intent.putExtra("postDescription", posts.getPostDescription());
                                        intent.putExtra("postId", posts.getPostId());
                                        intent.putExtra("imagePost", posts.getImagePost());
                                        context.startActivity(intent);
                                    }

                                    if (which == 1) {
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                        builder1.setTitle(delete);

                                        builder1.setMessage(sure);

                                        builder1.setPositiveButton(delete2, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                progressDialog.setTitle(delete);
                                                progressDialog.setMessage(wait);
                                                progressDialog.setCanceledOnTouchOutside(false);
                                                progressDialog.show();

                                                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts");
                                                DatabaseReference comment = FirebaseDatabase.getInstance().getReference("Comments");
                                                DatabaseReference like = FirebaseDatabase.getInstance().getReference("Likes");
                                                comment.child(posts.getPostId()).removeValue();
                                                like.child(posts.getPostId()).removeValue();
                                                postRef.child(posts.getPostId()).removeValue();
                                                progressDialog.dismiss();
                                                Toast.makeText(context, delete3, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        builder1.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });

                                        AlertDialog alertDialog = builder1.create();
                                        alertDialog.show();

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
                    }
                    else {

                        holder.settingPost.setOnClickListener(v -> {

                            String view = holder.itemView.getResources().getString(R.string.view_profile);
                            String report = holder.itemView.getResources().getString(R.string.report_user);
                            String report2 = holder.itemView.getResources().getString(R.string.report_post);
                            String why = holder.itemView.getResources().getString(R.string.why_report_post);
                            String save = holder.itemView.getResources().getString(R.string.save_report);
                            String wait = holder.itemView.getResources().getString(R.string.please_wait);
                            String thanks = holder.itemView.getResources().getString(R.string.thanks_report);
                            String require = holder.itemView.getResources().getString(R.string.required);

                            String[] options = {view, report};

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == 0){
                                        Intent intent = new Intent(context, ProfilePostActivity.class);
                                        intent.putExtra("userId", posts.getUserId());
                                        context.startActivity(intent);
                                    }

                                    if (which == 1) {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                        builder.setTitle(report2);

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

                                                    DatabaseReference databaseReferenceReport = FirebaseDatabase.getInstance().getReference().child("Report Posts");
                                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                                    FirebaseUser user1 = auth.getCurrentUser();
                                                    String currentId =  user1.getUid();

                                                    HashMap<String, Object> hashMap = new HashMap<>();

                                                    hashMap.put("reportId", reportId);
                                                    hashMap.put("FromUserId", currentId);
                                                    hashMap.put("ToUserId", posts.getUserId());
                                                    hashMap.put("postId", posts.getPostId());
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
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.datePost.setText(posts.getDatePost());
        holder.timePost.setText(posts.getTimePost());
        holder.postDescription.setText(posts.getPostDescription());

        if (posts.getCommentsNum().equals("0")){
            holder.imageComment.setVisibility(View.GONE);
            holder.commentNum.setVisibility(View.GONE);
        }
        else {
            holder.imageComment.setVisibility(View.VISIBLE);
            holder.commentNum.setVisibility(View.VISIBLE);
            holder.commentNum.setText(posts.getCommentsNum() + "");
        }

        if (posts.getImagePost().equals("noImage")){

            holder.imagePost.setVisibility(View.GONE);
        }

        else {

            try {

                Picasso.get().load(posts.getImagePost()).into(holder.imagePost);

                holder.imagePost.setVisibility(View.VISIBLE);

            }
            catch (Exception e){
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }



    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    class myHolder extends RecyclerView.ViewHolder {

        TextView tvUsername;
        TextView tvCareer;
        TextView datePost;
        TextView commentNum;
        TextView timePost;
        TextView postDescription;
        CircleImageView ciProfileImage;
        ImageView imagePost;
        ImageView imageComment;
        TextView lineHome;
        TextView btnComment;

        ImageButton settingPost;

        ImageButton btnLike;

        TextView likeCountTv;
        ImageView likeImage;

        int likeCount;
        DatabaseReference likeRef;

        public myHolder(@NonNull View itemView) {

            super(itemView);

            tvUsername = itemView.findViewById(R.id.username_post);
            tvCareer = itemView.findViewById(R.id.career_post);
            ciProfileImage = itemView.findViewById(R.id.image_profile_post);
            postDescription = itemView.findViewById(R.id.tv_post_ed_desc);
            imagePost = itemView.findViewById(R.id.image_view_post);
            commentNum = itemView.findViewById(R.id.count_comment);

            lineHome = itemView.findViewById(R.id.line_home);

            settingPost = itemView.findViewById(R.id.btn_more_setting_post);

            imageComment = itemView.findViewById(R.id.comment_image);

            btnComment = itemView.findViewById(R.id.comment_btn);
            datePost = itemView.findViewById(R.id.date_post);
            timePost = itemView.findViewById(R.id.time_post);
            btnLike = itemView.findViewById(R.id.like_btn);
            likeCountTv = itemView.findViewById(R.id.count_like);
            likeImage = itemView.findViewById(R.id.like_image);

            btnComment.setOnClickListener(v -> {
                Intent intent = new Intent(context, CommentsActivity.class);
                int i = getAdapterPosition();
                intent.putExtra("postId", postsList.get(i).getPostId());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

            imagePost.setOnClickListener(v -> {
                Intent intent = new Intent(context, ImagePostActivity.class);
                int i = getAdapterPosition();
                intent.putExtra("imagePost", postsList.get(i).getImagePost());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

        }

        public void setLikeButton(final String postId) {

            likeRef = FirebaseDatabase.getInstance().getReference("Likes");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String currentId = user.getUid();
            String likes = "";

            likeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(postId).hasChild(currentId)){
                        likeCount = (int) snapshot.child(postId).getChildrenCount();
                        btnLike.setImageResource(R.drawable.ic_like);
                        likeCountTv.setText(likeCount+likes);
                    }
                    else {
                        likeCount = (int) snapshot.child(postId).getChildrenCount();
                        btnLike.setImageResource(R.drawable.ic_like_dislike);
                        likeCountTv.setText(likeCount+likes);
                    }
                    if (snapshot.child(postId).exists()){
                        likeCountTv.setVisibility(View.VISIBLE);
                        likeImage.setVisibility(View.VISIBLE);
                    }
                    else {
                        likeCountTv.setVisibility(View.GONE);
                        likeImage.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
