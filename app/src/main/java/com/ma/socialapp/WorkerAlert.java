package com.ma.socialapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.ma.socialapp.R;

public class WorkerAlert extends AppCompatDialogFragment {

    private Button btnOk;
    private FirebaseAuth auth;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialoge_alert_worker, null);
        builder.setView(view);

        btnOk = view.findViewById(R.id.ok_btn);

        auth = FirebaseAuth.getInstance();

        btnOk.setOnClickListener(v -> {
            SenderWorkerToLoginActivity();
        });

        return builder.create();
    }

    private void SenderWorkerToLoginActivity() {
        auth.signOut();
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }
}
