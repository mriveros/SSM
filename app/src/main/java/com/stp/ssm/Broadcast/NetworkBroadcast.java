package com.stp.ssm.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.stp.ssm.Servicios.SendDataService2;
import com.stp.ssm.Util.CellUtils;

public class NetworkBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*valida que haya conexion a Internet y que el servicio de envio no este en ejecucio*/
        if(CellUtils.hasConnectionInternet(context) && !CellUtils.isMyServiceRunning(SendDataService2.class,context)){
            context.startService(new Intent(context, SendDataService2.class));
        }
    }
}
