package com.stp.ssm.View;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.stp.ssm.Interfaces.OnDataSelectListener;

import java.util.Calendar;

import static android.app.DatePickerDialog.OnDateSetListener;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

@SuppressLint("ValidFragment")
public class DialogDatePicker extends DialogFragment implements OnDateSetListener {

    private OnDataSelectListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = getInstance();
        int year = c.get(YEAR);
        int month = c.get(MONTH);
        int day = c.get(DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void setOnDataSelectListener(OnDataSelectListener onDataSelectListener) {
        this.listener = onDataSelectListener;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String fecha = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        listener.OnDataSelect(fecha);
    }
}
