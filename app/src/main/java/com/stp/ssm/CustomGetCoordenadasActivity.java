package com.stp.ssm;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.stp.ssm.Model.Coordenadas;
import com.stp.ssm.Util.CoordenadasUtils;
import com.stp.ssm.Util.FechaUtil;

import static android.content.Intent.CATEGORY_DEFAULT;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.view.View.OnClickListener;
import static android.view.ViewGroup.LayoutParams;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.widget.AdapterView.OnItemSelectedListener;
import static com.stp.ssm.R.array;
import static com.stp.ssm.R.array.array_geo_provider;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.lbl_spinne_item;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_custom_get_coordenadas;
import static com.stp.ssm.R.layout.spinner_item_1;
import static com.stp.ssm.Util.CoordenadasUtils.TipoCoordenada;
import static com.stp.ssm.Util.CoordenadasUtils.TipoCoordenada.LATITUD;
import static com.stp.ssm.Util.CoordenadasUtils.TipoCoordenada.LONGITUD;
import static com.stp.ssm.Util.CoordenadasUtils.decimalToDegree;
import static com.stp.ssm.Util.FechaUtil.getFechaActual;

public class CustomGetCoordenadasActivity extends BaseActivity implements LocationListener {

    private Spinner spCustomProviderGeo;
    private TextView txt_custom_longitud;
    private TextView txt_custom_latitud;
    private TextView txt_custom_altitud;
    private TextView txt_custom_precision;
    private EditText edt_custom_descripcion;
    private Button btn_custom_agregar;
    private Location location;
    private LocationManager locationManager;
    private String proveedor;
    private Coordenadas coordenadas;
    private String uuid;

    public static final String ACTION_GET_COORDENADAS = "com.stp.ssm.GETCOORDENADAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_NO_TITLE);
        setContentView(activity_custom_get_coordenadas);
        getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);

        inicializar();
        uuid = getIntent().getExtras().getString("uuid");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        spCustomProviderGeo = (Spinner) findViewById(id.spCustomProviderGeo);
        spCustomProviderGeo.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                spinner_item_1,
                lbl_spinne_item,
                getResources().getStringArray(array_geo_provider)));
        txt_custom_longitud = (TextView) findViewById(id.txt_custom_longitud);
        txt_custom_latitud = (TextView) findViewById(id.txt_custom_latitud);
        txt_custom_altitud = (TextView) findViewById(id.txt_custom_altitud);
        txt_custom_precision = (TextView) findViewById(id.txt_custom_precision);
        edt_custom_descripcion = (EditText) findViewById(id.edt_custom_descripcion);
        btn_custom_agregar = (Button) findViewById(id.btn_custom_agregar);

        asignarEventos();
    }

    private void asignarEventos() {
        btn_custom_agregar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    location = locationManager.getLastKnownLocation(proveedor);
                    locationManager.removeUpdates(CustomGetCoordenadasActivity.this);
                } catch (SecurityException ex) {
                }

                coordenadas = new Coordenadas(Double.toString(location.getLongitude()),
                        Double.toString(location.getLatitude()),
                        location.getAccuracy(),
                        location.getAltitude(),
                        location.getProvider(),
                        getFechaActual(),
                        edt_custom_descripcion.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable("coordenadas", coordenadas);
                bundle.putString("uuid", uuid);
                enviarBroadcast(bundle);
                finish();
            }
        });

        spCustomProviderGeo.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    locationManager.removeUpdates(CustomGetCoordenadasActivity.this);
                } catch (Exception e) {
                }

                if (position == 0) {
                    proveedor = GPS_PROVIDER;
                } else {
                    proveedor = NETWORK_PROVIDER;
                }
                suscribirProvider(proveedor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void suscribirProvider(String proveedor) {
        try {
            location = locationManager.getLastKnownLocation(proveedor);
            showPosicion(location);
            locationManager.requestLocationUpdates(proveedor, 0, 0, CustomGetCoordenadasActivity.this);
        } catch (SecurityException e) {
        }
    }

    private void showPosicion(Location location) {
        if (location != null) {
            txt_custom_latitud.setText(decimalToDegree(location.getLatitude(), LATITUD));
            txt_custom_longitud.setText(decimalToDegree(location.getLongitude(), LONGITUD));
            txt_custom_altitud.setText(Double.toString(location.getAltitude()));
            txt_custom_precision.setText(Float.toString(location.getAccuracy()));
        } else {
            txt_custom_latitud.setText("SIN SEÑAL");
            txt_custom_longitud.setText("SIN SEÑAL");
            txt_custom_altitud.setText("SIN SEÑAL");
            txt_custom_precision.setText("SIN SEÑAL");
        }
    }

    private void enviarBroadcast(Bundle bundle) {
        Intent intentResponse = new Intent();
        intentResponse.setAction(ACTION_GET_COORDENADAS);
        intentResponse.addCategory(CATEGORY_DEFAULT);
        intentResponse.putExtras(bundle);
        sendBroadcast(intentResponse);
    }

    @Override
    public void onLocationChanged(Location location) {
        showPosicion(location);
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
