package com.stp.ssm.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.stp.ssm.Adapters.ListItemAdapter;
import com.stp.ssm.Interfaces.OnDeleteListener;
import com.stp.ssm.R;

import java.util.ArrayList;

import static android.support.v7.app.AlertDialog.Builder;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.files_adj;

@SuppressLint("ValidFragment")
public class DialogLstAdjuntos extends DialogFragment {

    private ArrayList<String> adjuntos;
    public OnDeleteListener onDeleteListener;

    public DialogLstAdjuntos(ArrayList<String> adjuntos) {
        this.adjuntos = adjuntos;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setTitle(getString(files_adj));

        final ListItemAdapter adapter = new ListItemAdapter(getContext(), 0, adjuntos);
        adapter.setOnDeleteListener(new OnDeleteListener() {
            @Override
            public void OnDelete(int position) {
                adapter.remove(adjuntos.get(position));
                DialogLstAdjuntos.this.onDeleteListener.OnDelete(0);
            }
        });

        builder.setAdapter(adapter, null);
        return builder.create();
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }
}
