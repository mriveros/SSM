package com.stp.ssm;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.scanlibrary.ScanConstants;
import com.stp.ssm.Evt.ValidIdentificacionesEvt;
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
import com.stp.ssm.http.HttpVolleyRequest;
import com.stp.ssm.Util.ImageFileUtil;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.View.DialogAlertSimple;
import com.stp.ssm.View.DialogImagenes2;
import com.stp.ssm.View.DialogLstAdjuntos;
import com.stp.ssm.View.ViewFactory;
import com.stp.ssm.databases.BDLocalidadesFuntions;
import com.stp.ssm.http.URLs;

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
import static android.provider.MediaStore.Images;
import static android.provider.MediaStore.Images.Media;
import static android.provider.MediaStore.Images.Media.getBitmap;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.widget.AdapterView.OnItemSelectedListener;
import static android.widget.CompoundButton.OnCheckedChangeListener;
import static com.scanlibrary.ScanConstants.SCANNED_RESULT;
import static com.stp.ssm.Model.Beneficiario.Estado;
import static com.stp.ssm.Model.Beneficiario.Estado.NUEVO_NO_VALIDADO;
import static com.stp.ssm.Model.Beneficiario.TipoBeneficiario;
import static com.stp.ssm.Model.Beneficiario.TipoBeneficiario.findByCodigo;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN.CAMARA;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN.GALERIA;
import static com.stp.ssm.Model.Visita.TipoVisita;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_ASIGNADO;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_CORREGIR;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_NUEVO_BENEFICIARIO;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_SIN_BENEFICIARIO;
import static com.stp.ssm.R.array;
import static com.stp.ssm.R.array.array_tipo_beneficiario;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.act_adj;
import static com.stp.ssm.R.id.act_camera;
import static com.stp.ssm.R.id.act_img;
import static com.stp.ssm.R.id.act_scanner;
import static com.stp.ssm.R.id.act_view_adj;
import static com.stp.ssm.R.id.lbl_spinne_item;
import static com.stp.ssm.R.id.lbl_time_visita;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_visita;
import static com.stp.ssm.R.layout.spinner_item_1;
import static com.stp.ssm.R.menu;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msg_scan;
import static com.stp.ssm.R.string.dialog_msg_select;
import static com.stp.ssm.R.string.dialog_msg_validando;
import static com.stp.ssm.R.string.dialog_msj_alert_confir_out;
import static com.stp.ssm.R.string.dialog_take_scan;
import static com.stp.ssm.R.string.dialog_title_alert_capt;
import static com.stp.ssm.R.string.dialog_title_alert_confir;
import static com.stp.ssm.R.string.dialog_title_scan;
import static com.stp.ssm.R.string.dialog_title_select;
import static com.stp.ssm.R.string.dialog_title_validando;
import static com.stp.ssm.R.string.lbl_1er_nombre;
import static com.stp.ssm.R.string.lbl_Nom_dist;
import static com.stp.ssm.R.string.lbl_archivos;
import static com.stp.ssm.R.string.lbl_continuar;
import static com.stp.ssm.R.string.lbl_denominacion;
import static com.stp.ssm.R.string.lbl_documento;
import static com.stp.ssm.R.string.lbl_entidad;
import static com.stp.ssm.R.string.lbl_finalizar;
import static com.stp.ssm.R.string.lbl_id;
import static com.stp.ssm.R.string.lbl_img_galeria;
import static com.stp.ssm.R.string.lbl_nombre;
import static com.stp.ssm.R.string.lbl_ruc;
import static com.stp.ssm.R.string.lbl_si;
import static com.stp.ssm.R.string.msj_err_apellido;
import static com.stp.ssm.R.string.msj_err_div;
import static com.stp.ssm.R.string.msj_err_documento;
import static com.stp.ssm.R.string.msj_err_nombre;
import static com.stp.ssm.R.string.title_visita;
import static com.stp.ssm.R.string.toast_msj_add_file;
import static com.stp.ssm.R.string.toast_msj_beneficiario_duplicado;
import static com.stp.ssm.R.string.toast_msj_err_select_img;
import static com.stp.ssm.R.string.toast_msj_err_sin_ced;
import static com.stp.ssm.R.string.toast_msj_geo_err;
import static com.stp.ssm.R.string.toast_msj_sin_con;
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
import static com.stp.ssm.Util.FechaUtil.getFechaCadena;
import static com.stp.ssm.Util.ImageFileUtil.bitmapToFile;
import static com.stp.ssm.Util.ImageFileUtil.copyImagen;
import static com.stp.ssm.Util.ImageFileUtil.getRealPathFromURI;
import static com.stp.ssm.View.ViewFactory.notificacionToast;
import static com.stp.ssm.databases.BDLocalidadesFuntions.getInstance;
import static com.stp.ssm.http.URLs.URL_INDENTIFICACIONES;
import static java.lang.Integer.parseInt;
import static java.lang.System.currentTimeMillis;

public class VisitaActivity extends BaseActivity {

    private Visita visita;
    private Beneficiario beneficiario;
    private Proyecto proyecto;
    private Cronometro cron;

    private ArrayList<Capturas> capturas;
    private ArrayList<String> adjuntos;
    private ArrayList<Motivos> motivos;
    private ArrayList<Departamento> departamentos;

    private boolean isrunning = true;
    private long segundos = 0;
    private int distritoSelect = 0;
    private int deptSelect = 0;
    private int localidadSelect = 0;
    private long idvisitaRestore = 0;
    private ArrayList<Localidad> localidads;

    private Bundle bundle;
    private LocationManager locationManager;
    private Handler handler = new Handler();
    private Spinner spTipoBeneficiario;
    private Spinner spMotivos;
    private Spinner spDepartamento;
    private Spinner spDistritos;
    private Spinner spLocalidad;
    private EditText edtNombre;
    private EditText edtNombre1;
    private EditText edtApellido;
    private EditText edtApellido1;
    private EditText edtCedRuc;
    private EditText edtRucDiv;
    private EditText edtObservacion;
    private Button btnFinalizar;
    private ToggleButton tgbtnSinCed;
    private TextView lblNombre;
    private TextView lblApellido;
    private TextView lblCedRuc;
    private TextView lbl_time;
    private TextView lbl_separador;
    private TextView lbldepartamento;
    private TextView lbldistrito;
    private TextView lblcontcapturas;
    private TextView lblcontadj;
    private CheckBox chJefeFamilia;
    private Button btnVerif;
    private TextView lbllocalidad;

    private final String KEY_SEGUNDOS = "segundos";
    private final String KEY_DEPARTAMENTO = "departamento";
    private final String KEY_DISTRITO = "distrito";
    private final String KEY_LOCALIDAD = "localidad";
    private final String KEY_MOTIVOS = "motivos";
    private final String KEY_OBSERVACION = "observacion";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_visita);
        setTitle(getString(title_visita));

        inicializar();
        eventBus.register(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        capturas = new ArrayList<>();
        adjuntos = new ArrayList<>();
        spTipoBeneficiario = (Spinner) findViewById(id.spTipoBeneficiario);
        spTipoBeneficiario.setAdapter(new ArrayAdapter<String>(this, spinner_item_1,
                lbl_spinne_item,
                getResources().getStringArray(array_tipo_beneficiario)));
        spMotivos = (Spinner) findViewById(id.spMotivos);
        spDepartamento = (Spinner) findViewById(id.spDepartamento);
        spDistritos = (Spinner) findViewById(id.spDistritos);
        spLocalidad = (Spinner) findViewById(id.spLocalidad);
        lblNombre = (TextView) findViewById(id.lblNombre);
        lblApellido = (TextView) findViewById(id.lblApellido);
        lblCedRuc = (TextView) findViewById(id.lblCedRuc);
        edtNombre = (EditText) findViewById(id.edtNombre);
        edtNombre1 = (EditText) findViewById(id.edtNombre1);
        edtApellido = (EditText) findViewById(id.edtApellido);
        edtApellido1 = (EditText) findViewById(id.edtApellido1);
        edtCedRuc = (EditText) findViewById(id.edtCedRuc);
        edtRucDiv = (EditText) findViewById(id.edtRucDiv);
        edtObservacion = (EditText) findViewById(id.edtObservacion);
        btnFinalizar = (Button) findViewById(id.btnFinalizar);
        tgbtnSinCed = (ToggleButton) findViewById(id.tgbtnSinCed);
        lbl_time = (TextView) findViewById(lbl_time_visita);
        lbl_separador = (TextView) findViewById(id.lbl_separador);
        lbldepartamento = (TextView) findViewById(id.lbldepartamento);
        lbldistrito = (TextView) findViewById(id.lbldistrito);
        lblcontcapturas = (TextView) findViewById(id.lblcontcapturas);
        lblcontadj = (TextView) findViewById(id.lblcontadj);
        chJefeFamilia = (CheckBox) findViewById(id.chJefeFamilia);
        btnVerif = (Button) findViewById(id.btnVerif);
        lbllocalidad = (TextView) findViewById(id.lbllocalidad);

        asignarEventos();
        cargarDatos();

        if (savedInstanceState != null) {
            segundos = savedInstanceState.getLong(KEY_SEGUNDOS);
            spDepartamento.setSelection(savedInstanceState.getInt(KEY_DEPARTAMENTO));
            spDistritos.setSelection(savedInstanceState.getInt(KEY_DISTRITO));
            spLocalidad.setSelection(savedInstanceState.getInt(KEY_LOCALIDAD));
            spMotivos.setSelection(savedInstanceState.getInt(KEY_MOTIVOS));
            edtObservacion.setText(savedInstanceState.getString(KEY_OBSERVACION));
        }
        inicializarCronometro();
    }

    @Override
    protected void onStart() {
        restaurarDatos();
        super.onStart();
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
                        adjuntarFichero(VisitaActivity.this);
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
                        takeScan(VisitaActivity.this);
                    }

                    @Override
                    public void OnNegativeClick(DialogInterface dialog, String tag) {
                        takePhoto2(VisitaActivity.this, capturas);
                    }
                });
                dialogAcpView.show(getSupportFragmentManager(), "");
                break;
        }
        return true;
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
                cargarBeneficiario(beneficiario);
                break;
            case 1:
                visita = new Visita(getFechaActual(), VISITA_NUEVO_BENEFICIARIO);
                lbldepartamento.setVisibility(VISIBLE);
                spDepartamento.setVisibility(VISIBLE);
                lbldistrito.setVisibility(VISIBLE);
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
                capturas = bundle.getParcelableArrayList("capturas");
                adjuntos = bundle.getStringArrayList("adjuntos");
                edtObservacion.setText(bundle.getString("informe"));
                break;
        }
    }

    private void cargarBeneficiario(Beneficiario beneficiario) {
        spTipoBeneficiario.setSelection(beneficiario.getTipo().getCodigo());
        spTipoBeneficiario.setEnabled(false);

        edtNombre.setText(beneficiario.getPrimerNombre());
        edtNombre1.setText(beneficiario.getSegundoNombre());
        edtApellido.setText(beneficiario.getPrimerApellido());
        edtApellido1.setText(beneficiario.getSegundoApellido());

        if (beneficiario.getTipo().getCodigo() == 0 || beneficiario.getTipo().getCodigo() == 2) {
            edtCedRuc.setText(beneficiario.getDocumento());
        } else {
            String[] parts = beneficiario.getDocumento().split("-");
            edtCedRuc.setText(parts[0]);
            edtRucDiv.setText(parts[1]);
            edtRucDiv.setEnabled(false);
        }

        edtCedRuc.setEnabled(false);
        tgbtnSinCed.setEnabled(false);

        chJefeFamilia.setChecked(beneficiario.isJefe());

        deptSelect = beneficiario.getDepartamento();
        distritoSelect = beneficiario.getDistrito();
        localidadSelect = beneficiario.getLocalidad();

        seleccionarDpto(deptSelect);
        seleccionarDistrito(distritoSelect);
        seleccionarLocalidad(localidadSelect);
    }


    private void asignarEventos() {
        spTipoBeneficiario.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        lblCedRuc.setText(getString(lbl_documento));
                        edtRucDiv.setVisibility(GONE);
                        lbl_separador.setVisibility(GONE);
                        tgbtnSinCed.setVisibility(VISIBLE);
                        btnVerif.setVisibility(VISIBLE);
                        lblNombre.setText(getString(lbl_nombre));
                        edtNombre.setHint(getString(lbl_1er_nombre));
                        edtApellido.setVisibility(VISIBLE);
                        edtNombre1.setVisibility(VISIBLE);
                        edtApellido1.setVisibility(VISIBLE);
                        chJefeFamilia.setVisibility(VISIBLE);
                        lblApellido.setVisibility(VISIBLE);
                        break;
                    case 1:
                        lblCedRuc.setText(getString(lbl_ruc));
                        edtRucDiv.setVisibility(VISIBLE);
                        lbl_separador.setVisibility(VISIBLE);
                        tgbtnSinCed.setVisibility(GONE);
                        btnVerif.setVisibility(GONE);
                        lblNombre.setText(getString(lbl_entidad));
                        edtNombre.setHint("");
                        edtApellido.setVisibility(GONE);
                        edtNombre1.setVisibility(GONE);
                        edtApellido1.setVisibility(GONE);
                        chJefeFamilia.setVisibility(GONE);
                        lblApellido.setVisibility(GONE);
                        break;
                    case 2:
                        lblCedRuc.setText(getString(lbl_id));
                        edtRucDiv.setVisibility(GONE);
                        lbl_separador.setVisibility(GONE);
                        tgbtnSinCed.setVisibility(GONE);
                        btnVerif.setVisibility(GONE);
                        lblNombre.setText(getString(lbl_denominacion));
                        edtNombre.setHint("");
                        edtApellido.setVisibility(GONE);
                        edtNombre1.setVisibility(GONE);
                        edtApellido1.setVisibility(GONE);
                        chJefeFamilia.setVisibility(GONE);
                        lblApellido.setVisibility(GONE);
                        break;
                    case 3:
                        lblCedRuc.setText(getString(lbl_ruc));
                        edtRucDiv.setVisibility(VISIBLE);
                        lbl_separador.setVisibility(VISIBLE);
                        tgbtnSinCed.setVisibility(GONE);
                        btnVerif.setVisibility(GONE);
                        lblNombre.setText(getString(lbl_Nom_dist));
                        edtNombre.setHint(getString(lbl_nombre));
                        edtNombre.setVisibility(VISIBLE);
                        edtApellido.setVisibility(GONE);
                        edtNombre1.setVisibility(GONE);
                        edtApellido1.setVisibility(GONE);
                        chJefeFamilia.setVisibility(GONE);
                        lblApellido.setVisibility(GONE);
                        lbldepartamento.setVisibility(GONE);
                        spDepartamento.setVisibility(GONE);
                        lbldistrito.setVisibility(GONE);
                        spDistritos.setVisibility(GONE);
                        lbllocalidad.setVisibility(GONE);
                        spLocalidad.setVisibility(GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spMotivos.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (visita.getTipo().equals(VISITA_NUEVO_BENEFICIARIO) && tgbtnSinCed.isChecked()) {
                    generarCodDocumento();
                }
                if (motivos.get(position).hasFormulario()) {
                    btnFinalizar.setText(getString(lbl_continuar));
                } else {
                    btnFinalizar.setText(getString(lbl_finalizar));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /*Queres saber algo Dios No existe*/
        spDepartamento.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deptSelect = parseInt(departamentos.get(position).getCodigo());
                spDistritos.setAdapter(new ArrayAdapter<Distrito>(VisitaActivity.this,
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
                        HttpVolleyRequest.getInstance(getApplicationContext()).validarCedula(request);
                    } else {
                        notificacionToast(getApplicationContext(), getString(toast_msj_err_sin_ced));
                    }
                } else {
                    notificacionToast(getApplicationContext(), getString(toast_msj_sin_con));
                }
            }
        });
    }


    private void saltarActivity(Coordenadas coordenadas) {
        guardarRelevamiento(coordenadas);
        if (motivos.get(spMotivos.getSelectedItemPosition()).hasFormulario()) {

            Bundle bundle = new Bundle();
            if (visita.getTipo().equals(VISITA_CORREGIR)) {
                bundle.putSerializable("motivo", motivos.get(spMotivos.getSelectedItemPosition()));
                bundle.putSerializable("proyecto", proyecto);
                bundle.putString("beneficiario", beneficiario.getDocumento());
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
                bundle.putInt("tipo_destinatario", spTipoBeneficiario.getSelectedItemPosition());
            }

            dbFuntions.addCapturas2(capturas, visita.getIdvisita(), beneficiario.getDocumento(), 2);
            dbFuntions.addAdjuntos(adjuntos, visita.getIdvisita(), 2);


            Intent intent = new Intent(getApplicationContext(), FormularioActivity.class).putExtras(bundle);
            startActivityForResult(intent, 400);
        } else {
            isrunning = false;
            enviarDatos();
            finish();
        }
    }


    private void addAdjunto(String path) {
        adjuntos.add(path);
        notificacionToast(getApplicationContext(), getString(toast_msj_add_file));
    }


    private void guardarRelevamiento(Coordenadas coordenadas) {
        String nombre = edtNombre.getText().toString() + "#" + edtNombre1.getText().toString();
        String apellido = edtApellido.getText().toString() + "#" + edtApellido1.getText().toString();
        String observacion = edtObservacion.getText().toString();
        String documento = "";

        switch (spTipoBeneficiario.getSelectedItemPosition()) {
            case 0:
            case 2:
                documento = edtCedRuc.getText().toString();
                break;
            case 1:
            case 3:
                documento = edtCedRuc.getText().toString() + "-" + edtRucDiv.getText().toString();
                break;
        }
        Estado estado;
        if (visita.getTipo().equals(VISITA_ASIGNADO)) {
            estado = beneficiario.getEstado();
        } else {
            estado = NUEVO_NO_VALIDADO;
        }
        TipoBeneficiario tipo = findByCodigo(spTipoBeneficiario.getSelectedItemPosition());

        beneficiario = new Beneficiario(nombre, apellido, documento, sessionData.getUsuario(), proyecto.getCodigo(), tipo, distritoSelect, deptSelect, estado, chJefeFamilia.isChecked());
        beneficiario.setCoordenadas(coordenadas);
        beneficiario.setLocalidad(localidadSelect);
        dbFuntions.addBeneficiario(beneficiario);

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


        String id_key_visita = sessionData.getUsuario() + "_" + currentTimeMillis() + "_" + getAndroidId(getApplicationContext());
        id_key_visita = getStringMessageDigest(id_key_visita);
        visita.setId_key(id_key_visita);
        long idvisita = dbFuntions.addVisita(visita, sessionData.getUsuario(), beneficiario);
        visita.setIdvisita(idvisita);
        sessionData.setLastIdVisita(idvisita);

        if (!motivos.get(spMotivos.getSelectedItemPosition()).hasFormulario()) {
            dbFuntions.addAdjuntos(adjuntos, idvisita, 0);
            dbFuntions.addCapturas2(capturas, idvisita, beneficiario.getDocumento(), 0);
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


        if (spTipoBeneficiario.getSelectedItemPosition() == 1) {
            if (edtRucDiv.getText().toString().equals("")) {
                edtRucDiv.setError(getString(msj_err_div));
                return false;
            }
        }

        if (!motivos.get(spMotivos.getSelectedItemPosition()).hasFormulario()) {
            if (visita.getTipo().equals(VISITA_NUEVO_BENEFICIARIO) && spTipoBeneficiario.getSelectedItemPosition() == 0) {
                if (capturas.size() < proyecto.getCant_min_img()) {
                    new DialogAlertSimple(getString(dialog_title_alert_capt), "Debe agregar al menos " + proyecto.getCant_min_img() + " capturas del Destinatario").show(getSupportFragmentManager(), "");
                    return false;
                }
            }

            if (spTipoBeneficiario.getSelectedItemPosition() == 1) {
                if (capturas.size() < proyecto.getCant_min_img()) {
                    new DialogAlertSimple(getString(dialog_title_alert_capt), "Debe agregar al menos " + proyecto.getCant_min_img() + " evidencias de la Actividad").show(getSupportFragmentManager(), "");
                    return false;
                }
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
            if (spTipoBeneficiario.getSelectedItemPosition() == 0) {
                documento = edtCedRuc.getText().toString();
            } else {
                documento = edtCedRuc.getText().toString() + "-" + edtRucDiv.getText().toString();
            }
            return dbFuntions.isBeneficiarioExists(documento);
        }
        return false;
    }


    @Override
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
                    //String path = capturas.get(capturas.size()-1);
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
                        String path = bitmapToFile(bitmap, edtCedRuc.getText().toString(), capturas.size());
                        //capturas.add(path);
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
                                //capturas.add(picturePath);
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
                                            //capturas.add(ImageFileUtil.copyImagen(params[0]));
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
                                //capturas.add(picturePath);
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

        dbFuntions.borrarVisitaById(visita.getIdvisita());
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

    private void restaurarDatos() {
        ArrayList<Capturas> restoreCap = dbFuntions.getCapturas(0);
        if (!restoreCap.isEmpty()) {
            capturas = restoreCap;
            dbFuntions.borrarCapturasByVisita(0);
        }

        ArrayList<String> restoreAdj = dbFuntions.getAdjuntos(0);
        if (!restoreCap.isEmpty()) {
            adjuntos = restoreAdj;
            dbFuntions.borrarAdjuntosByVisita(0);
        }
        lblcontcapturas.setText(Integer.toString(capturas.size()));
        lblcontadj.setText(Integer.toString(adjuntos.size()));
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


    private void enviarDatos() {
        if (hasConnectionInternet(getApplicationContext()) && !isMyServiceRunning(SendDataService2.class, getApplicationContext())) {
            startService(new Intent(getApplicationContext(), SendDataService2.class));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(KEY_SEGUNDOS, segundos);
        outState.putInt(KEY_DEPARTAMENTO, spDepartamento.getSelectedItemPosition());
        outState.putInt(KEY_DISTRITO, spDistritos.getSelectedItemPosition());
        outState.putInt(KEY_LOCALIDAD, spLocalidad.getSelectedItemPosition());
        outState.putInt(KEY_MOTIVOS, spMotivos.getSelectedItemPosition());
        outState.putString(KEY_OBSERVACION, edtObservacion.getText().toString());
        super.onSaveInstanceState(outState);
    }


    private void generarCodDocumento() {
        String cadena = sessionData.getUsuario() + "_" + proyecto.getCodigo() + "_" + motivos.get(spMotivos.getSelectedItemPosition()).getCodmotivo() + "_" + getFechaCadena();
        edtCedRuc.setText(cadena);
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
        localidads = getInstance(getApplicationContext()).getLocalidadByDistrito(parseInt(departamentos.get(spDepartamento.getSelectedItemPosition()).getCodigo()), distritoSelect);
        for (int i = 0; i < localidads.size(); i++) {
            if (localidads.get(i).getCodigo() == cod_localidad) {
                spLocalidad.setSelection(i);
                break;
            }
        }
    }
}