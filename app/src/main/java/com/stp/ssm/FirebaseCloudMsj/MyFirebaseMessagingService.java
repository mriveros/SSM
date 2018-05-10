package com.stp.ssm.FirebaseCloudMsj;

import android.app.NotificationManager;;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.stp.ssm.Model.Notificacion;
import com.stp.ssm.Servicios.NotificacionService;
import com.stp.ssm.databases.BDFuntions;

import static android.util.Log.i;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private BDFuntions bdFuntions;

    @Override
    public void onCreate() {
        super.onCreate();
        bdFuntions = new BDFuntions(getApplicationContext());
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        i("FROM", "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            i("MEssage", "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            i("Notificion", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        bdFuntions.guardarNotificacion(new Notificacion(remoteMessage.getFrom(),
                remoteMessage.getMessageId(),
                remoteMessage.getNotification().getBody(),
                false));
        startService(new Intent(getApplicationContext(), NotificacionService.class).
                putExtra("notificacion", remoteMessage.getNotification().getBody()));
    }
}
