package com.stp.ssm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Model.Coordenadas;
import com.stp.ssm.Model.Evento;
import com.stp.ssm.Servicios.SendDataService2;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.FechaUtil;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.View.DialogImagenes;

import java.io.File;
import java.util.ArrayList;

import com.stp.ssm.View.ViewFactory;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.view.View.OnClickListener;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.act_camera;
import static com.stp.ssm.R.id.act_history;
import static com.stp.ssm.R.id.act_img;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_reporte_evento;
import static com.stp.ssm.R.menu;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msg_salir;
import static com.stp.ssm.R.string.dialog_title_salir;
import static com.stp.ssm.R.string.title_Eventos;
import static com.stp.ssm.R.string.toast_msj_text_vacio;
import static com.stp.ssm.Util.CellUtils.hasConnectionInternet;
import static com.stp.ssm.Util.CellUtils.isMyServiceRunning;
import static com.stp.ssm.Util.CellUtils.takePhoto;
import static com.stp.ssm.Util.FechaUtil.getFechaActual;
import static com.stp.ssm.View.ViewFactory.notificacionToast;

public class InformarEventoActivity extends BaseActivity {

    private Button btnReportar;
    private EditText edtEvento;
    private TextView lblcontcapturasevt;
    private Coordenadas coordenadas;
    private ArrayList<String> capturas;
    private boolean isrunning = true;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_reporte_evento);
        setTitle(getString(title_Eventos));
        inicializar();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        capturas = new ArrayList<>();
        btnReportar = (Button) findViewById(id.btnReportar);
        edtEvento = (EditText) findViewById(id.edtEvento);
        lblcontcapturasevt = (TextView) findViewById(id.lblcontcapturasevt);

        asignarEventos();
    }

    @Override
    protected void onStart() {
        restaurarDatos();
        super.onStart();
    }


    private void asignarEventos() {
        btnReportar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });
    }

    private void guardar() {
        coordenadas = capturarCoodenadas();
        if (validarDatos(coordenadas)) {
            dbFuntions.addEvento(new Evento(coordenadas, edtEvento.getText().toString(), capturas, getFechaActual()));
            enviarDatos();
            isrunning = false;
            finish();
        }
    }


    private Coordenadas generateCoordenadas(Location location) {
        return new Coordenadas(Double.toString(location.getLongitude()), Double.toString(location.getLatitude()), location.getAccuracy());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu.mn_eventos, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case act_camera:
                takePhoto(this, capturas);
                break;
            case act_img:
                new DialogImagenes(capturas).show(getSupportFragmentManager(), "");
                break;
            case act_history:
                startActivity(new Intent(getApplicationContext(), HistorialEvtActivity.class));
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 200:
                if (resultCode == RESULT_CANCELED) {
                    capturas.remove(capturas.size() - 1);
                }
                lblcontcapturasevt.setText(" " + Integer.toString(capturas.size()));
                break;
            case 300:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Coordenadas coordenadas = new Coordenadas(Double.toString(bundle.getDouble("longitud")),
                            Double.toString(bundle.getDouble("latitud")),
                            bundle.getFloat("presicion"));
                    dbFuntions.addEvento(new Evento(coordenadas, edtEvento.getText().toString(), capturas, getFechaActual()));
                    enviarDatos();
                    finish();
                }
                break;
        }

    }


    @Override
    public void onBackPressed() {
        isrunning = false;
        if (capturas.size() > 0 || !edtEvento.equals("")) {
            DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_salir),
                    getString(dialog_msg_salir));
            dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
                @Override
                public void OnPositiveClick(DialogInterface dialog, String tag) {
                    dialog.dismiss();
                    if (!capturas.isEmpty()) {
                        File file;
                        for (String captura : capturas) {
                            file = new File(captura);
                            if (file.exists()) {
                                file.delete();
                            }
                        }
                    }
                    finish();
                }

                @Override
                public void OnNegativeClick(DialogInterface dialog, String tag) {
                    dialog.dismiss();
                }
            });
            dialogAcpView.show(getSupportFragmentManager(), "");
        } else {
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        if (isrunning) {
            dbFuntions.addCapturaEvento(0, capturas, 2);
        }
        super.onDestroy();
    }


    private void restaurarDatos() {
        ArrayList<String> restoreCap = dbFuntions.getCapturasEventos(0);
        if (!restoreCap.isEmpty()) {
            capturas = restoreCap;
            dbFuntions.borrarCapturasByEventos(0);
        }
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
            }
        } catch (SecurityException e) {
        }
        return null;
    }


    private boolean validarDatos(Coordenadas coordenadas) {
        if (edtEvento.getText().toString().equals("")) {
            notificacionToast(getApplicationContext(), getString(toast_msj_text_vacio));
            return false;
        }

        if (coordenadas == null || coordenadas.getPrecision() > 30) {
            startActivityForResult(new Intent(getApplicationContext(), CalibrarLocationActivity.class), 300);
            return false;
        }
        return true;
    }


    private void enviarDatos() {
        if (hasConnectionInternet(getApplicationContext()) && !isMyServiceRunning(SendDataService2.class, getApplicationContext())) {
            startService(new Intent(getApplicationContext(), SendDataService2.class));
        }
    }
}
