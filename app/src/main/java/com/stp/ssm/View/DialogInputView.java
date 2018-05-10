package com.stp.ssm.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.stp.ssm.Interfaces.OnClickDialogInputListener;
import com.stp.ssm.R;

import static android.content.DialogInterface.OnClickListener;
import static android.support.v7.app.AlertDialog.Builder;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.edtInputText;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.view_input_dialog;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_title_input_text;
import static com.stp.ssm.R.string.lbl_aceptar;
import static com.stp.ssm.R.string.lbl_cancelar;

@SuppressLint("ValidFragment")
public class DialogInputView extends DialogFragment {

    private String tag;
    private boolean shown = false;
    private OnClickDialogInputListener onClickDialogInputListener;

    public void setOnClickDialogInputListener(OnClickDialogInputListener onClickDialogInputListener) {
        this.onClickDialogInputListener = onClickDialogInputListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(view_input_dialog, null);

        final EditText edtInputText = (EditText) view.findViewById(R.id.edtInputText);

        Builder builder = new Builder(getActivity());
        builder.setTitle(getString(dialog_title_input_text));
        builder.setView(view);
        builder.setPositiveButton(getString(lbl_aceptar), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickDialogInputListener.OnPositiveClick(dialog, tag, edtInputText.getText().toString());
            }
        }).setNegativeButton(getString(lbl_cancelar), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickDialogInputListener.OnNegativeClick(dialog, tag);
            }
        });
        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        this.tag = tag;
        super.show(manager, tag);
        shown = true;
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        shown = false;
        super.onDismiss(dialog);
    }
}
