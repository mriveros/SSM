package com.stp.ssm.View;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stp.ssm.Model.Coordenadas;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.R;
import com.stp.ssm.Util.FechaUtil;
import com.stp.ssm.Util.ObjectToJson;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout.custom_coordenadas_view;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.toast_msj_geo_err;
import static com.stp.ssm.Util.FechaUtil.getFechaActual;
import static com.stp.ssm.Util.ObjectToJson.getJsonFromObject;
import static com.stp.ssm.View.ViewFactory.notificacionToast;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;

public class CustomCoordenadasView extends LinearLayout {

    private Context context;
    private Pregunta pregunta;
    private TextView txt_coordenadas_lon;
    private TextView txt_coordenadas_lat;
    private TextView txt_coordenadas_alt;
    private TextView txt_coordenadas_prec;
    private TextView txt_coordenadas_prov;
    private Button btnObtCoordenadas;
    private LocationManager locationManager;

    public CustomCoordenadasView(Context context, Pregunta pregunta) {
        super(context);
        this.context = context;
        this.pregunta = pregunta;
        inicializar();
    }

    public CustomCoordenadasView(Context context, @Nullable AttributeSet attrs, Pregunta pregunta) {
        super(context, attrs);
        this.context = context;
        this.pregunta = pregunta;
        inicializar();
    }

    private void inicializar() {
        String infService = LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(custom_coordenadas_view, this, true);

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        txt_coordenadas_lon = (TextView) findViewById(id.txt_coordenadas_lon);
        txt_coordenadas_lat = (TextView) findViewById(id.txt_coordenadas_lat);
        txt_coordenadas_alt = (TextView) findViewById(id.txt_coordenadas_alt);
        txt_coordenadas_prec = (TextView) findViewById(id.txt_coordenadas_prec);
        txt_coordenadas_prov = (TextView) findViewById(id.txt_coordenadas_prov);
        btnObtCoordenadas = (Button) findViewById(id.btnObtCoordenadas);

        asignarEventos();
    }

    private void asignarEventos() {
        btnObtCoordenadas.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Coordenadas coordenadas = capturarCoodenadas();
                cargarCoordenadas(coordenadas);
                pregunta.responder(getJsonFromObject(coordenadas));
            }
        });
    }

    private void cargarCoordenadas(Coordenadas coordenadas) {
        txt_coordenadas_lon.setText(coordenadas.convertLongitud());
        txt_coordenadas_lat.setText(coordenadas.convertLatitud());
        txt_coordenadas_alt.setText(Double.toString(coordenadas.getAltitud()));
        txt_coordenadas_prec.setText(Float.toString(coordenadas.getPrecision()));
        txt_coordenadas_prov.setText(coordenadas.getProveedor());
    }

    public void setCoordenadas(String jsonCoordenadas) {
        try {
            JSONObject jsonObject = new JSONObject(jsonCoordenadas);
            Coordenadas coordenadas = new Coordenadas(jsonObject.getString("longitud"),
                    jsonObject.getString("latitud"),
                    parseFloat(jsonObject.getString("precision")),
                    parseDouble(jsonObject.getString("altitud")),
                    jsonObject.getString("proveedor"),
                    jsonObject.getString("hora"));
            cargarCoordenadas(coordenadas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setDisableButton() {
        btnObtCoordenadas.setVisibility(GONE);
    }

    private Coordenadas capturarCoodenadas() {
        Location locationgps;
        Location locationnetwork;
        try {
            locationgps = locationManager.getLastKnownLocation(GPS_PROVIDER);
            locationnetwork = locationManager.getLastKnownLocation(NETWORK_PROVIDER);
            if (locationgps != null && locationnetwork != null) {
                if (locationgps.getAccuracy() <= locationnetwork.getAccuracy()) {
                    return generateCoordenadas(locationgps);
                } else {
                    return generateCoordenadas(locationnetwork);
                }
            } else if (locationgps != null) {
                return generateCoordenadas(locationgps);
            } else if (locationnetwork != null) {
                return generateCoordenadas(locationnetwork);
            } else {
                notificacionToast(context, context.getString(toast_msj_geo_err));
            }
        } catch (SecurityException e) {
        }
        return null;
    }

    private Coordenadas generateCoordenadas(Location location) {
        return new Coordenadas(Double.toString(location.getLongitude()),
                Double.toString(location.getLatitude()),
                location.getAccuracy(),
                location.getAltitude(),
                location.getProvider(),
                getFechaActual());
    }
}
