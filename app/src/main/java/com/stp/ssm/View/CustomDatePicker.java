package com.stp.ssm.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.lang.reflect.Field;

import static android.util.Log.i;

public class CustomDatePicker extends DatePicker {


    public CustomDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        Field[] datePickerFields = this.getClass().getDeclaredFields();
        for (Field datePickerField : datePickerFields) {
            i("test", datePickerField.getName());
            if (datePickerField.getName().equals("mYearPicker")) {
                datePickerField.setAccessible(true);
                Object yearPicker = new Object();
                try {
                    yearPicker = datePickerField.get(this);
                    ((View) yearPicker).setVisibility(GONE);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
