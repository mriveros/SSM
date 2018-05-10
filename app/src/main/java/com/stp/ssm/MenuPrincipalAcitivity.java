package com.stp.ssm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stp.ssm.Evt.CerrarSessionEvt;
import com.stp.ssm.Evt.DescargaBeneficiarioEvt;
import com.stp.ssm.Evt.DescargaMotivosEvt;
import com.stp.ssm.Evt.FinalizoInsert;
import com.stp.ssm.Evt.MarcacionEvt;
import com.stp.ssm.Evt.NuevaDistanciaEvt;
import com.stp.ssm.Evt.NuevaHoraEvt;
import com.stp.ssm.Evt.TokenExpirado;
import com.stp.ssm.Excepciones.ExcepcionReadBD;
import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Interfaces.OnItemListSelectListener;
import com.stp.ssm.Model.HelpObject;
import com.stp.ssm.Model.Proyecto;
import com.stp.ssm.Model.Visita;
import com.stp.ssm.Servicios.InsertarDatosService;
import com.stp.ssm.Servicios.MarcacionService;
import com.stp.ssm.Servicios.SendDataService2;
import com.stp.ssm.Servicios.UbicacionService;
import com.stp.ssm.Servicios.VerifcGelleryService;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.SessionData;
import com.stp.ssm.http.HttpVolleyRequest;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.View.DialogAlertSimple;
import com.stp.ssm.View.DialogList;
import com.stp.ssm.View.ViewFactory;
import com.stp.ssm.http.URLs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.os.Environment.getExternalStorageDirectory;
import static android.util.Log.i;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static com.stp.ssm.Evt.CerrarSessionEvt.RESULTADO;
import static com.stp.ssm.Evt.CerrarSessionEvt.RESULTADO.OK;
import static com.stp.ssm.Model.Visita.TipoVisita;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_NUEVO_BENEFICIARIO;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.act_help;
import static com.stp.ssm.R.id.action_opciones;
import static com.stp.ssm.R.id.btnReporteMarcacion;
import static com.stp.ssm.R.id.mn_cerrar_session;
import static com.stp.ssm.R.id.mn_descargar;
import static com.stp.ssm.R.id.mn_exportar;
import static com.stp.ssm.R.id.mn_importar;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_menu;
import static com.stp.ssm.R.menu;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_btn_enviartarde;
import static com.stp.ssm.R.string.dialog_btn_reporte;
import static com.stp.ssm.R.string.dialog_msg_cerrar_session;
import static com.stp.ssm.R.string.dialog_msg_error_descargar;
import static com.stp.ssm.R.string.dialog_msg_timeout_marcacion;
import static com.stp.ssm.R.string.dialog_msg_token;
import static com.stp.ssm.R.string.dialog_msj_copy;
import static com.stp.ssm.R.string.dialog_msj_err_cerrar_session;
import static com.stp.ssm.R.string.dialog_msj_finalizar;
import static com.stp.ssm.R.string.dialog_msj_marcacion;
import static com.stp.ssm.R.string.dialog_msj_marcacion_confir;
import static com.stp.ssm.R.string.dialog_msj_marcacion_err;
import static com.stp.ssm.R.string.dialog_msj_pendientes;
import static com.stp.ssm.R.string.dialog_msj_session_close;
import static com.stp.ssm.R.string.dialog_title_cerrar_session;
import static com.stp.ssm.R.string.dialog_title_copy;
import static com.stp.ssm.R.string.dialog_title_error_descargar;
import static com.stp.ssm.R.string.dialog_title_finalizar;
import static com.stp.ssm.R.string.dialog_title_marcacion;
import static com.stp.ssm.R.string.dialog_title_pendientes;
import static com.stp.ssm.R.string.dialog_title_proy;
import static com.stp.ssm.R.string.dialog_title_session;
import static com.stp.ssm.R.string.dialog_title_token;
import static com.stp.ssm.R.string.help_btn_menu_evento;
import static com.stp.ssm.R.string.help_btn_menu_marcacion;
import static com.stp.ssm.R.string.help_btn_menu_notificaciones;
import static com.stp.ssm.R.string.help_btn_menu_recorrido;
import static com.stp.ssm.R.string.help_btn_menu_reporte;
import static com.stp.ssm.R.string.help_btn_menu_visita;
import static com.stp.ssm.R.string.help_linear_menu;
import static com.stp.ssm.R.string.lbl_btn_marcacion_ent;
import static com.stp.ssm.R.string.lbl_btn_marcacion_sal;
import static com.stp.ssm.R.string.lbl_descarga;
import static com.stp.ssm.R.string.lbl_fin_recorrido;
import static com.stp.ssm.R.string.lbl_inicio_recorrido;
import static com.stp.ssm.R.string.lbl_metros;
import static com.stp.ssm.R.string.lbl_msj_descargando;
import static com.stp.ssm.R.string.lbl_no;
import static com.stp.ssm.R.string.lbl_si;
import static com.stp.ssm.R.string.lbl_usuario;
import static com.stp.ssm.R.string.toast_msj_alert_recorrido;
import static com.stp.ssm.R.string.toast_msj_sinc;
import static com.stp.ssm.SelectActivity.EX_PATH;
import static com.stp.ssm.SelectActivity.EX_PATH_RESULT;
import static com.stp.ssm.SelectActivity.EX_STYLE;
import static com.stp.ssm.SelectMode.SELECT_FILE;
import static com.stp.ssm.SelectMode.SELECT_FOLDER;
import static com.stp.ssm.Util.CellUtils.checkNetworkConnect;
import static com.stp.ssm.Util.CellUtils.copyDatabaseToFolder;
import static com.stp.ssm.Util.CellUtils.hasConnectionInternet;
import static com.stp.ssm.Util.CellUtils.isMyServiceRunning;
import static com.stp.ssm.View.ViewFactory.notificacionToast;
import static com.stp.ssm.http.HttpVolleyRequest.getInstance;
import static com.stp.ssm.http.URLs.URL_CERRAR_SESSION;

public class MenuPrincipalAcitivity extends BaseActivity {
    private LinearLayout layout_timer;
    private Button btnVisita;
    private Button btnReporte;
    private Button btnEvento;
    private Button btnRecorrido;
    private Button btnMarcacion;
    private Button btnNotificaciones;
    private Button btnGaleria;
    private Button btnRepMarcaciones;
    private TextView lbl_time;
    private TextView lbl_distancia;
    private boolean finishRequestBeneficiario;
    private boolean finishRequestEncuesta;
    private String cadenaBeneficiarios;
    private String cadenaEncuesta;
    private Handler handler = new Handler();
    private ArrayList<HelpObject> arr_help_object;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_menu);
        inicializar();

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setVisibility(GONE);
        progressBar.setIndeterminate(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(progressBar);

        eventBus.register(this);
        setTitle(getString(lbl_usuario) + ":" + sessionData.getUsuario());
        layout_timer = (LinearLayout) findViewById(id.layout_timer);
        lbl_distancia = (TextView) findViewById(id.lbl_distancia);
        lbl_time = (TextView) findViewById(id.lbl_time);
        btnVisita = (Button) findViewById(id.btnVisita);
        btnReporte = (Button) findViewById(id.btnReporte);
        btnEvento = (Button) findViewById(id.btnEvento);
        btnRecorrido = (Button) findViewById(id.btnRecorrido);
        btnMarcacion = (Button) findViewById(id.btnMarcacion);
        btnNotificaciones = (Button) findViewById(id.btnNotificaciones);
        btnGaleria = (Button) findViewById(id.btnGaleria);
        btnRepMarcaciones = (Button) findViewById(btnReporteMarcacion);

        arr_help_object = new ArrayList<>();
        arr_help_object.add(new HelpObject(findViewById(action_opciones), getString(help_linear_menu), false));
        arr_help_object.add(new HelpObject(layout_timer, getString(help_linear_menu), true));
        arr_help_object.add(new HelpObject(btnVisita, getString(help_btn_menu_visita), true));
        arr_help_object.add(new HelpObject(btnReporte, getString(help_btn_menu_reporte), true));
        arr_help_object.add(new HelpObject(btnEvento, getString(help_btn_menu_evento), true));
        arr_help_object.add(new HelpObject(btnNotificaciones, getString(help_btn_menu_notificaciones), true));
        arr_help_object.add(new HelpObject(btnRecorrido, getString(help_btn_menu_recorrido), true));
        arr_help_object.add(new HelpObject(btnMarcacion, getString(help_btn_menu_marcacion), true));

        validTipoUsuario();
        checkSession();
        asignarEventos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkNetworkConnect(getApplicationContext())) {
            if (sessionData.getToken().equals("")) {
                DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_token),
                        getString(dialog_msg_token),
                        "Ahora", "Mas Tarde");
                dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
                    @Override
                    public void OnPositiveClick(DialogInterface dialog, String tag) {
                        cerrarSession();
                    }

                    @Override
                    public void OnNegativeClick(DialogInterface dialog, String tag) {
                        dialog.cancel();
                    }
                });
                dialogAcpView.show(getSupportFragmentManager(), "");
            }
        }
    }

    public void validTipoUsuario() {
        if (sessionData.getNivel() == 9) {
            btnVisita.setVisibility(GONE);
            btnReporte.setVisibility(GONE);
            btnNotificaciones.setVisibility(GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu.mn_menu_principal, menu);
        return true;
    }

    @Override
    public void setSupportProgressBarIndeterminateVisibility(boolean visible) {
        getSupportActionBar().getCustomView().setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case mn_descargar:
                descargarBase();
                break;
            case mn_exportar:
                exportarBaseDirectorio();
                break;
            case mn_importar:
                importarBase();
                break;
            case act_help:
                //iniciarHelp(arr_help_object);
                break;
            case mn_cerrar_session:
                if (!sessionData.isRecorridoStart()) {
                    DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_cerrar_session),
                            getString(dialog_msg_cerrar_session),
                            getString(lbl_si),
                            getString(lbl_no));
                    dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
                        @Override
                        public void OnPositiveClick(DialogInterface dialog, String tag) {
                            cerrarSession();
                            dialog.dismiss();
                        }

                        @Override
                        public void OnNegativeClick(DialogInterface dialog, String tag) {
                            dialog.dismiss();
                        }
                    });
                    dialogAcpView.show(getSupportFragmentManager(), "");
                } else {
                    notificacionToast(getApplicationContext(), "Debe finalizar el Recorrido");
                }
                break;
        }
        return true;
    }

    private void asignarEventos() {
        btnVisita.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionData.isRecorridoStart()) {
                    if (!isMyServiceRunning(InsertarDatosService.class, getApplicationContext())) {
                        final ArrayList<Proyecto> array = dbFuntions.arrayProyectos();
                        DialogList dialogList = new DialogList(getString(dialog_title_proy), listToArrayString(array));
                        dialogList.setOnItemListSelectListener(new OnItemListSelectListener() {
                            @Override
                            public void OnItemListSelect(int position) {
                                Proyecto proyecto = array.get(position);
                                if (proyecto.getTipo() == 3) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("tipovisita", VISITA_NUEVO_BENEFICIARIO.getCodigo());
                                    bundle.putSerializable("proyecto", proyecto);
                                    bundle.putString("entidad_relevar", proyecto.getEntidad_relevar());
                                    startActivity(new Intent(getApplicationContext(), VisitaRelevamientoActivity.class).putExtras(bundle));
                                } else {
                                    startActivity(new Intent(MenuPrincipalAcitivity.this, ListaBeneficiariosActivity.class).putExtra("proyecto", array.get(position)));
                                }
                            }
                        });
                        dialogList.show(getSupportFragmentManager(), "");
                    } else {
                        notificacionToast(getApplication(), getString(toast_msj_sinc));
                    }
                } else {
                    notificacionToast(getApplicationContext(), getString(toast_msj_alert_recorrido));
                }
            }
        });


        btnReporte.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuPrincipalAcitivity.this, ReporteActivity.class));
            }
        });

        btnRecorrido.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionData.isRecorridoStart()) {
                    DialogAcpView dialog = new DialogAcpView(getString(dialog_title_finalizar), getString(dialog_msj_finalizar));
                    dialog.setOnClickDialogListener(new OnClickDialogListener() {
                        @Override
                        public void OnPositiveClick(DialogInterface dialog, String tag) {
                            finalizarRecorrido();
                            if (dbFuntions.hasPendientes()) {
                                dialogPendietes();
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void OnNegativeClick(DialogInterface dialog, String tag) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show(getSupportFragmentManager(), "");
                } else {
                    btnRecorrido.setText(getString(lbl_fin_recorrido));
                    startService(new Intent(MenuPrincipalAcitivity.this, UbicacionService.class));
                }
            }
        });

        btnEvento.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionData.isRecorridoStart()) {
                    startActivity(new Intent(getApplicationContext(), InformarEventoActivity.class));
                } else {
                    notificacionToast(getApplicationContext(), getString(toast_msj_alert_recorrido));
                }
            }
        });

        btnMarcacion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                marcarEntradaSalida();
            }
        });

        btnNotificaciones.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ListNotificationsActivity.class));
            }
        });

        btnGaleria.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), GaleriaInternaActivity.class));
            }
        });

        btnRepMarcaciones.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }


    private void finalizarRecorrido() {
        sessionData.finalizarRecorrido();
        btnRecorrido.setText(getString(lbl_inicio_recorrido));
        sessionData.setTimeSave(0);
        sessionData.setDistanciaRecorrida(0);
        stopService(new Intent(MenuPrincipalAcitivity.this, UbicacionService.class));
    }

    private void dialogPendietes() {
        DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_pendientes),
                getString(dialog_msj_pendientes),
                getString(dialog_btn_reporte),
                getString(dialog_btn_enviartarde));
        dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
            @Override
            public void OnPositiveClick(DialogInterface dialog, String tag) {
                startActivity(new Intent(getApplicationContext(), ReporteActivity.class));
                dialog.dismiss();
            }

            @Override
            public void OnNegativeClick(DialogInterface dialog, String tag) {
                dialog.dismiss();
            }
        });
        dialogAcpView.show(getSupportFragmentManager(), "");
    }

    private void checkSession() {
        if (sessionData.getEstadoMarcacion() == 0) {
            btnMarcacion.setText(getString(lbl_btn_marcacion_ent));
        } else {
            btnMarcacion.setText(getString(lbl_btn_marcacion_sal));
        }

        if (!sessionData.isRecorridoStart()) {
            /*if(CellUtils.isMyServiceRunning(UbicacionService.class,getApplicationContext())){
                stopService(new Intent(MenuPrincipalAcitivity.this, UbicacionService.class));
            }*/
            if (!sessionData.isSessionStart()) {
                descargarBase();
            } else {
                startService(new Intent(getApplicationContext(), VerifcGelleryService.class));
            }

            btnRecorrido.setText(getString(lbl_inicio_recorrido));
            if (dbFuntions.hasPendientes()) {
                dialogPendietes();
            }
        } else {
            btnRecorrido.setText(getString(lbl_fin_recorrido));
            if (!isMyServiceRunning(UbicacionService.class, getApplicationContext())) {
                startService(new Intent(MenuPrincipalAcitivity.this, UbicacionService.class));
            }
        }
    }

    private void descargarBase() {
        sessionData.iniciarSession();
        setSupportProgressBarIndeterminateVisibility(true);
        if (hasConnectionInternet(getApplicationContext())) {
            showProgressDialog(getString(lbl_descarga), getString(lbl_msj_descargando));
            finishRequestBeneficiario = false;
            finishRequestEncuesta = false;
            String token = sessionData.getToken();
            if (!token.equals("")) {
                getInstance(getApplicationContext()).RequestGetBase(sessionData.getUsuario(), token);
            } else {
                cerrarSession();
            }

        }
    }

    public void onEvent(DescargaBeneficiarioEvt evt) {
        finishRequestBeneficiario = true;
        if (evt.getJson() != null) {
            cadenaBeneficiarios = evt.getJson();
        }
        starProcessInsert();

    }

    public void onEvent(DescargaMotivosEvt evt) {
        finishRequestEncuesta = true;
        if (evt.getJson() != null) {
            cadenaEncuesta = evt.getJson();
        }
        starProcessInsert();
    }

    public void onEvent(CerrarSessionEvt evt) {
        offProgressDialog();
        if (evt.getResultado() == OK) {
            sessionData.disablelocationMonitor();
            sessionData.cerrarSession();
            //startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        } else {
            DialogAcpView dialog = new DialogAcpView(getString(dialog_title_session),
                    getString(dialog_msj_err_cerrar_session));
            dialog.setOnClickDialogListener(new OnClickDialogListener() {
                @Override
                public void OnPositiveClick(DialogInterface dialog, String tag) {
                    dialog.dismiss();
                    finish();
                }

                @Override
                public void OnNegativeClick(DialogInterface dialog, String tag) {
                    dialog.dismiss();
                }
            });
            dialog.show(getSupportFragmentManager(), "");
        }
    }

    public void onEvent(final NuevaHoraEvt evt) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lbl_time.setText(evt.getHora());
            }
        }, 100);
    }

    public void onEvent(final NuevaDistanciaEvt evt) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lbl_distancia.setText(evt.getDistancia() + getString(lbl_metros));
            }
        }, 100);
    }

    public void onEvent(FinalizoInsert evt) {
        handler.postDelayed(new Runnable() {
            public void run() {
                startService(new Intent(getApplicationContext(), VerifcGelleryService.class));
                setSupportProgressBarIndeterminateVisibility(false);
            }
        }, 100);
    }

    public void onEvent(TokenExpirado evt) {
        finalizarRecorrido();
        cerrarSession();
        startActivity(new Intent(getApplicationContext(), AlertSessionActivity.class));
    }


    private void starProcessInsert() {
        if (finishRequestEncuesta && finishRequestBeneficiario) {
            offProgressDialog();
            if (cadenaBeneficiarios == null) {
                cadenaBeneficiarios = "";
            }

            if (cadenaEncuesta == null) {
                cadenaEncuesta = "";
            }

            if (!cadenaBeneficiarios.equals("")) {
                if (!cadenaEncuesta.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), InsertarDatosService.class)
                            .putExtra("jsonBeneficiario", cadenaBeneficiarios)
                            .putExtra("jsonEncuesta", cadenaEncuesta)
                            .putExtra("usuario", sessionData.getUsuario());
                    i("Beneficiarios", cadenaBeneficiarios);
                    i("Proyecto", cadenaEncuesta);
                    startService(intent);
                } else {
                    DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_error_descargar),
                            getString(dialog_msg_error_descargar),
                            getString(lbl_si),
                            getString(lbl_no));
                    dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
                        @Override
                        public void OnPositiveClick(DialogInterface dialog, String tag) {
                            descargarBase();
                            dialog.dismiss();
                        }

                        @Override
                        public void OnNegativeClick(DialogInterface dialog, String tag) {
                            setSupportProgressBarIndeterminateVisibility(false);
                            dialog.dismiss();
                        }
                    });
                    dialogAcpView.show(getSupportFragmentManager(), "");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void cerrarSession() {
        showProgressDialog(getString(dialog_title_session), getString(dialog_msj_session_close));
        final Map<String, String> parametros = new HashMap<>();
        parametros.put("usuario", sessionData.getUsuario());
        //String token = sessionData.getToken();
        getInstance(getApplicationContext()).cerrarSession(URL_CERRAR_SESSION, parametros, "");
    }


    private void exportarBaseDirectorio() {
        Intent i = new Intent(getApplicationContext(), SelectActivity.class);
        i.putExtra(EX_PATH, getExternalStorageDirectory().getAbsolutePath());
        i.putExtra(EX_STYLE, SELECT_FOLDER);
        startActivityForResult(i, 100);
    }


    private void importarBase() {
        Intent i = new Intent(getApplicationContext(), SelectActivity.class);
        i.putExtra(EX_PATH, getExternalStorageDirectory().getAbsolutePath());
        i.putExtra(EX_STYLE, SELECT_FILE);
        startActivityForResult(i, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    new ExportarBase().execute(data.getStringExtra(EX_PATH_RESULT));
                }
                break;
            case 200:
                if (resultCode == RESULT_OK) {
                    new ImportarBase().execute(data.getStringExtra(EX_PATH_RESULT));
                }
                break;
        }
    }

    private class ExportarBase extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            showProgressDialog(getString(dialog_title_copy), getString(dialog_msj_copy));
        }

        @Override
        protected Void doInBackground(String... params) {
            copyDatabaseToFolder(params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            offProgressDialog();
        }
    }

    private class ImportarBase extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            showProgressDialog(getString(dialog_title_copy), getString(dialog_msj_copy));
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                dbFuntions.importarBD(params[0]);
            } catch (ExcepcionReadBD excepcionReadBD) {
                notificacionToast(getApplicationContext(), excepcionReadBD.getCause().getMessage());
                excepcionReadBD.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            offProgressDialog();
        }
    }

    private String[] listToArrayString(ArrayList<Proyecto> array) {
        String cadena[] = new String[array.size()];
        int cont = 0;
        for (Proyecto proyecto : array) {
            cadena[cont] = proyecto.getDescripcion() + "(" + proyecto.getInstitucionNom() + ")";
            cont++;
        }
        return cadena;
    }


    private void marcarEntradaSalida() {
        int tipo = dbFuntions.isEntradaSalida();
        if (tipo > 0) {
            if (tipo == 4) {
                DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_marcacion),
                        getString(dialog_msj_marcacion_confir));
                dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
                    @Override
                    public void OnPositiveClick(DialogInterface dialog, String tag) {
                        sessionData.setSalida();
                        tomarMarcacion();
                        dialog.dismiss();
                    }

                    @Override
                    public void OnNegativeClick(DialogInterface dialog, String tag) {
                        dialog.dismiss();
                    }
                });
                dialogAcpView.show(getSupportFragmentManager(), "");
            } else {
                sessionData.setEntrada();
                tomarMarcacion();
            }
        } else {
            new DialogAlertSimple(getString(dialog_title_marcacion), getString(dialog_msj_marcacion_err)).show(getSupportFragmentManager(), "");
        }
    }

    private void tomarMarcacion() {
        showProgressDialog(getString(dialog_title_marcacion), getString(dialog_msj_marcacion));
        startService(new Intent(getApplicationContext(), MarcacionService.class));
    }

    public void onEvent(MarcacionEvt evt) {
        offProgressDialog();
        if (evt.getTipo() > 0) {
            if (evt.getTipo() == 4) {
                btnMarcacion.setText(getString(lbl_btn_marcacion_ent));
            } else {
                btnMarcacion.setText(getString(lbl_btn_marcacion_sal));
            }
            if (hasConnectionInternet(getApplicationContext()) && !isMyServiceRunning(SendDataService2.class, getApplicationContext())) {
                startService(new Intent(getApplicationContext(), SendDataService2.class));
            }
        } else {
            DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_marcacion),
                    getString(dialog_msg_timeout_marcacion));
            dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
                @Override
                public void OnPositiveClick(DialogInterface dialog, String tag) {
                    dialog.dismiss();
                    tomarMarcacion();
                }

                @Override
                public void OnNegativeClick(DialogInterface dialog, String tag) {
                    dialog.dismiss();
                }
            });
            dialogAcpView.show(getSupportFragmentManager(), "");
        }
    }
}