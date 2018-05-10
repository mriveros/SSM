package com.stp.ssm.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import com.stp.ssm.AlertProviderActivity;
import com.stp.ssm.Evt.ProviderEnbledEvt;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.SessionData;
import de.greenrobot.event.EventBus;


public class LocationProviderBroadcast extends BroadcastReceiver {

    private EventBus eventBus;

    @Override
    public void onReceive(Context context, Intent intent) {
        eventBus = EventBus.getDefault();
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            SessionData sessionData = SessionData.getInstance(context);
            if(sessionData.isMonitorServiceEnable()){
                if(!CellUtils.checkLocationGPS(context)){
                    context.startActivity(new Intent(context,AlertProviderActivity.class).putExtra("provider", LocationManager.GPS_PROVIDER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }else if(!CellUtils.checkNetworkConnect(context)){
                    context.startActivity(new Intent(context,AlertProviderActivity.class).putExtra("provider",LocationManager.NETWORK_PROVIDER).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }else {
                    eventBus.post(new ProviderEnbledEvt());
                }
            }
        }
    }
}
