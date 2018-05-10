package com.stp.ssm.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.stp.ssm.Interfaces.OnDataSelectListener;

import java.util.Calendar;

import static android.app.TimePickerDialog.OnTimeSetListener;
import static android.text.format.DateFormat.is24HourFormat;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.getInstance;

@SuppressLint("ValidFragment")
public class DialogTimerPicker extends DialogFragment implements OnTimeSetListener {

    private OnDataSelectListener listener;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = getInstance();
        int hour = c.get(HOUR_OF_DAY);
        int minute = c.get(MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, is24HourFormat(getActivity()));
    }

    public void setOnDataSelectListener(OnDataSelectListener onDataSelectListener) {
        this.listener = onDataSelectListener;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String hora = hourOfDay + ":" + minute;
        listener.OnDataSelect(hora);
    }
}
