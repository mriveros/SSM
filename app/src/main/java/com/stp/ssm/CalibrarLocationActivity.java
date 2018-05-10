package com.stp.ssm;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.stp.ssm.View.ViewFactory;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.view.View.OnClickListener;
import static android.view.Window.FEATURE_NO_TITLE;
import static android.widget.AdapterView.OnItemSelectedListener;
import static com.stp.ssm.R.array;
import static com.stp.ssm.R.array.array_geo_provider;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.lbl_spinne_item;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.dialog_custom_location;
import static com.stp.ssm.R.layout.spinner_item_1;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.lbl_metros;
import static com.stp.ssm.R.string.lbl_rep_lat;
import static com.stp.ssm.R.string.lbl_rep_lat_null;
import static com.stp.ssm.R.string.lbl_rep_long;
import static com.stp.ssm.R.string.lbl_rep_long_null;
import static com.stp.ssm.R.string.lbl_rep_pres;
import static com.stp.ssm.R.string.lbl_rep_pres_null;
import static com.stp.ssm.R.string.toast_null_location;
import static com.stp.ssm.View.ViewFactory.notificacionToast;


public class CalibrarLocationActivity extends AppCompatActivity {

    private TextView lbltxtLongitud;
    private TextView lbltxtlatitud;
    private TextView lbltxtprecision;
    private Spinner spProviderGeo;
    private Button btnAcpPos;
    private boolean swlistener = false;
    private String proveedor;
    private Location location;
    private LocationManager locationManager;

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            actualizarPosicion(location);
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
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_NO_TITLE);
        setContentView(dialog_custom_location);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        lbltxtLongitud = (TextView) findViewById(id.lbltxtLongitud);
        lbltxtlatitud = (TextView) findViewById(id.lbltxtlatitud);
        lbltxtprecision = (TextView) findViewById(id.lbltxtprecision);
        spProviderGeo = (Spinner) findViewById(id.spProviderGeo);
        spProviderGeo.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                spinner_item_1,
                lbl_spinne_item,
                getResources().getStringArray(array_geo_provider)));
        btnAcpPos = (Button) findViewById(id.btnAcpPos);

        asignarEventos();
    }


    private void asignarEventos() {
        spProviderGeo.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (swlistener) {
                    try {
                        locationManager.removeUpdates(listener);
                    } catch (SecurityException e) {
                    }
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

        btnAcpPos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (location != null) {
                    bundle.putDouble("longitud", location.getLongitude());
                    bundle.putDouble("latitud", location.getLatitude());
                    bundle.putFloat("presicion", location.getAccuracy());
                    Intent intent = getIntent();
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    notificacionToast(getApplicationContext(), getString(toast_null_location));
                }
            }
        });
    }

    private void suscribirProvider(String proveedor) {
        swlistener = true;
        try {
            location = locationManager.getLastKnownLocation(proveedor);
            actualizarPosicion(location);
            locationManager.requestLocationUpdates(proveedor, 0, 0, listener);
        } catch (SecurityException e) {
        }
    }


    private void actualizarPosicion(Location location) {
        if (location == null) {
            lbltxtlatitud.setText(getString(lbl_rep_lat_null));
            lbltxtLongitud.setText(getString(lbl_rep_long_null));
            lbltxtprecision.setText(getString(lbl_rep_pres_null));

        } else {
            lbltxtlatitud.setText(getString(lbl_rep_lat) + location.getLatitude());
            lbltxtLongitud.setText(getString(lbl_rep_long) + location.getLongitude());
            lbltxtprecision.setText(getString(lbl_rep_pres) + location.getAccuracy() + getString(lbl_metros));
        }
    }

    @Override
    protected void onPause() {
        try {
            locationManager.removeUpdates(listener);
        } catch (SecurityException e) {
        }
        super.onPause();
    }
}
