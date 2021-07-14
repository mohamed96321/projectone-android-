package com.ma.socialapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import static android.app.Activity.RESULT_OK;

public class FragmentReport extends Fragment {


    public FragmentReport(){
        // require empty constructor
    }

    View view;
    private Button btnSend, btnImageReport;
    private EditText edReport;
    private CoordinatorLayout coordinatorLayout;
    private ImageView imageViewReport;

    Uri imageUri;

    private static final int REQUEST_CODE = 101;

    private DatabaseReference reportAppRef;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private StorageReference storageReferenceReportApp;
    private ProgressDialog progressDialog;

    String save, undo, wait, thanks, remove, require;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_report, container, false);

        btnSend = view.findViewById(R.id.send_btn_rp_app);

        edReport = view.findViewById(R.id.report_app);

        btnImageReport = view.findViewById(R.id.btn_report_image_app);

        coordinatorLayout = view.findViewById(R.id.coordinator_frag_report);
        imageViewReport = view.findViewById(R.id.image_report_app);

        progressDialog = new ProgressDialog(getActivity());

        save = getResources().getString(R.string.save_report);
        thanks = getResources().getString(R.string.thanks_report);
        remove = getResources().getString(R.string.remove_report);
        wait = getResources().getString(R.string.please_wait);
        undo = getResources().getString(R.string.undo);
        require = getResources().getString(R.string.required);

        reportAppRef = FirebaseDatabase.getInstance().getReference().child("Reports App");

        storageReferenceReportApp = FirebaseStorage.getInstance().getReference().child("Reports App Images");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        btnImageReport.setOnClickListener(v -> {

            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, REQUEST_CODE);

        });

        btnSend.setOnClickListener(v -> {
            saveDataReport();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            imageViewReport.setImageURI(imageUri);

        }
    }

    private void saveDataReport() {

        String textReport = edReport.getText().toString();

        if (TextUtils.isEmpty(textReport)){
            edReport.setError(require);
        }

        else if (imageUri == null){

            progressDialog.setTitle(save);
            progressDialog.setMessage(wait);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            String reportId = String.valueOf(System.currentTimeMillis());

            String currentId = user.getUid();

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("reportContent", textReport);
            hashMap.put("imageReport", "noImage");
            hashMap.put("reportId", reportId);
            hashMap.put("userId", currentId);


            reportAppRef.child(reportId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {


                    progressDialog.dismiss();

                    Snackbar snackbar = Snackbar.make(coordinatorLayout, thanks, Snackbar.LENGTH_SHORT)
                            .setAction(undo, v1 -> {
                                removeReport(reportId);
                            });

                    snackbar.show();

                    edReport.setText("");

                    imageViewReport.setImageURI(null);
                    imageUri = null;


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else {

            progressDialog.setTitle(save);
            progressDialog.setMessage(wait);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            String reportId = String.valueOf(System.currentTimeMillis());

            String currentId = user.getUid();

            storageReferenceReportApp.child(reportId).putFile(imageUri).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    storageReferenceReportApp.child(reportId).getDownloadUrl().addOnSuccessListener(uri -> {

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("reportContent", textReport);
                        hashMap.put("imageReport", uri.toString());
                        hashMap.put("reportId", reportId);
                        hashMap.put("userId", currentId);

                        reportAppRef.child(reportId).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {


                                progressDialog.dismiss();

                                Snackbar snackbar = Snackbar.make(coordinatorLayout, thanks, Snackbar.LENGTH_SHORT)
                                        .setAction(undo, v1 -> {
                                            removeReport(reportId);
                                        });

                                snackbar.show();

                                edReport.setText("");

                                imageViewReport.setImageURI(null);
                                imageUri = null;


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    });
                }
                else {
                    progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    Toast.makeText(getActivity(), "Error Occurred : " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void removeReport(final String reportId) {

        reportAppRef.child(reportId).removeValue();

        Toast.makeText(getActivity(), remove, Toast.LENGTH_SHORT).show();

    }
}
