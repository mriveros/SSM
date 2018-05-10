package com.stp.ssm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.stp.ssm.Evt.ValidIdentificacionesEvt;
import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Interfaces.OnTimeCronometroListener;
import com.stp.ssm.Model.Beneficiario;
import com.stp.ssm.Model.Coordenadas;
import com.stp.ssm.Model.Departamento;
import com.stp.ssm.Model.Distrito;
import com.stp.ssm.Model.Localidad;
import com.stp.ssm.Model.Motivos;
import com.stp.ssm.Model.Proyecto;
import com.stp.ssm.Model.Visita;
import com.stp.ssm.Servicios.SendDataService2;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.Cronometro;
import com.stp.ssm.Util.FechaUtil;
import com.stp.ssm.http.HttpVolleyRequest;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.View.ViewFactory;
import com.stp.ssm.databases.BDLocalidadesFuntions;
import com.stp.ssm.http.URLs;

import java.util.ArrayList;

import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.widget.AdapterView.OnItemSelectedListener;
import static android.widget.CompoundButton.OnCheckedChangeListener;
import static com.stp.ssm.Model.Beneficiario.Estado;
import static com.stp.ssm.Model.Beneficiario.Estado.NUEVO_NO_VALIDADO;
import static com.stp.ssm.Model.Beneficiario.TipoBeneficiario;
import static com.stp.ssm.Model.Beneficiario.TipoBeneficiario.PERSONA;
import static com.stp.ssm.Model.Visita.TipoVisita;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_ASIGNADO;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_NUEVO_BENEFICIARIO;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.lbl_spinne_item;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_visita;
import static com.stp.ssm.R.layout.spinner_item_1;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msg_validando;
import static com.stp.ssm.R.string.dialog_msj_alert_confir_out;
import static com.stp.ssm.R.string.dialog_title_alert_confir;
import static com.stp.ssm.R.string.dialog_title_validando;
import static com.stp.ssm.R.string.lbl_btn_emprezar;
import static com.stp.ssm.R.string.lbl_modulo;
import static com.stp.ssm.R.string.msj_err_apellido;
import static com.stp.ssm.R.string.msj_err_documento;
import static com.stp.ssm.R.string.msj_err_nombre;
import static com.stp.ssm.R.string.toast_msj_beneficiario_duplicado;
import static com.stp.ssm.R.string.toast_msj_err_sin_ced;
import static com.stp.ssm.R.string.toast_msj_geo_err;
import static com.stp.ssm.R.string.toast_msj_sin_con;
import static com.stp.ssm.Util.CellUtils.hasConnectionInternet;
import static com.stp.ssm.Util.CellUtils.isMyServiceRunning;
import static com.stp.ssm.Util.FechaUtil.SegundoToHora;
import static com.stp.ssm.Util.FechaUtil.getFechaActual;
import static com.stp.ssm.Util.FechaUtil.getFechaCadena;
import static com.stp.ssm.View.ViewFactory.notificacionToast;
import static com.stp.ssm.http.HttpVolleyRequest.getInstance;
import static com.stp.ssm.http.URLs.URL_INDENTIFICACIONES;
import static java.lang.Integer.parseInt;

public class VisitaCapacitacionActivity extends BaseActivity {

    private Visita visita;
    private Beneficiario beneficiario;
    private Proyecto proyecto;
    private Cronometro cron;
    private Handler handler = new Handler();
    private LocationManager locationManager;

    private ArrayList<Motivos> motivos;
    private ArrayList<Departamento> departamentos;
    private ArrayList<Localidad> localidads;

    private TextView lbl_time_visita;
    private LinearLayout layout_capturas;
    private TextView lbl_tipo_destinatario;
    private Spinner spTipoBeneficiario;
    private ToggleButton tgbtnSinCed;
    private EditText edtCedRuc;
    private Button btnVerif;
    private EditText edtNombre;
    private EditText edtNombre1;
    private EditText edtApellido;
    private EditText edtApellido1;
    private Spinner spMotivos;
    private CheckBox chJefeFamilia;
    private Spinner spDepartamento;
    private Spinner spDistritos;
    private Spinner spLocalidad;
    private TextView lbl_motivos;
    private TextView lbl_observacion;
    private EditText edtObservacion;
    private Button btnFinalizar;

    private long segundos = 0;
    private int distritoSelect = 0;
    private int deptSelect = 0;
    private int localidadSelect = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_visita);
        inicializar();
        eventBus.register(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        lbl_time_visita = (TextView) findViewById(id.lbl_time_visita);
        lbl_tipo_destinatario = (TextView) findViewById(id.lbl_tipo_destinatario);
        layout_capturas = (LinearLayout) findViewById(id.layout_capturas);
        lbl_tipo_destinatario = (TextView) findViewById(id.lbl_tipo_destinatario);
        spTipoBeneficiario = (Spinner) findViewById(id.spTipoBeneficiario);
        tgbtnSinCed = (ToggleButton) findViewById(id.tgbtnSinCed);
        edtCedRuc = (EditText) findViewById(id.edtCedRuc);
        btnVerif = (Button) findViewById(id.btnVerif);
        btnVerif.setEnabled(true);
        edtNombre = (EditText) findViewById(id.edtNombre);
        edtNombre1 = (EditText) findViewById(id.edtNombre1);
        edtApellido = (EditText) findViewById(id.edtApellido);
        edtApellido1 = (EditText) findViewById(id.edtApellido1);
        spMotivos = (Spinner) findViewById(id.spMotivos);
        chJefeFamilia = (CheckBox) findViewById(id.chJefeFamilia);
        spDepartamento = (Spinner) findViewById(id.spDepartamento);
        spDistritos = (Spinner) findViewById(id.spDistritos);
        spLocalidad = (Spinner) findViewById(id.spLocalidad);
        lbl_motivos = (TextView) findViewById(id.lbl_motivos);
        lbl_observacion = (TextView) findViewById(id.lbl_observacion);
        edtObservacion = (EditText) findViewById(id.edtObservacion);
        btnFinalizar = (Button) findViewById(id.btnFinalizar);

        asignarEventos();
        cargarDatos();
        inicializarCronometro();
    }

    private void asignarEventos() {
        tgbtnSinCed.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    generarCodDocumento();
                    edtCedRuc.setEnabled(false);
                    btnVerif.setEnabled(false);
                } else {
                    edtCedRuc.setEnabled(true);
                    edtCedRuc.setText("");
                    btnVerif.setEnabled(true);
                }
            }
        });

        btnVerif.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasConnectionInternet(getApplicationContext())) {
                    if (!edtCedRuc.getText().toString().equals("")) {
                        String request = URL_INDENTIFICACIONES + "/?ci=" + edtCedRuc.getText().toString();
                        showProgressDialog(getString(dialog_title_validando), getString(dialog_msg_validando));
                        getInstance(getApplicationContext()).validarCedula(request);
                    } else {
                        notificacionToast(getApplicationContext(), getString(toast_msj_err_sin_ced));
                    }
                } else {
                    notificacionToast(getApplicationContext(), getString(toast_msj_sin_con));
                }
            }
        });

        spDepartamento.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deptSelect = parseInt(departamentos.get(position).getCodigo());
                spDistritos.setAdapter(new ArrayAdapter<Distrito>(getApplicationContext(),
                        spinner_item_1,
                        id.lbl_spinne_item,
                        departamentos.get(position).getDistritos()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spDistritos.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                distritoSelect = parseInt(departamentos.get(spDepartamento.getSelectedItemPosition()).getDistritos().get(position).getCodigo());
                localidads = BDLocalidadesFuntions.getInstance(getApplicationContext()).getLocalidadByDistrito(parseInt(departamentos.get(spDepartamento.getSelectedItemPosition()).getCodigo()), distritoSelect);
                localidads.add(new Localidad(0, "-OTRO-"));
                spLocalidad.setAdapter(new ArrayAdapter<Localidad>(getApplicationContext(),
                        spinner_item_1,
                        id.lbl_spinne_item,
                        localidads));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spLocalidad.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                localidadSelect = localidads.get(position).getCodigo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnFinalizar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Coordenadas coordenadas = capturarCoodenadas();
                if (validarDatos(coordenadas)) {
                    saltarActivity(coordenadas);
                }
            }
        });
    }

    private void cargarDatos() {
        proyecto = (Proyecto) getIntent().getExtras().getSerializable("proyecto");
        motivos = dbFuntions.getMotivos(parseInt(proyecto.getCodigo()));
        spMotivos.setAdapter(new ArrayAdapter<Motivos>(getApplicationContext(),
                spinner_item_1,
                lbl_spinne_item,
                motivos));
        beneficiario = (Beneficiario) getIntent().getExtras().getSerializable("beneficiario");
        setTitle(proyecto.getDescripcion());
        layout_capturas.setVisibility(GONE);
        lbl_tipo_destinatario.setVisibility(GONE);
        spTipoBeneficiario.setVisibility(GONE);
        tgbtnSinCed.setVisibility(VISIBLE);
        chJefeFamilia.setVisibility(GONE);
        departamentos = dbFuntions.getArrayDepartametos();
        spDepartamento.setAdapter(new ArrayAdapter<Departamento>(this, spinner_item_1,
                lbl_spinne_item,
                departamentos));
        lbl_motivos.setText(getString(lbl_modulo));
        lbl_observacion.setVisibility(GONE);
        edtObservacion.setVisibility(GONE);
        btnFinalizar.setText(getString(lbl_btn_emprezar));

        if (getIntent().getExtras().getInt("tipovisita") == VISITA_ASIGNADO.getCodigo()) {
            deptSelect = beneficiario.getDepartamento();
            distritoSelect = beneficiario.getDistrito();
            localidadSelect = beneficiario.getLocalidad();

            seleccionarDpto(deptSelect);
            seleccionarDistrito(distritoSelect);
            seleccionarLocalidad(localidadSelect);
            visita = new Visita(getFechaActual(), VISITA_ASIGNADO);
            edtCedRuc.setText(beneficiario.getDocumento());
            edtNombre.setText(beneficiario.getPrimerNombre());
            edtNombre1.setText(beneficiario.getSegundoNombre());
            edtApellido.setText(beneficiario.getPrimerApellido());
            edtApellido1.setText(beneficiario.getSegundoApellido());
        } else {
            visita = new Visita(getFechaActual(), VISITA_NUEVO_BENEFICIARIO);
        }
    }

    private void inicializarCronometro() {
        cron = new Cronometro();
        cron.setOnTimeCronometroListener(new OnTimeCronometroListener() {
            @Override
            public void OnTimeCronometro(final long time) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        segundos = time;
                        sessionData.setLastTimeVisita(segundos);
                        lbl_time_visita.setText(SegundoToHora(segundos));
                    }
                }, 100);
            }
        });
        cron.inicializar(segundos);
    }

    private void generarCodDocumento() {
        String cadena = sessionData.getUsuario() + "_" + proyecto.getCodigo() + "_" + motivos.get(spMotivos.getSelectedItemPosition()).getCodmotivo() + "_" + getFechaCadena();
        edtCedRuc.setText(cadena);
    }

    @Override
    protected void onDestroy() {
        cron.finalizar();
        super.onDestroy();
    }

    public void onEvent(ValidIdentificacionesEvt evt) {
        offProgressDialog();
        edtNombre.setText(evt.getPrimerNombre() != null ? evt.getPrimerNombre() : "");
        edtNombre1.setText(evt.getSegundoNombre() != null ? evt.getSegundoNombre() : "");
        edtApellido.setText(evt.getPrimerApellido() != null ? evt.getPrimerApellido() : "");
        edtApellido1.setText(evt.getSegundoApellido() != null ? evt.getSegundoApellido() : "");
    }

    private void seleccionarDpto(int cod_departamento) {
        for (int i = 0; i < departamentos.size(); i++) {
            if (parseInt(departamentos.get(i).getCodigo()) == cod_departamento) {
                spDepartamento.setSelection(i);
                break;
            }
        }
    }

    private void seleccionarDistrito(int cod_distrito) {
        ArrayList<Distrito> arr_distritos = departamentos.get(spDepartamento.getSelectedItemPosition()).getDistritos();
        for (int i = 0; i < arr_distritos.size(); i++) {
            if (parseInt(arr_distritos.get(i).getCodigo()) == cod_distrito) {
                spDistritos.setSelection(i);
                break;
            }
        }
    }

    private void seleccionarLocalidad(int cod_localidad) {
        localidads = BDLocalidadesFuntions.getInstance(getApplicationContext()).getLocalidadByDistrito(parseInt(departamentos.get(spDepartamento.getSelectedItemPosition()).getCodigo()), distritoSelect);
        for (int i = 0; i < localidads.size(); i++) {
            if (localidads.get(i).getCodigo() == cod_localidad) {
                spLocalidad.setSelection(i);
                break;
            }
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
            } else {
                notificacionToast(getApplicationContext(), getString(toast_msj_geo_err));
            }
        } catch (SecurityException e) {
        }
        return null;
    }

    private Coordenadas generateCoordenadas(Location location) {
        return new Coordenadas(Double.toString(location.getLongitude()), Double.toString(location.getLatitude()), location.getAccuracy());
    }

    private boolean validarDatos(Coordenadas coordenadas) {
        if (edtCedRuc.getText().toString().equals("")) {
            edtCedRuc.setError(getString(msj_err_documento));
            return false;
        }

        if (spTipoBeneficiario.getSelectedItemPosition() == 0) {
            if (edtNombre.getText().toString().equals("")) {
                edtNombre.setError(getString(msj_err_nombre));
                return false;
            }

            if (edtApellido.getText().toString().equals("")) {
                edtApellido.setError(getString(msj_err_apellido));
                return false;
            }
        } else {
            if (edtNombre.getText().toString().equals("")) {
                edtNombre.setError(getString(msj_err_nombre));
                return false;
            }
        }

        if (existeDocumento()) {
            notificacionToast(getApplicationContext(), getString(toast_msj_beneficiario_duplicado));
            return false;
        }

        if (coordenadas == null || coordenadas.getPrecision() > 30) {
            startActivityForResult(new Intent(getApplicationContext(), CalibrarLocationActivity.class), 300);
            return false;
        }
        return true;
    }

    private boolean existeDocumento() {
        if (visita.getTipo().equals(VISITA_NUEVO_BENEFICIARIO)) {
            String documento;
            documento = edtCedRuc.getText().toString();
            return dbFuntions.isBeneficiarioExists(documento);
        }
        return false;
    }

    private void saltarActivity(Coordenadas coordenadas) {
        guardarRelevamiento(coordenadas);

        Bundle bundle = new Bundle();
        bundle.putSerializable("motivo", motivos.get(spMotivos.getSelectedItemPosition()));
        bundle.putSerializable("proyecto", proyecto);
        bundle.putLong("idvisita", visita.getIdvisita());
        bundle.putString("beneficiario", beneficiario.getDocumento());
        bundle.putBoolean("recuperar", false);
        bundle.putLong("segundos", segundos);
        bundle.putInt("tipo_visita", visita.getTipo().getCodigo());
        bundle.putInt("tipo_destinatario", spTipoBeneficiario.getSelectedItemPosition());

        Intent intent = new Intent(getApplicationContext(), VideoCapacitacionActivity.class).putExtras(bundle);
        startActivityForResult(intent, 400);
    }

    private void guardarRelevamiento(Coordenadas coordenadas) {
        String nombre = edtNombre.getText().toString() + "#" + edtNombre1.getText().toString();
        String apellido = edtApellido.getText().toString() + "#" + edtApellido1.getText().toString();
        String documento = edtCedRuc.getText().toString();

        Estado estado;
        if (visita.getTipo().equals(VISITA_ASIGNADO)) {
            estado = beneficiario.getEstado();
        } else {
            estado = NUEVO_NO_VALIDADO;
        }
        TipoBeneficiario tipo = PERSONA;
        beneficiario = new Beneficiario(nombre, apellido, documento, sessionData.getUsuario(), proyecto.getCodigo(), tipo, distritoSelect, deptSelect, estado, false);
        beneficiario.setCoordenadas(coordenadas);
        beneficiario.setLocalidad(localidadSelect);
        dbFuntions.addBeneficiario(beneficiario);
        visita.setBeneficiario(beneficiario);
        visita.setHorafin(getFechaActual());
        visita.setCodmotivo(motivos.get(spMotivos.getSelectedItemPosition()).getCodmotivo());
        visita.setCoordenadas(coordenadas);
        visita.setProyecto(parseInt(proyecto.getCodigo()));
        visita.setObservacion("");
        visita.setTiempo(segundos);
        visita.setHasFormulario();
        long idvisita = dbFuntions.addVisita(visita, sessionData.getUsuario(), beneficiario);
        visita.setIdvisita(idvisita);
        sessionData.setLastIdVisita(idvisita);
    }

    private void limpiarBase(Visita visita, Beneficiario beneficiario) {
        if (visita.getTipo().getCodigo() == VISITA_NUEVO_BENEFICIARIO.getCodigo()) {
            if (beneficiario != null) {
                dbFuntions.borrarBeneficiarioByCed(beneficiario.getDocumento());
            }
        }
        dbFuntions.borrarVisitaById(visita.getIdvisita());
    }

    private void enviarDatos() {
        if (hasConnectionInternet(getApplicationContext()) && !isMyServiceRunning(SendDataService2.class, getApplicationContext())) {
            startService(new Intent(getApplicationContext(), SendDataService2.class));
        }
    }

    @Override
    public void onBackPressed() {
        DialogAcpView dialog = new DialogAcpView(getString(dialog_title_alert_confir),
                getString(dialog_msj_alert_confir_out));
        dialog.setOnClickDialogListener(new OnClickDialogListener() {
            @Override
            public void OnPositiveClick(DialogInterface dialog, String tag) {
                dialog.dismiss();
                limpiarBase(visita, beneficiario);
                sessionData.setLastIdVisita(0);
                finish();
            }

            @Override
            public void OnNegativeClick(DialogInterface dialog, String tag) {
                dialog.dismiss();
            }
        });

        dialog.show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 300:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    Coordenadas coordenadas = new Coordenadas(Double.toString(bundle.getDouble("longitud")),
                            Double.toString(bundle.getDouble("latitud")),
                            bundle.getFloat("presicion"));
                    saltarActivity(coordenadas);
                }
                break;
            case 400:
                if (resultCode == RESULT_OK) {
                    sessionData.setLastIdVisita(0);
                    sessionData.setLastTimeVisita(0);
                    enviarDatos();
                    finish();
                } else {
                    if (visita.getTipo().getCodigo() == VISITA_NUEVO_BENEFICIARIO.getCodigo()) {
                        if (beneficiario != null) {
                            dbFuntions.borrarBeneficiarioByCed(beneficiario.getDocumento());
                        }
                    }
                    dbFuntions.borrarVisitaById(visita.getIdvisita());
                }
                break;
        }
    }
}
