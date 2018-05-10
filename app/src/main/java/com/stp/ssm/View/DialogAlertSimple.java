package com.stp.ssm.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.stp.ssm.R;

import static android.content.DialogInterface.OnClickListener;
import static android.support.v7.app.AlertDialog.Builder;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.lbl_aceptar;


@SuppressLint("ValidFragment")
public class DialogAlertSimple extends DialogFragment {

    private String titulo;
    private String mensaje;


    public DialogAlertSimple(String titulo, String mensaje) {
        this.titulo = titulo;
        this.mensaje = mensaje;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Builder builder = new Builder(getActivity());
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton(getString(lbl_aceptar), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
