package com.stp.ssm.Interfaces;
//Created by desarrollo on 01/04/16.

import android.content.DialogInterface;

public interface OnClickDialogListener {

    public void OnPositiveClick(DialogInterface dialog, String tag);


    public void OnNegativeClick(DialogInterface dialog, String tag);
}
