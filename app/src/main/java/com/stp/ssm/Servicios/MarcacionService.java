package com.stp.ssm.Servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.stp.ssm.Evt.MarcacionEvt;
import com.stp.ssm.databases.BDFuntions;
import de.greenrobot.event.EventBus;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static de.greenrobot.event.EventBus.getDefault;

public class MarcacionService extends Service implements LocationListener {

    private LocationManager locationManager;
    private BDFuntions bdFuntions;
    private EventBus bus;
    private Handler handler;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Location location = locationManager.getLastKnownLocation(GPS_PROVIDER);
                if (location != null) {
                    marcarPosicion(location);
                } else {
                    location = locationManager.getLastKnownLocation(NETWORK_PROVIDER);
                    if (location != null) {
                        marcarPosicion(location);
                    } else {
                        bus.post(new MarcacionEvt(-1));
                        MarcacionService.this.stopSelf();
                    }
                }
            } catch (SecurityException ex) {
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        bus = getDefault();
        bdFuntions = new BDFuntions(getApplicationContext());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        handler = new Handler();
        inicializarGeoposicion();
    }

    private void inicializarGeoposicion() {
        try {
            locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(NETWORK_PROVIDER, 0, 0, this);
            handler.postDelayed(runnable, 60000);
        } catch (SecurityException e) {
        }
    }

    private void marcarPosicion(Location location) {
        int tipo = bdFuntions.isEntradaSalida();
        bdFuntions.guardarMarcacionEntradaSalida(location, tipo);
        bus.post(new MarcacionEvt(tipo));
        MarcacionService.this.stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        marcarPosicion(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        try {
            locationManager.removeUpdates(this);
            handler.removeCallbacks(runnable);
        } catch (SecurityException ex) {
        }
        super.onDestroy();
    }
}
