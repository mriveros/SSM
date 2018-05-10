package com.stp.ssm.Servicios;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.stp.ssm.Evt.SendDataFinish;
import com.stp.ssm.Evt.SendDataSuccessful;
import com.stp.ssm.Evt.TokenExpirado;
import com.stp.ssm.Model.SendData;
import com.stp.ssm.http.ApiResponse;
import com.stp.ssm.http.HttpCliente;
import com.stp.ssm.Util.SessionData;
import com.stp.ssm.databases.BDFuntions;
import com.stp.ssm.databases.DataBaseMaestro;
import com.stp.ssm.http.HttpStatus;
import com.stp.ssm.http.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

import static android.util.Log.e;
import static android.util.Log.i;
import static com.stp.ssm.Model.SendData.TIPO;
import static com.stp.ssm.Model.SendData.TIPO.CAPTURAS;
import static com.stp.ssm.Model.SendData.TIPO.EVENTO_CAPTURAS;
import static com.stp.ssm.Util.SessionData.getInstance;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_ADJ;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_CAPTURAS;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_CAPTURA_EVENTOS;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_CORRECCIONES;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_EVENTOS;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_EVTCELL;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_OPERADORA;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_RECORRIDO;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_UBICACIONES;
import static com.stp.ssm.http.HttpStatus.OK;
import static com.stp.ssm.http.HttpStatus.UNAUTHORIZED;
import static com.stp.ssm.http.URLs.URL_ADJUNTOS;
import static com.stp.ssm.http.URLs.URL_CAPTURA_INVALIDA;
import static com.stp.ssm.http.URLs.URL_CHECK_IMG_FILE;
import static com.stp.ssm.http.URLs.URL_EVENTO;
import static com.stp.ssm.http.URLs.URL_EVENTO_CAPTURAS;
import static com.stp.ssm.http.URLs.URL_EVENTO_CAPTURA_INVALIDA;
import static com.stp.ssm.http.URLs.URL_EVENTO_DEVICE;
import static com.stp.ssm.http.URLs.URL_INDENTIFICACIONES;
import static com.stp.ssm.http.URLs.URL_RECEPCION;
import static com.stp.ssm.http.URLs.URL_RECEPCION_IMG;
import static com.stp.ssm.http.URLs.URL_RECORRIDO;
import static com.stp.ssm.http.URLs.URL_RELEVAMIENTO_CORRECCION;
import static com.stp.ssm.http.URLs.URL_TELEFONIA;
import static com.stp.ssm.http.URLs.URL_UBICACION;
import static de.greenrobot.event.EventBus.getDefault;
import static java.lang.Long.parseLong;

public class SendDataService2 extends IntentService {

    private BDFuntions dbFuntions;
    private SessionData sessionData;
    private EventBus bus;
    private HttpCliente httpCliente;
    private ArrayList<SendData> sendDatas;
    private String token;
    private boolean tokenValid = true;

    private String[] urls = new String[]{URL_RECEPCION,                   //0
            URL_RECEPCION_IMG,
            URL_ADJUNTOS,
            URL_UBICACION,
            URL_TELEFONIA,
            URL_RECORRIDO,
            URL_EVENTO,
            URL_EVENTO_CAPTURAS,
            URL_EVENTO_DEVICE,
            URL_INDENTIFICACIONES,
            URL_CHECK_IMG_FILE,
            URL_RELEVAMIENTO_CORRECCION,
            URL_CAPTURA_INVALIDA,
            URL_EVENTO_CAPTURA_INVALIDA};


    public SendDataService2() {
        super("SendDataService2");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        i("SERVICIO", "Inicializado");
        token = getInstance(getApplicationContext()).getToken();
        if (token.equals("")) {
            tokenValid = false;
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        httpCliente = HttpCliente.getInstance();
        bus = getDefault();
        sessionData = getInstance(getApplicationContext());
        dbFuntions = new BDFuntions(getApplicationContext());

        if (tokenValid) {
            enviarVisita();
        }
        if (tokenValid) {
            enviarCapturas();
        }
        if (tokenValid) {
            enviarAdjuntos();
        }
        if (tokenValid) {
            enviarUbicaciones();
        }
        if (tokenValid) {
            enviarTelefonia();
        }
        if (tokenValid) {
            enviarRecorrido();
        }
        if (tokenValid) {
            enviarEvento();
        }
        if (tokenValid) {
            enviarCapturasEventos();
        }
        if (tokenValid) {
            syncCorrecciones();
        }
        if (tokenValid) {
            enviarCapturasInvalidas();
        }
        if (tokenValid) {
            enviarCapturasEvtInvalidas();
        }
    }

    private void enviarVisita() {
        sendDatas = dbFuntions.getArrayPendienteVisita();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("Visita", sendData.getParametros().toString());
            response = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData, token);
            e("respose", response.toString());
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }

    private void enviarCapturas() {
        sendDatas = dbFuntions.getPendienteArrCapturas();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("Captura", sendData.getParametros().toString());
            //result = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData);
            File file = new File(sendData.getParametros().get("archivo"));
            sendData.getParametros().remove("archivo");
            response = httpCliente.httpUpFile(sendData.getParametros(), urls[sendData.getTipo().getCodigo()], file, token);
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }


    private void enviarAdjuntos() {
        sendDatas = dbFuntions.getArrrayAdjuntosPendientes();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("Adjunto", sendData.getParametros().toString());
            response = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData, token);
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }


    private void enviarUbicaciones() {
        sendDatas = dbFuntions.getArrayUbicacionesPendientes();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("Ubicacion", sendData.getParametros().toString());
            response = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData, token);
            e("response", response.toString());
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }

    private void enviarTelefonia() {
        if (sessionData.isNuevoSim()) {
            sendDatas = dbFuntions.getArrayDataTelefonoPendiente();
            ApiResponse response = null;
            for (SendData sendData : sendDatas) {
                i("Operadora", sendData.getParametros().toString());
                response = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData, token);
                if (response.getHttpStatus() == OK.getCodigo()) {
                    interpretarResult(response.getTextReponse(), sendData);
                } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                    tokenValid = false;
                    break;
                }
            }
        }
    }


    private void enviarRecorrido() {
        sendDatas = dbFuntions.getArrayRecorridoPendiente();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("Recorrido", sendData.getParametros().toString());
            response = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData, token);
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }


    private void enviarEvento() {
        sendDatas = dbFuntions.getArrayEventosPendiente();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("Eventos", sendData.getParametros().toString());
            response = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData, token);
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }

    private void enviarCapturasEventos() {
        sendDatas = dbFuntions.getPendienteArrCapturasEvt();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("CapturaEventos", sendData.getParametros().toString());
            File file = new File(sendData.getParametros().get("archivo"));
            sendData.getParametros().remove("archivo");
            response = httpCliente.httpUpFile(sendData.getParametros(), urls[sendData.getTipo().getCodigo()], file, token);
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }

    private void enviarEventosDevice() {
        sendDatas = dbFuntions.getArrayEventosDevice();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("EventosDevice", sendData.getParametros().toString());
            response = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData, token);
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }


    private void syncCorrecciones() {
        sendDatas = dbFuntions.getArrCorreccionesSyncPendientes();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("Correcciones", sendData.getParametros().toString());
            response = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData, token);
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }

    private void enviarCapturasInvalidas() {
        sendDatas = dbFuntions.getArrCapturasInvalidas();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("Capturas Invalidas", sendData.getParametros().toString());
            response = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData, token);
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }

    private void enviarCapturasEvtInvalidas() {
        sendDatas = dbFuntions.getArrCapturasEvtInvalidas();
        ApiResponse response = null;
        for (SendData sendData : sendDatas) {
            i("Capturas Evt Invalidas", sendData.getParametros().toString());
            response = httpCliente.sendDataPost(urls[sendData.getTipo().getCodigo()], sendData, token);
            if (response.getHttpStatus() == OK.getCodigo()) {
                interpretarResult(response.getTextReponse(), sendData);
            } else if (response.getHttpStatus() == UNAUTHORIZED.getCodigo()) {
                tokenValid = false;
                break;
            }
        }
    }

    private void interpretarResult(String result, SendData sendData) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.getInt("respuesta") == 0) {
                if (sendData.getTipo().equals(CAPTURAS) || sendData.getTipo().equals(EVENTO_CAPTURAS)) {
                    String hashServer = jsonObject.getString("hash");
                    String hashLocal = dbFuntions.getHashImagen(parseLong(sendData.getId()), sendData.getTipo());
                    if (hashServer.equals(hashLocal)) {
                        envioCorrecto(sendData.getId(), sendData.getTipo());
                    } else {
                        dbFuntions.imagenNoValida(parseLong(sendData.getId()), sendData.getTipo());
                    }
                } else {
                    envioCorrecto(sendData.getId(), sendData.getTipo());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void envioCorrecto(String id, TIPO tipo) {
        switch (tipo.getCodigo()) {
            case 0:
                dbFuntions.changeEstadoEnvioVisita(1, id);
                break;
            case 1:
                dbFuntions.changeEstadoEnvio(1, id, TABLE_CAPTURAS);
                dbFuntions.eliminarImagen(id, TABLE_CAPTURAS);
                break;
            case 2:
                dbFuntions.changeEstadoEnvio(1, id, TABLE_ADJ);
                dbFuntions.eliminarZip(id);
                break;
            case 3:
                dbFuntions.changeEstadoEnvio(1, id, TABLE_UBICACIONES);
                break;
            case 4:
                dbFuntions.changeEstadoEnvio(1, id, TABLE_OPERADORA);
                break;
            case 5:
                dbFuntions.changeEstadoEnvio(1, id, TABLE_RECORRIDO);
                break;
            case 6:
                dbFuntions.changeEstadoEnvio(1, id, TABLE_EVENTOS);
                break;
            case 7:
                dbFuntions.changeEstadoEnvio(1, id, TABLE_CAPTURA_EVENTOS);
                dbFuntions.eliminarImagen(id, TABLE_CAPTURA_EVENTOS);
                break;
            case 8:
                dbFuntions.changeEstadoEnvio(1, id, TABLE_EVTCELL);
                break;
            case 11:
                dbFuntions.changeEstadoEnvio(1, id, TABLE_CORRECCIONES);
                break;
            case 12:
                dbFuntions.changeSync(1, id, TABLE_CAPTURAS);
                break;
            case 13:
                dbFuntions.changeSync(1, id, TABLE_CAPTURA_EVENTOS);
                break;
        }
        bus.post(new SendDataSuccessful());
    }


    @Override
    public void onDestroy() {
        i("SERVICIO", "Finalizado");
        if (tokenValid) {
            bus.post(new SendDataFinish());
        } else {
            bus.post(new TokenExpirado());
        }
        super.onDestroy();
    }
}