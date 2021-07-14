package com.ma.socialapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

public class FragmentReview extends Fragment {


    public FragmentReview(){
        // require empty constructor
    }

    View view;

    private Button btnSend;
    private RatingBar ratingBarApp;
    private EditText edTextReview;
    private CoordinatorLayout coordinatorLayout;

    private DatabaseReference reviewAppRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ProgressDialog progressDialog;

    String save, undo, wait, thanks, remove, require, rat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_review, container, false);

        btnSend = view.findViewById(R.id.send_btn_rv);

        ratingBarApp = view.findViewById(R.id.rating_app);

        edTextReview = view.findViewById(R.id.review_app);

        coordinatorLayout = view.findViewById(R.id.coordinator_review);

        save = getResources().getString(R.string.save_review);
        thanks = getResources().getString(R.string.thanks_review);
        remove = getResources().getString(R.string.remove_review);
        wait = getResources().getString(R.string.please_wait);
        undo = getResources().getString(R.string.undo);
        require = getResources().getString(R.string.required);
        rat = getResources().getString(R.string.rating_must);

        progressDialog = new ProgressDialog(getActivity());

        reviewAppRef = FirebaseDatabase.getInstance().getReference().child("Reviews");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        ratingBarApp.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                saveDataReviewApp(rating);
            }
        });

        return view;
    }

    private void saveDataReviewApp(float rating) {
        btnSend.setOnClickListener(v -> {
            String review = edTextReview.getText().toString();

            if (TextUtils.isEmpty(review)) {
                edTextReview.setError(require);
            }

            else if (rating == 0) {
                Toast.makeText(getActivity(), rat, Toast.LENGTH_LONG).show();
            }

            else {

                progressDialog.setTitle(save);
                progressDialog.setMessage(wait);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                String currentId = user.getUid();

                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("reviewContent", review);
                hashMap.put("userId", currentId);
                hashMap.put("rating", rating);


                reviewAppRef.child(currentId).updateChildren(hashMap).addOnSuccessListener(aVoid -> {

                    progressDialog.dismiss();


                    Snackbar snackbar = Snackbar.make(coordinatorLayout, thanks, Snackbar.LENGTH_LONG)
                            .setAction(undo, v1 -> {
                                removeOpinion(currentId);
                            });

                    snackbar.show();


                    edTextReview.setText("");

                    ratingBarApp.setRating((float) 0.5);


                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void removeOpinion(final String currentId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Reviews App");

        reference.child(currentId).removeValue();

        Toast.makeText(getActivity(), remove, Toast.LENGTH_LONG).show();
    }
}
