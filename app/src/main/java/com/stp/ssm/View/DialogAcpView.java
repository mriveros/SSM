package com.stp.ssm.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.R;

import static android.content.DialogInterface.OnClickListener;
import static android.support.v7.app.AlertDialog.Builder;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.lbl_aceptar;
import static com.stp.ssm.R.string.lbl_cancelar;

@SuppressLint("ValidFragment")
public class DialogAcpView extends DialogFragment {

    private String tag;
    private String titulo;
    private String mensaje;
    private String titleAceptar;
    private String titleCancelar;
    private int tipo = 1;
    private boolean shown = false;

    public final static int SIMPLE = 0;
    public final static int SELECCIONAR = 1;

    private OnClickDialogListener onClickDialogListener;

    public DialogAcpView(String titulo, String mensaje) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tag = "";
    }

    public DialogAcpView(String titulo, String mensaje, int tipo) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tag = "";
        this.tipo = tipo;
    }

    public DialogAcpView(String titulo, String mensaje, String titleAceptar, String titleCancelar) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.titleAceptar = titleAceptar;
        this.titleCancelar = titleCancelar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
        }

        if (titleAceptar == null) {
            this.titleAceptar = getResources().getString(lbl_aceptar);
        }

        if (titleCancelar == null) {
            this.titleCancelar = getResources().getString(lbl_cancelar);
        }
    }

    public void setOnClickDialogListener(OnClickDialogListener onClickDialogListener) {
        this.onClickDialogListener = onClickDialogListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Builder builder = new Builder(getActivity());
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton(titleAceptar, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onClickDialogListener.OnPositiveClick(dialog, tag);
            }
        });
        if (tipo == 1) {
            builder.setNegativeButton(titleCancelar, new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onClickDialogListener.OnNegativeClick(dialog, tag);
                }
            });
        }
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