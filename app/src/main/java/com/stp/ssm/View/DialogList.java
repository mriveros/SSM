package com.stp.ssm.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;

import com.stp.ssm.Interfaces.OnItemListSelectListener;

import static android.content.DialogInterface.OnClickListener;
import static android.support.v7.app.AlertDialog.Builder;

@SuppressLint("ValidFragment")
public class DialogList extends DialogFragment {

    private String tag;
    private String titulo;
    private String[] array;
    private OnItemListSelectListener onItemListSelectListener;

    public DialogList(String titulo, String[] array) {
        this.titulo = titulo;
        this.array = array;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }
    }

    public void setOnItemListSelectListener(OnItemListSelectListener onItemListSelectListener) {
        this.onItemListSelectListener = onItemListSelectListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setTitle(titulo);
        builder.setItems(array, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onItemListSelectListener.OnItemListSelect(i);
            }
        });
        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        this.tag = tag;
        super.show(manager, tag);
    }
}
