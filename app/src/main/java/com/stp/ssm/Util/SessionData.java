package com.stp.ssm.Util;
//Created by desarrollo on 15/03/16.

import android.content.Context;
import android.content.SharedPreferences;

import com.stp.ssm.Evt.LoginResult;

import static android.content.Context.MODE_PRIVATE;
import static android.content.SharedPreferences.Editor;
import static com.stp.ssm.Util.Encriptacion.cifrarAes;
import static com.stp.ssm.Util.FechaUtil.getFechaDia;

public class SessionData {

    private SharedPreferences preferences;
    private SharedPreferences credenciales;
    private static SessionData INSTANCE;

    private final String NAME_FILE_PREFS = "stp_ssm";
    private final String NAME_FILE_CREDENT = "credenciales";
    private final String KEY_USUARIO = "usuario";
    private final String KEY_DEPT = "departamento";
    private final String KEY_DIST = "distrito";
    private final String KEY_RECORRIDO = "recorrido";
    private final String KEY_RECORRIDO_DATE_INI = "recorrido_date_ini";
    private final String KEY_MONT_SERV = "monitorservice";
    private final String KEY_SIM = "nuevoSim";
    private final String KEY_SESSION = "session";
    private final String KEY_TIEMPO_RECORRIDO = "time_recorrido";
    private final String KEY_DISTANCIA_RECORRIDO = "distancia_recorrido";
    private final String KEY_LAST_IDVISITA = "last_idvisita";
    private final String KEY_LAST_TIME_VISITA = "last_time_visita";
    private final String KEY_FIRST_RUNNING = "fist_running";
    private final String KEY_LAST_RECORRIDO = "last_recorrido";
    private final String KEY_DEVICE_REGISTER = "device_register";
    private final String KEY_MARCACION = "marcacion";
    private final String KEY_FIREBASE_PUSH_TOKEN = "firebase_push_token";
    private final String KEY_UPDATE = "update";
    private final String KEY_DEPT_POSITION = "dept_position";
    private final String KEY_DIST_POSITION = "dist_position";
    private final String KEY_LOCAL_POSITION = "local_position";
    private final String KEY_MOTIVO_POSITION = "motivo_position";
    private final String KEY_OBSERVACION = "observacion";
    private final String KEY_NIVEL = "nivel";
    private final String KEY_LAST_USER = "last_user";
    private final String KEY_LAST_PASS = "last_pass";
    private final String KEY_TOKEN = "token";

    private SessionData(Context context) {
        preferences = context.getSharedPreferences(NAME_FILE_PREFS, MODE_PRIVATE);
        credenciales = context.getSharedPreferences(NAME_FILE_CREDENT, MODE_PRIVATE);
    }

    private synchronized static void createInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SessionData(context);
        }
    }


    public static SessionData getInstance(Context context) {
        if (INSTANCE == null) createInstance(context);
        return INSTANCE;
    }


    public void salvarUsuarioOnline(LoginResult loginResult) {
        Editor editor = preferences.edit();
        editor.putString(KEY_USUARIO, loginResult.getUsuario());
        editor.putString(KEY_DEPT, loginResult.getIddepartamento());
        editor.putString(KEY_DIST, loginResult.getIddistrito());
        //editor.putInt(KEY_SESSION, 1);
        editor.commit();
    }


    public void salvarCredenciales(String usuario, String password) {
        Editor editor = credenciales.edit();
        editor.putString(KEY_LAST_USER, cifrarAes(usuario));
        editor.putString(KEY_LAST_PASS, cifrarAes(password));
        editor.commit();
    }


    public void salvarUsuarioOffline(String user) {
        Editor editor = preferences.edit();
        editor.putString(KEY_USUARIO, user);
        editor.putInt(KEY_SESSION, 1);
        editor.commit();
    }

    public void cerrarSession() {
        Editor editor = preferences.edit();
        editor.putInt(KEY_SESSION, 0);
        editor.commit();
    }

    public void iniciarSession() {
        Editor editor = preferences.edit();
        editor.putInt(KEY_SESSION, 1);
        editor.commit();
    }


    public boolean checkLastPassword(String user, String pass) {

        String usuario = cifrarAes(user);
        String password = cifrarAes(pass);

        if (credenciales.getString(KEY_LAST_USER, "").equals(usuario) &&
                credenciales.getString(KEY_LAST_PASS, "").equals(password)) {
            return true;
        } else {
            return false;
        }
    }


    public boolean isRecorridoStart() {
        if (preferences.getInt(KEY_RECORRIDO, 0) == 0) {
            return false;
        } else {
            return true;
        }
    }


    public boolean isSessionStart() {
        return preferences.getInt(KEY_SESSION, 0) == 1;
    }

    public String getUsuario() {
        return preferences.getString(KEY_USUARIO, "");
    }


    public void iniciarRecorrido() {
        Editor editor = preferences.edit();
        editor.putInt(KEY_RECORRIDO, 1);
        editor.putString(KEY_RECORRIDO_DATE_INI, getFechaDia());
        editor.commit();
    }

    public void finalizarRecorrido() {
        Editor editor = preferences.edit();
        editor.putInt(KEY_RECORRIDO, 0);
        editor.commit();
    }


    public void enabledlocationMonitor() {
        Editor editor = preferences.edit();
        editor.putBoolean(KEY_MONT_SERV, true);
        editor.commit();
    }


    public void disablelocationMonitor() {
        Editor editor = preferences.edit();
        editor.putBoolean(KEY_MONT_SERV, false);
        editor.commit();
    }


    public boolean isMonitorServiceEnable() {
        return preferences.getBoolean(KEY_MONT_SERV, false);
    }


    public void clearSession() {
        Editor editor = preferences.edit();
        editor.remove(KEY_USUARIO);
        editor.remove(KEY_MONT_SERV);
        editor.remove(KEY_SESSION);
        editor.commit();
    }


    public void nuevoSim(boolean valor) {
        Editor editor = preferences.edit();
        editor.putBoolean(KEY_SIM, valor);
        editor.commit();
    }


    public boolean isNuevoSim() {
        return preferences.getBoolean(KEY_SIM, false);
    }


    public void setTimeSave(long time) {
        Editor editor = preferences.edit();
        editor.putLong(KEY_TIEMPO_RECORRIDO, time);
        editor.commit();
    }


    public long getTimeGuardado() {
        return preferences.getLong(KEY_TIEMPO_RECORRIDO, 0);
    }


    public void setDistanciaRecorrida(float distancia) {
        Editor editor = preferences.edit();
        editor.putFloat(KEY_DISTANCIA_RECORRIDO, distancia);
        editor.commit();
    }


    public float getDistanciaRecorrida() {
        return preferences.getFloat(KEY_DISTANCIA_RECORRIDO, 0);
    }


    public void setLastIdVisita(long idvisita) {
        Editor editor = preferences.edit();
        editor.putLong(KEY_LAST_IDVISITA, idvisita);
        editor.commit();
    }


    public long getLastIdVisita() {
        return preferences.getLong(KEY_LAST_IDVISITA, 0);
    }


    public void setLastTimeVisita(long time) {
        Editor editor = preferences.edit();
        editor.putLong(KEY_LAST_TIME_VISITA, time);
        editor.commit();
    }


    public long getLastTimeVisita() {
        return preferences.getLong(KEY_LAST_TIME_VISITA, 0);
    }


    public boolean isfirsRunning() {
        if (preferences.getBoolean(KEY_FIRST_RUNNING, true)) {
            return true;
        } else {
            return false;
        }
    }


    public void changeIsFisrtRun() {
        Editor editor = preferences.edit();
        editor.putBoolean(KEY_FIRST_RUNNING, false);
        editor.commit();
    }


    public void setLastRecorrido(long recorrido) {
        Editor editor = preferences.edit();
        editor.putLong(KEY_LAST_RECORRIDO, recorrido);
        editor.commit();
    }


    public long getLastRercorrido() {
        return preferences.getLong(KEY_LAST_RECORRIDO, 0);
    }


    public void setRegisterDevice() {
        Editor editor = preferences.edit();
        editor.putBoolean(KEY_DEVICE_REGISTER, true);
        editor.commit();
    }


    public int getEstadoMarcacion() {
        return preferences.getInt(KEY_MARCACION, 0);
    }

    public void setEntrada() {
        Editor editor = preferences.edit();
        editor.putInt(KEY_MARCACION, 1);
        editor.commit();
    }

    public void setSalida() {
        Editor editor = preferences.edit();
        editor.putInt(KEY_MARCACION, 0);
        editor.commit();
    }

    public void saveTokenFirebase(String token) {
        Editor editor = preferences.edit();
        editor.putString(KEY_FIREBASE_PUSH_TOKEN, token);
        editor.commit();
    }


    public String getTokenFirebase() {
        return preferences.getString(KEY_FIREBASE_PUSH_TOKEN, "");
    }

    public void checkUpdate() {
        if (preferences.getInt(KEY_UPDATE, 0) == 0) {
            clearSession();
            Editor editor = preferences.edit();
            editor.putInt(KEY_UPDATE, 1);
            editor.commit();
        }
    }

    public void setDptoPosicion(int position) {
        Editor editor = preferences.edit();
        editor.putInt(KEY_DEPT_POSITION, position);
        editor.commit();
    }

    public int getDptoPosicion() {
        return preferences.getInt(KEY_DEPT_POSITION, 0);
    }

    public void setDistPosicion(int position) {
        Editor editor = preferences.edit();
        editor.putInt(KEY_DIST_POSITION, position);
        editor.commit();
    }

    public int getDistPosicion() {
        return preferences.getInt(KEY_DIST_POSITION, 0);
    }

    public void setLocalidadPosicion(int position) {
        Editor editor = preferences.edit();
        editor.putInt(KEY_LOCAL_POSITION, position);
        editor.commit();
    }

    public int getLocalidadPosicion() {
        return preferences.getInt(KEY_LOCAL_POSITION, 0);
    }

    public void setMotivoPosicion(int position) {
        Editor editor = preferences.edit();
        editor.putInt(KEY_MOTIVO_POSITION, position);
        editor.commit();
    }

    public int getMotivoPosicion() {
        return preferences.getInt(KEY_MOTIVO_POSITION, 0);
    }

    public void setObservacion(String observacion) {
        Editor editor = preferences.edit();
        editor.putString(KEY_OBSERVACION, observacion);
        editor.commit();
    }

    public String getObservacion() {
        return preferences.getString(KEY_OBSERVACION, "");
    }

    public void setNivel(int nivel) {
        Editor editor = preferences.edit();
        editor.putInt(KEY_NIVEL, nivel);
        editor.commit();
    }

    public int getNivel() {
        return preferences.getInt(KEY_NIVEL, 0);
    }

    public void setFistLoad(String claseName) {
        Editor editor = preferences.edit();
        editor.putInt(claseName, 1);
        editor.commit();
    }

    public boolean isFistLoad(String claseName) {
        return (preferences.getInt(claseName, 0) == 0);
    }

    public void setToken(String token) {
        Editor editor = preferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, "");
    }
}