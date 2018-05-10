package com.stp.ssm;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.scanlibrary.ScanConstants;
import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Interfaces.OnDeleteListener;
import com.stp.ssm.Interfaces.OnTimeCronometroListener;
import com.stp.ssm.Model.Beneficiario;
import com.stp.ssm.Model.Capturas;
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
import com.stp.ssm.Util.Encriptacion;
import com.stp.ssm.Util.FechaUtil;
import com.stp.ssm.Util.ImageFileUtil;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.View.DialogImagenes2;
import com.stp.ssm.View.DialogLstAdjuntos;
import com.stp.ssm.View.ViewFactory;
import com.stp.ssm.databases.BDLocalidadesFuntions;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.ClipData.Item;
import static android.content.Intent.ACTION_GET_CONTENT;
import static android.content.Intent.EXTRA_ALLOW_MULTIPLE;
import static android.content.Intent.createChooser;
import static android.location.LocationManager.GPS_PROVIDER;
import static android.location.LocationManager.NETWORK_PROVIDER;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images;
import static android.provider.MediaStore.Images.Media;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static android.provider.MediaStore.Images.Media.getBitmap;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.widget.AdapterView.OnItemSelectedListener;
import static com.scanlibrary.ScanConstants.SCANNED_RESULT;
import static com.stp.ssm.Model.Beneficiario.Estado;
import static com.stp.ssm.Model.Beneficiario.Estado.NUEVO_NO_VALIDADO;
import static com.stp.ssm.Model.Beneficiario.TipoBeneficiario;
import static com.stp.ssm.Model.Beneficiario.TipoBeneficiario.ENTIDAD_NO_INSTITUCION;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN.CAMARA;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN.GALERIA;
import static com.stp.ssm.Model.Visita.TipoVisita;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_ASIGNADO;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_CORREGIR;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_NUEVO_BENEFICIARIO;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_SIN_BENEFICIARIO;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.act_adj;
import static com.stp.ssm.R.id.act_camera;
import static com.stp.ssm.R.id.act_img;
import static com.stp.ssm.R.id.act_scanner;
import static com.stp.ssm.R.id.act_view_adj;
import static com.stp.ssm.R.id.btnFormulario;
import static com.stp.ssm.R.id.edtObservacionRel;
import static com.stp.ssm.R.id.lbl_spinne_item;
import static com.stp.ssm.R.id.lbl_time_visita_rel;
import static com.stp.ssm.R.id.lblcontadj_rel;
import static com.stp.ssm.R.id.lblcontcapturas_rel;
import static com.stp.ssm.R.id.spDepartamentoRel;
import static com.stp.ssm.R.id.spDistritosRel;
import static com.stp.ssm.R.id.spLocalidadRel;
import static com.stp.ssm.R.id.spMotivosRel;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_relevamiento;
import static com.stp.ssm.R.layout.spinner_item_1;
import static com.stp.ssm.R.menu;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msg_scan;
import static com.stp.ssm.R.string.dialog_msg_select;
import static com.stp.ssm.R.string.dialog_msj_alert_confir_out;
import static com.stp.ssm.R.string.dialog_take_scan;
import static com.stp.ssm.R.string.dialog_title_alert_confir;
import static com.stp.ssm.R.string.dialog_title_scan;
import static com.stp.ssm.R.string.dialog_title_select;
import static com.stp.ssm.R.string.lbl_archivos;
import static com.stp.ssm.R.string.lbl_img_galeria;
import static com.stp.ssm.R.string.lbl_si;
import static com.stp.ssm.R.string.msj_err_idem;
import static com.stp.ssm.R.string.title_visita;
import static com.stp.ssm.R.string.toast_msj_add_file;
import static com.stp.ssm.R.string.toast_msj_err_select_img;
import static com.stp.ssm.R.string.toast_msj_geo_err;
import static com.stp.ssm.SelectActivity.EX_PATH_RESULT;
import static com.stp.ssm.Util.CellUtils.BorrarFileCapturas2;
import static com.stp.ssm.Util.CellUtils.adjuntarFichero;
import static com.stp.ssm.Util.CellUtils.getAndroidId;
import static com.stp.ssm.Util.CellUtils.hasConnectionInternet;
import static com.stp.ssm.Util.CellUtils.isMyServiceRunning;
import static com.stp.ssm.Util.CellUtils.scanDocumentAndroid;
import static com.stp.ssm.Util.CellUtils.takePhoto2;
import static com.stp.ssm.Util.CellUtils.takeScan;
import static com.stp.ssm.Util.Encriptacion.getStringMessageDigest;
import static com.stp.ssm.Util.FechaUtil.SegundoToHora;
import static com.stp.ssm.Util.FechaUtil.getFechaActual;
import static com.stp.ssm.Util.ImageFileUtil.bitmapToFile;
import static com.stp.ssm.Util.ImageFileUtil.copyImagen;
import static com.stp.ssm.View.ViewFactory.notificacionToast;
import static com.stp.ssm.databases.BDLocalidadesFuntions.getInstance;
import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;

public class VisitaRelevamientoActivity extends BaseActivity {

    private LocationManager locationManager;
    private Proyecto proyecto;
    private Beneficiario beneficiario;
    private Cronometro cron;
    private Handler handler = new Handler();
    private Bundle bundle;
    private Visita visita;

    private ArrayList<Capturas> capturas;
    private ArrayList<String> adjuntos;
    private ArrayList<Motivos> motivos;
    private ArrayList<Departamento> departamentos;
    private ArrayList<Localidad> localidads;

    private TextView lbl_time;
    private TextView lblcontcapturas;
    private TextView lblcontadj;
    private TextView txt_entidad_relevar;
    private EditText edtPrimaryKey;
    private Spinner spDepartamento;
    private Spinner spDistritos;
    private Spinner spLocalidad;
    private Spinner spMotivos;
    private EditText edtObservacion;
    private Button btnFinalizar;
    private Button btnLeerCodigo;

    private boolean isrunning = true;
    private long segundos = 0;
    private int distritoSelect = 0;
    private int deptSelect = 0;
    private int localidadSelect = 0;
    private long idvisitaRestore = 0;

    private final String KEY_SEGUNDOS = "segundos";
    private final String KEY_DEPARTAMENTO = "departamento";
    private final String KEY_DISTRITO = "distrito";
    private final String KEY_LOCALIDAD = "localidad";
    private final String KEY_MOTIVOS = "motivos";
    private final String KEY_OBSERVACION = "observacion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_relevamiento);
        setTitle(getString(title_visita));

        inicializar();
        //eventBus.register(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        capturas = new ArrayList<>();
        adjuntos = new ArrayList<>();

        lbl_time = (TextView) findViewById(lbl_time_visita_rel);
        lblcontcapturas = (TextView) findViewById(lblcontcapturas_rel);
        lblcontadj = (TextView) findViewById(lblcontadj_rel);
        txt_entidad_relevar = (TextView) findViewById(id.txt_entidad_relevar);
        edtPrimaryKey = (EditText) findViewById(id.edtPrimaryKey);
        spDepartamento = (Spinner) findViewById(spDepartamentoRel);
        spDistritos = (Spinner) findViewById(spDistritosRel);
        spLocalidad = (Spinner) findViewById(spLocalidadRel);
        spMotivos = (Spinner) findViewById(spMotivosRel);
        edtObservacion = (EditText) findViewById(edtObservacionRel);
        btnFinalizar = (Button) findViewById(btnFormulario);
        btnLeerCodigo = (Button) findViewById(id.btnLeerCodigo);

        asignarEventos();
        cargarDatos();
        inicializarCronometro();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu.mn_formulario, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case act_adj:
                DialogAcpView selecAdjunto = new DialogAcpView(getString(dialog_title_select),
                        getString(dialog_msg_select),
                        getString(lbl_archivos),
                        getString(lbl_img_galeria));
                selecAdjunto.setOnClickDialogListener(new OnClickDialogListener() {
                    @Override
                    public void OnPositiveClick(DialogInterface dialog, String tag) {
                        adjuntarFichero(VisitaRelevamientoActivity.this);
                    }

                    @Override
                    public void OnNegativeClick(DialogInterface dialog, String tag) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        if (SDK_INT >= 18) {
                            intent.putExtra(EXTRA_ALLOW_MULTIPLE, true);
                        }
                        intent.setAction(ACTION_GET_CONTENT);
                        startActivityForResult(createChooser(intent, "Select Picture"), 800);
                    }
                });
                selecAdjunto.show(getSupportFragmentManager(), "");

                break;
            case act_view_adj:
                DialogLstAdjuntos dialog = new DialogLstAdjuntos(adjuntos);
                dialog.setOnDeleteListener(new OnDeleteListener() {
                    @Override
                    public void OnDelete(int position) {
                        lblcontadj.setText(Integer.toString(adjuntos.size()));
                    }
                });
                dialog.show(getSupportFragmentManager(), "");
                break;
            case act_camera:
                takePhoto2(this, capturas);
                break;
            case act_img:
                new DialogImagenes2(capturas).show(getSupportFragmentManager(), "");
                break;
            case act_scanner:
                DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_scan),
                        getString(dialog_msg_scan),
                        getString(lbl_si),
                        getString(dialog_take_scan));
                dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
                    @Override
                    public void OnPositiveClick(DialogInterface dialog, String tag) {
                        takeScan(VisitaRelevamientoActivity.this);
                    }

                    @Override
                    public void OnNegativeClick(DialogInterface dialog, String tag) {
                        takePhoto2(VisitaRelevamientoActivity.this, capturas);
                    }
                });
                dialogAcpView.show(getSupportFragmentManager(), "");
                break;
        }
        return true;
    }

    private void cargarDatos() {
        proyecto = (Proyecto) getIntent().getExtras().getSerializable("proyecto");
        motivos = dbFuntions.getMotivos(parseInt(proyecto.getCodigo()));
        spMotivos.setAdapter(new ArrayAdapter<Motivos>(getApplicationContext(),
                spinner_item_1,
                lbl_spinne_item,
                motivos));

        departamentos = dbFuntions.getArrayDepartametos();
        spDepartamento.setAdapter(new ArrayAdapter<Departamento>(this, spinner_item_1,
                lbl_spinne_item,
                departamentos));
        bundle = getIntent().getExtras();

        switch (bundle.getInt("tipovisita")) {
            case 0:
                visita = new Visita(getFechaActual(), VISITA_ASIGNADO);
                beneficiario = (Beneficiario) bundle.getSerializable("beneficiario");
                //cargarBeneficiario(beneficiario);
                break;
            case 1:
                visita = new Visita(getFechaActual(), VISITA_NUEVO_BENEFICIARIO);
                spDepartamento.setVisibility(VISIBLE);
                spDistritos.setVisibility(VISIBLE);
                break;
            case 2:
                visita = new Visita(getFechaActual(), VISITA_SIN_BENEFICIARIO);
                break;
            case 3:
                visita = new Visita(getFechaActual(), VISITA_CORREGIR);
                idvisitaRestore = bundle.getLong("idvisitarestore");
                beneficiario = (Beneficiario) bundle.getSerializable("beneficiario");
                cargarBeneficiario(beneficiario);
                Motivos motivo = (Motivos) bundle.getSerializable("motivo");
                for (int i = 0; i < motivos.size(); i++) {
                    if (motivos.get(i).getCodmotivo() == motivo.getCodmotivo()) {
                        spMotivos.setSelection(i);
                        break;
                    }
                }
                spMotivos.setEnabled(false);
                //capturas = bundle.getStringArrayList("capturas");
                capturas = bundle.getParcelableArrayList("capturas");
                adjuntos = bundle.getStringArrayList("adjuntos");
                edtObservacion.setText(bundle.getString("informe"));
                break;
        }

        txt_entidad_relevar.setText(bundle.getString("entidad_relevar"));
    }

    private void asignarEventos() {
        spDepartamento.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deptSelect = parseInt(departamentos.get(position).getCodigo());
                spDistritos.setAdapter(new ArrayAdapter<Distrito>(VisitaRelevamientoActivity.this,
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
                localidads = getInstance(getApplicationContext()).getLocalidadByDistrito(parseInt(departamentos.get(spDepartamento.getSelectedItemPosition()).getCodigo()), distritoSelect);
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

        btnLeerCodigo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getApplicationContext(), ReadBarcoderActivity.class), 900);
            }
        });
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
                        lbl_time.setText(SegundoToHora(segundos));
                    }
                }, 100);
            }
        });
        cron.inicializar(segundos);
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
        if (edtPrimaryKey.getText().toString().equals("")) {
            edtPrimaryKey.setError(getString(msj_err_idem));
            return false;
        }

        /*if(existeDocumento()){
            ViewFactory.notificacionToast(getApplicationContext(),getString(R.string.toast_msj_beneficiario_duplicado));
            return false;
        }*/

        if (coordenadas == null || coordenadas.getPrecision() > 30) {
            startActivityForResult(new Intent(getApplicationContext(), CalibrarLocationActivity.class), 300);
            return false;
        }
        return true;
    }

    private void guardarRelevamiento(Coordenadas coordenadas) {
        String documento = edtPrimaryKey.getText().toString();
        String observacion = edtObservacion.getText().toString();
        Estado estado = NUEVO_NO_VALIDADO;
        TipoBeneficiario tipo = ENTIDAD_NO_INSTITUCION;
        ///Nueva Instancia de Beneficiario
        beneficiario = new Beneficiario("", "", documento, sessionData.getUsuario(), proyecto.getCodigo(), tipo, distritoSelect, deptSelect, estado, false);
        beneficiario.setCoordenadas(coordenadas);
        beneficiario.setLocalidad(localidadSelect);
        ///Agregar Beneficiario en la base de datos
        dbFuntions.addBeneficiario(beneficiario);
        ///Setear datos en la visita
        visita.setBeneficiario(beneficiario);
        visita.setHorafin(getFechaActual());
        visita.setCodmotivo(motivos.get(spMotivos.getSelectedItemPosition()).getCodmotivo());
        visita.setCoordenadas(coordenadas);
        visita.setProyecto(parseInt(proyecto.getCodigo()));
        visita.setObservacion(observacion);
        visita.setTiempo(segundos);
        if (motivos.get(spMotivos.getSelectedItemPosition()).hasFormulario()) {
            visita.setHasFormulario();
        }
        //Agregar Visita a la base de datos y recuperar su id
        String id_key_visita = sessionData.getUsuario() + "_" + currentTimeMillis() + "_" + getAndroidId(getApplicationContext());
        id_key_visita = getStringMessageDigest(id_key_visita);
        visita.setId_key(id_key_visita);
        long idvisita = dbFuntions.addVisita(visita, sessionData.getUsuario(), beneficiario);
        visita.setIdvisita(idvisita);
        sessionData.setLastIdVisita(idvisita);
        ///Si finalizar
        if (!motivos.get(spMotivos.getSelectedItemPosition()).hasFormulario()) {
            dbFuntions.addAdjuntos(adjuntos, idvisita, 0);
            dbFuntions.addCapturas2(capturas, idvisita, beneficiario.getDocumento(), 0);
        }
    }

    private void saltarActivity(Coordenadas coordenadas) {
        guardarRelevamiento(coordenadas);
        if (motivos.get(spMotivos.getSelectedItemPosition()).hasFormulario()) {
            Bundle bundle = new Bundle();
            if (visita.getTipo().equals(VISITA_CORREGIR)) {
                bundle.putSerializable("motivo", motivos.get(spMotivos.getSelectedItemPosition()));
                bundle.putSerializable("proyecto", proyecto);
                bundle.putString("beneficiario", beneficiario.getDocumento());
                //bundle.putStringArrayList("capturas", capturas);
                bundle.putParcelableArrayList("capturas", capturas);
                bundle.putStringArrayList("adjuntos", adjuntos);
                bundle.putLong("idvisita", visita.getIdvisita());
                bundle.putLong("idvisitarestore", idvisitaRestore);
                bundle.putBoolean("recuperar", true);
                bundle.putInt("tipo_visita", visita.getTipo().getCodigo());
            } else {
                bundle.putSerializable("motivo", motivos.get(spMotivos.getSelectedItemPosition()));
                bundle.putSerializable("proyecto", proyecto);
                bundle.putLong("idvisita", visita.getIdvisita());
                bundle.putString("beneficiario", beneficiario.getDocumento());
                bundle.putBoolean("recuperar", false);
                bundle.putLong("segundos", segundos);
                bundle.putInt("tipo_visita", visita.getTipo().getCodigo());
                bundle.putInt("tipo_destinatario", ENTIDAD_NO_INSTITUCION.getCodigo());
            }
            dbFuntions.addCapturas2(capturas, visita.getIdvisita(), beneficiario.getDocumento(), 2);
            dbFuntions.addAdjuntos(adjuntos, visita.getIdvisita(), 2);
            //nuevo activity
            Intent intent = new Intent(getApplicationContext(), FormularioActivity.class).putExtras(bundle);
            startActivityForResult(intent, 400);
        } else {
            isrunning = false;
            enviarDatos();
            finish();
        }
    }

    private void enviarDatos() {
        if (hasConnectionInternet(getApplicationContext()) && !isMyServiceRunning(SendDataService2.class, getApplicationContext())) {
            startService(new Intent(getApplicationContext(), SendDataService2.class));
        }
    }

    private void limpiarBase(Visita visita, Beneficiario beneficiario) {
        dbFuntions.borrarAdjuntosByVisita(visita.getIdvisita());
        dbFuntions.borrarCapturasByVisita(visita.getIdvisita());
        BorrarFileCapturas2(capturas);

        if (visita.getTipo().getCodigo() == VISITA_NUEVO_BENEFICIARIO.getCodigo()) {
            if (beneficiario != null) {
                dbFuntions.borrarBeneficiarioByCed(beneficiario.getDocumento());
            }
        }
        ///Se borra una visita por su Id
        dbFuntions.borrarVisitaById(visita.getIdvisita());
    }

    private void addAdjunto(String path) {
        adjuntos.add(path);
        notificacionToast(getApplicationContext(), getString(toast_msj_add_file));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    addAdjunto(data.getStringExtra(EX_PATH_RESULT));
                    lblcontadj.setText(Integer.toString(adjuntos.size()));
                }
                break;
            case 200:
                if (resultCode == RESULT_CANCELED) {
                    capturas.remove(capturas.size() - 1);
                }
                lblcontcapturas.setText(Integer.toString(capturas.size()));
                break;
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
                    isrunning = false;
                    sessionData.setLastIdVisita(0);
                    sessionData.setLastTimeVisita(0);
                    if (visita.getTipo().equals(VISITA_CORREGIR)) {
                        dbFuntions.updateRelavamientoCorrecto(idvisitaRestore, 0);
                        dbFuntions.insertarCorreccion(getIntent().getExtras().getString("id_key_rel"), visita.getId_key());
                    }
                    enviarDatos();
                    finish();
                } else {
                    dbFuntions.borrarAdjuntosByVisita(visita.getIdvisita());
                    dbFuntions.borrarCapturasByVisita(visita.getIdvisita());
                    if (visita.getTipo().getCodigo() == VISITA_NUEVO_BENEFICIARIO.getCodigo()) {
                        if (beneficiario != null) {
                            dbFuntions.borrarBeneficiarioByCed(beneficiario.getDocumento());
                        }
                    }
                    dbFuntions.borrarVisitaById(visita.getIdvisita());
                }
                break;
            case 500:
                if (resultCode == RESULT_OK) {
                    String path = capturas.get(capturas.size() - 1).getPath();
                    scanDocumentAndroid(this, path);
                } else {
                    capturas.remove(capturas.size() - 1);
                }
                break;
            case 700:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getExtras().getParcelable(SCANNED_RESULT);
                    Bitmap bitmap = null;
                    try {
                        bitmap = getBitmap(getContentResolver(), uri);
                        getContentResolver().delete(uri, null, null);
                        String path = bitmapToFile(bitmap, edtPrimaryKey.getText().toString(), capturas.size());
                        capturas.add(new Capturas(path, CAMARA.getCodigo()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 800:
                if (resultCode == RESULT_OK) {
                    String picturePath;
                    Uri selectedImageUri;
                    if (SDK_INT >= 18) {
                        if (data.getClipData() != null) {
                            ClipData clipData = data.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Item item = clipData.getItemAt(i);
                                selectedImageUri = item.getUri();
                                picturePath = getRealPathFromURI(getApplicationContext(), selectedImageUri);
                                capturas.add(new Capturas(picturePath, GALERIA.getCodigo()));
                                lblcontcapturas.setText(Integer.toString(capturas.size()));
                            }
                        } else {
                            if (data != null) {
                                selectedImageUri = data.getData();
                                picturePath = getRealPathFromURI(getApplicationContext(), selectedImageUri);
                                if (picturePath != null && !picturePath.equals("")) {
                                    new AsyncTask<String, Void, Void>() {
                                        @Override
                                        protected Void doInBackground(String... params) {
                                            capturas.add(new Capturas(copyImagen(params[0]), GALERIA.getCodigo()));
                                            return null;
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);
                                            lblcontcapturas.setText(Integer.toString(capturas.size()));
                                        }
                                    }.execute(picturePath);
                                } else {
                                    notificacionToast(getApplicationContext(), getString(toast_msj_err_select_img));
                                }
                            } else {
                                notificacionToast(getApplicationContext(), getString(toast_msj_err_select_img));
                            }
                        }
                    } else {
                        if (data != null) {
                            selectedImageUri = data.getData();
                            picturePath = getRealPathFromURI(getApplicationContext(), selectedImageUri);
                            if (picturePath != null && !picturePath.equals("")) {
                                capturas.add(new Capturas(picturePath, GALERIA.getCodigo()));
                                lblcontcapturas.setText(Integer.toString(capturas.size()));
                            } else {
                                notificacionToast(getApplicationContext(), getString(toast_msj_err_select_img));
                            }
                        } else {
                            notificacionToast(getApplicationContext(), getString(toast_msj_err_select_img));
                        }
                    }
                }
                break;
            case 900:
                if (resultCode == RESULT_OK) {
                    edtPrimaryKey.setText(data.getExtras().getString("barcoder"));
                }
                break;
        }
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(DATA);
            if (cursor.moveToFirst()) {
                String result = cursor.getString(column_index);
                if (result == null) {
                    result = contentUri.getPath();
                    String id = result.split(":")[1];
                    String sel = _ID + "=?";
                    cursor = getContentResolver().query(EXTERNAL_CONTENT_URI, proj, sel, new String[]{id}, null);
                    column_index = cursor.getColumnIndex(proj[0]);
                    if (cursor.moveToFirst()) {
                        result = cursor.getString(column_index);
                    }
                }
                return result;
            } else {
                return "";
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void cargarBeneficiario(Beneficiario beneficiario) {
        edtPrimaryKey.setText(beneficiario.getDocumento());
        deptSelect = beneficiario.getDepartamento();
        distritoSelect = beneficiario.getDistrito();
        localidadSelect = beneficiario.getLocalidad();
        seleccionarDpto(deptSelect);
        seleccionarDistrito(distritoSelect);
        seleccionarLocalidad(localidadSelect);
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
        localidads = getInstance(getApplicationContext()).getLocalidadByDistrito(parseInt(departamentos.get(spDepartamento.getSelectedItemPosition()).getCodigo()), distritoSelect);
        for (int i = 0; i < localidads.size(); i++) {
            if (localidads.get(i).getCodigo() == cod_localidad) {
                spLocalidad.setSelection(i);
                break;
            }
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
                isrunning = false;
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
    protected void onDestroy() {
        if (isrunning) {
            dbFuntions.addCapturas2(capturas, 0, "", 2);
            dbFuntions.addAdjuntos(adjuntos, 0, 2);
        }
        cron.finalizar();
        super.onDestroy();
    }
}