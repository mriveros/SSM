package com.stp.ssm.Servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.stp.ssm.Evt.NuevaDistanciaEvt;
import com.stp.ssm.Evt.NuevaHoraEvt;
import com.stp.ssm.Model.Coordenadas;
import com.stp.ssm.Model.TipoUbicacion;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.FechaUtil;
import com.stp.ssm.Util.SessionData;
import com.stp.ssm.databases.BDFuntions;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static com.stp.ssm.Model.TipoUbicacion.FIN_RECORRIDO;
import static com.stp.ssm.Model.TipoUbicacion.INICIO_RECORRIDO;
import static com.stp.ssm.Model.TipoUbicacion.UBICACION_PERIODICA;
import static com.stp.ssm.Util.CellUtils.hasConnectionInternet;
import static com.stp.ssm.Util.CellUtils.isMyServiceRunning;
import static com.stp.ssm.Util.FechaUtil.SegundoToHora;
import static com.stp.ssm.Util.FechaUtil.milisegunToDate;
import static com.stp.ssm.Util.SessionData.getInstance;
import static de.greenrobot.event.EventBus.getDefault;
import static java.lang.System.currentTimeMillis;

public class UbicacionService extends Service {

    private LocationManager locationManager;
    private SessionData sessionData;
    private Coordenadas coordenadas;
    private BDFuntions bdFuntions;
    private Timer mTimer = null;
    private Timer cronometro = null;
    private ListenerLocation listenerGPS;
    private ListenerLocation listenerNetwork;
    private EventBus bus;
    private Location lastLocation;
    private long segundos = 0;
    private float distancia = 0;
    private final int TIME_SEND_POS = 180000;
    private final int TIME_CRONOMETRO = 1000;

    @Override
    public void onCreate() {
        super.onCreate();

        bus = getDefault();
        sessionData = getInstance(getApplicationContext());
        bdFuntions = new BDFuntions(getApplicationContext());

        sessionData.setLastRecorrido(bdFuntions.guardarInicioRecorrido());
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listenerGPS = new ListenerLocation();
        listenerNetwork = new ListenerLocation();

        segundos = sessionData.getTimeGuardado();
        distancia = sessionData.getDistanciaRecorrida();
        sessionData.setDistanciaRecorrida(distancia);

        inicializarCronometro();
        inicializarGeoposicion(0);
    }

    private void inicializarCronometro() {
        cronometro = new Timer();
        cronometro.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                bus.post(new NuevaHoraEvt(SegundoToHora(segundos)));
                sessionData.setTimeSave(segundos);
                segundos++;
            }
        }, 0, TIME_CRONOMETRO);
    }

    private void finalizarCronometro() {
        sessionData.setTimeSave(0);
        bus.post(new NuevaHoraEvt(SegundoToHora(0)));
        cronometro.cancel();
        bdFuntions.guardarFinRecorrido(sessionData.getLastRercorrido(), segundos);
    }

    private void inicializarGeoposicion(int refresh) {
        try {
            locationManager.requestLocationUpdates(GPS_PROVIDER, refresh, 0, listenerGPS);
            locationManager.requestLocationUpdates(NETWORK_PROVIDER, refresh, 0, listenerNetwork);

            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!sessionData.isRecorridoStart()) {
                        posicionInicioRecorrido();
                    } else {
                        capturarPosicion(UBICACION_PERIODICA);
                    }
                    enviarDatos();
                }
            }, 0, TIME_SEND_POS);

        } catch (SecurityException e) {
        }
    }


    private void finalizacionGeoposicion() {
        try {
            locationManager.removeUpdates(listenerGPS);
            locationManager.removeUpdates(listenerNetwork);
        } catch (SecurityException e) {
        }
    }

    private void posicionInicioRecorrido() {
        sessionData.iniciarRecorrido();
        try {
            Location location;
            int cont = 0;
            String provider = GPS_PROVIDER;
            long time1 = currentTimeMillis();
            long time2;
            do {
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    if (location.getAccuracy() < 30) {
                        break;
                    }
                }
                cont++;
                if (cont == 2) {
                    provider = NETWORK_PROVIDER;
                }

                time2 = currentTimeMillis();
                while ((time2 - time1) < 10000) {
                    time2 = currentTimeMillis();
                }
            } while (cont <= 5);
            lastLocation = location;
            guardarUbicacion(location, INICIO_RECORRIDO);
        } catch (SecurityException e) {
        }
    }


    private void capturarPosicion(TipoUbicacion tipo) {
        Location locationgps;
        Location locationnetwork = null;
        try {
            locationgps = locationManager.getLastKnownLocation(GPS_PROVIDER);
            locationnetwork = locationManager.getLastKnownLocation(NETWORK_PROVIDER);
            if (locationgps != null && locationnetwork != null) {
                if (locationgps.getAccuracy() <= locationnetwork.getAccuracy()) {
                    sacarDistancia(locationgps);
                    guardarUbicacion(locationgps, tipo);
                } else {
                    sacarDistancia(locationnetwork);
                    guardarUbicacion(locationnetwork, tipo);
                }
            } else if (locationgps != null) {
                sacarDistancia(locationgps);
                guardarUbicacion(locationgps, tipo);
            } else if (locationnetwork != null) {
                sacarDistancia(locationnetwork);
                guardarUbicacion(locationnetwork, tipo);
            } else {
            }
        } catch (SecurityException e) {
        }
    }

    private void sacarDistancia(Location mylocation) {
        try {
            distancia = distancia + mylocation.distanceTo(lastLocation);
            lastLocation = mylocation;
            sessionData.setDistanciaRecorrida(distancia);
            bus.post(new NuevaDistanciaEvt(distancia));
        } catch (NullPointerException ex) {
        }
    }

    private void guardarUbicacion(Location location, TipoUbicacion tipo) {
        coordenadas = new Coordenadas(Double.toString(location.getLongitude()),
                Double.toString(location.getLatitude()),
                location.getAccuracy(),
                location.getAltitude(),
                location.getProvider(),
                milisegunToDate(location.getTime()));
        bdFuntions.guardarUbicacion(coordenadas, tipo);
        /*if(location!=null){

        }else{
            bus.post(new ErrorLocationEvt());
        }*/
    }


    private void finalizarRecorrido() {
        capturarPosicion(FIN_RECORRIDO);
        finalizacionGeoposicion();
        mTimer.cancel();
        bus.post(new NuevaDistanciaEvt(0));
        enviarDatos();
    }


    private void enviarDatos() {
        if (hasConnectionInternet(getApplicationContext()) && !isMyServiceRunning(SendDataService2.class, getApplicationContext())) {
            startService(new Intent(getApplicationContext(), SendDataService2.class));
        }
    }


    @Override
    public void onDestroy() {
        finalizarRecorrido();
        finalizarCronometro();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class ListenerLocation implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
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
    }
}