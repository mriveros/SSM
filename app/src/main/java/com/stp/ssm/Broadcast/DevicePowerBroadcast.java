package com.stp.ssm.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.stp.ssm.EVENTOS_CELL;
import com.stp.ssm.databases.BDFuntions;

import static android.content.Intent.ACTION_BOOT_COMPLETED;
import static android.content.Intent.ACTION_SHUTDOWN;
import static com.stp.ssm.EVENTOS_CELL.DEVICE_OFF;
import static com.stp.ssm.EVENTOS_CELL.DEVICE_ON;

public class DevicePowerBroadcast extends BroadcastReceiver {

    private BDFuntions bdFuntions;

    @Override
    public void onReceive(Context context, Intent intent) {
        bdFuntions = new BDFuntions(context);
        final String action = intent.getAction();

        if (action.equals(ACTION_BOOT_COMPLETED)) {
            bdFuntions.addEventoCell(DEVICE_ON);

        } else if (action.equals(ACTION_SHUTDOWN)) {
            bdFuntions.addEventoCell(DEVICE_OFF);
        }
    }
}
