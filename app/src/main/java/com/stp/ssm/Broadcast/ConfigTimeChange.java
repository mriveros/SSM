package com.stp.ssm.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.stp.ssm.EVENTOS_CELL;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.databases.BDFuntions;

public class ConfigTimeChange extends BroadcastReceiver {

    private BDFuntions bdFuntions;

    @Override
    public void onReceive(Context context, Intent intent) {
        bdFuntions = new BDFuntions(context);
        final String action = intent.getAction();

        if (action.equals(Intent.ACTION_TIME_CHANGED) || action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
            if(CellUtils.checkAutomaticTime(context)){
                bdFuntions.addEventoCell(EVENTOS_CELL.CHANGE_TIME_ENABLE_AUTO);
            }else{
                bdFuntions.addEventoCell(EVENTOS_CELL.CHANGE_TIME_DISABLE_AUTO);
            }
        }
    }
}
