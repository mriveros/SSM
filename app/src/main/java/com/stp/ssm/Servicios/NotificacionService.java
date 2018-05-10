package com.stp.ssm.Servicios;
//Created by desarrollo on 06/09/16.

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.stp.ssm.GaleriaInternaActivity;
import com.stp.ssm.ListNotificationsActivity;
import com.stp.ssm.R;

import static android.app.Notification.PRIORITY_HIGH;
import static android.app.PendingIntent.getActivity;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.graphics.BitmapFactory.decodeResource;
import static android.graphics.Color.BLUE;
import static android.media.RingtoneManager.TYPE_NOTIFICATION;
import static android.media.RingtoneManager.getDefaultUri;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION.SDK_INT;
import static android.support.v4.app.NotificationCompat.Builder;
import static android.support.v4.app.TaskStackBuilder.create;
import static com.stp.ssm.R.drawable;
import static com.stp.ssm.R.drawable.ic_launcher;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.app_name;
import static com.stp.ssm.R.string.msj_notif;
import static com.stp.ssm.R.string.msj_notif_img;
import static java.lang.System.currentTimeMillis;

public class NotificacionService extends Service {

    private NotificationManager notificacion;
    private Bundle bundle;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bundle = intent.getExtras();
        if (bundle != null && !bundle.isEmpty()) {
            showNotification(bundle.getString("notificacion"));
        } else {
            showImagenNotification();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification(String mensaje) {
        Intent notifyIntent = new Intent(getApplicationContext(), ListNotificationsActivity.class);
        notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        int requestID = (int) currentTimeMillis();

        Builder mbuilder = new Builder(getApplicationContext())
                .setSmallIcon(ic_launcher)
                .setLargeIcon(decodeResource(getApplicationContext().getResources(), ic_launcher))
                .setContentTitle(getString(msj_notif))
                .setTicker(getResources().getString(app_name))
                .setContentText(mensaje)
                .setSound(getDefaultUri(TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(BLUE, 500, 500);


        PendingIntent contentIntent = getActivity(this, requestID, notifyIntent, 0);

        mbuilder.setContentIntent(contentIntent);
        notificacion = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificacion.notify(1, mbuilder.build());
    }

    private void showImagenNotification() {
        Intent notifyIntent = new Intent(getApplicationContext(), GaleriaInternaActivity.class);
        notifyIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
        notifyIntent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        int requestID = (int) currentTimeMillis();

        Builder mbuilder = new Builder(getApplicationContext())
                .setSmallIcon(ic_launcher)
                .setLargeIcon(decodeResource(getApplicationContext().getResources(), ic_launcher))
                .setContentTitle(getString(msj_notif))
                .setTicker(getResources().getString(app_name))
                .setContentText(getString(msj_notif_img))
                .setSound(getDefaultUri(TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setLights(BLUE, 500, 500);

        if (SDK_INT > 19) {
            mbuilder.setPriority(PRIORITY_HIGH);
        }

        TaskStackBuilder stackBuilder = create(this);
        stackBuilder.addParentStack(GaleriaInternaActivity.class);
        stackBuilder.addNextIntent(notifyIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, FLAG_ACTIVITY_NEW_TASK);

        mbuilder.setContentIntent(resultPendingIntent);
        notificacion = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificacion.notify(1, mbuilder.build());
    }

    @Override
    public void onDestroy() {
        notificacion.cancel(1);
        super.onDestroy();
    }
}