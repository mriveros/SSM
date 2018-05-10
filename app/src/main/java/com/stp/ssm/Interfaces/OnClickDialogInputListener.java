package com.stp.ssm.Interfaces;

import android.content.DialogInterface;

public interface OnClickDialogInputListener {

    public void OnPositiveClick(DialogInterface dialog, String tag, String inputText);

    public void OnNegativeClick(DialogInterface dialog, String tag);
}
