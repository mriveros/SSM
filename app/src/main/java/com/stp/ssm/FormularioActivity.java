package com.stp.ssm;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.scanlibrary.ScanConstants;
import com.stp.ssm.Adapters.FormularioPageAdapter;
import com.stp.ssm.Adapters.ListSeccionesAdapter;
import com.stp.ssm.Evt.AddMiembroFamiliaEvt;
import com.stp.ssm.Evt.FinalizoFormEvt;
import com.stp.ssm.Evt.NewSubFormularioEvt;
import com.stp.ssm.Evt.RemoveMiembroFamEvt;
import com.stp.ssm.Evt.ValidarSeccion;
import com.stp.ssm.Evt.ValidatePreguntaEvt;
import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Interfaces.OnDeleteListener;
import com.stp.ssm.Interfaces.OnTimeCronometroListener;
import com.stp.ssm.Model.Capturas;
import com.stp.ssm.Model.Motivos;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.Model.Proyecto;
import com.stp.ssm.Model.Secciones;
import com.stp.ssm.Model.Visita;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.Cronometro;
import com.stp.ssm.Util.FechaUtil;
import com.stp.ssm.Util.ImageFileUtil;
import com.stp.ssm.Util.ValidUtil;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.View.DialogAlertSimple;
import com.stp.ssm.View.DialogImagenes2;
import com.stp.ssm.View.DialogLstAdjuntos;
import com.stp.ssm.View.ViewFactory;

import java.io.IOException;
import java.util.ArrayList;

import static android.R.id.home;
import static android.content.ClipData.Item;
import static android.content.Intent.ACTION_GET_CONTENT;
import static android.content.Intent.EXTRA_ALLOW_MULTIPLE;
import static android.content.Intent.createChooser;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION.SDK_INT;
import static android.provider.MediaStore.Images;
import static android.provider.MediaStore.Images.Media;
import static android.provider.MediaStore.Images.Media.getBitmap;
import static android.support.v4.view.GravityCompat.START;
import static android.support.v4.view.ViewPager.OnPageChangeListener;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.view.WindowManager.LayoutParams;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
import static com.scanlibrary.ScanConstants.SCANNED_RESULT;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN.CAMARA;
import static com.stp.ssm.Model.Capturas.TIPO_ORIGEN.GALERIA;
import static com.stp.ssm.Model.Pregunta.TIPO;
import static com.stp.ssm.Model.Pregunta.TIPO.CHECKBOX;
import static com.stp.ssm.Model.Pregunta.TIPO.CUADRO_TEXTO_EMAIL;
import static com.stp.ssm.Model.Visita.TipoVisita;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_NUEVO_BENEFICIARIO;
import static com.stp.ssm.R.drawable;
import static com.stp.ssm.R.drawable.ic_nav_menu;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.act_adj;
import static com.stp.ssm.R.id.act_camera;
import static com.stp.ssm.R.id.act_img;
import static com.stp.ssm.R.id.act_scanner;
import static com.stp.ssm.R.id.act_view_adj;
import static com.stp.ssm.R.id.drawer_layout;
import static com.stp.ssm.R.id.lbl_time_visita;
import static com.stp.ssm.R.id.navview;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_formulario_1;
import static com.stp.ssm.R.menu;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msg_select;
import static com.stp.ssm.R.string.dialog_msg_sin_familia;
import static com.stp.ssm.R.string.dialog_msj_alert_confir_out;
import static com.stp.ssm.R.string.dialog_title_alert_capt;
import static com.stp.ssm.R.string.dialog_title_alert_confir;
import static com.stp.ssm.R.string.dialog_title_select;
import static com.stp.ssm.R.string.dialog_title_sin_familia;
import static com.stp.ssm.R.string.lbl_archivos;
import static com.stp.ssm.R.string.lbl_end_form;
import static com.stp.ssm.R.string.lbl_img_galeria;
import static com.stp.ssm.R.string.lbl_seccion;
import static com.stp.ssm.R.string.toast_msj_add_file;
import static com.stp.ssm.R.string.toast_msj_email_valid;
import static com.stp.ssm.R.string.toast_msj_err_requerido;
import static com.stp.ssm.R.string.toast_msj_err_select_img;
import static com.stp.ssm.SelectActivity.EX_PATH_RESULT;
import static com.stp.ssm.Util.CellUtils.adjuntarFichero;
import static com.stp.ssm.Util.CellUtils.scanDocumentAndroid;
import static com.stp.ssm.Util.CellUtils.takePhoto2;
import static com.stp.ssm.Util.CellUtils.takeScan;
import static com.stp.ssm.Util.FechaUtil.SegundoToHora;
import static com.stp.ssm.Util.ImageFileUtil.bitmapToFile;
import static com.stp.ssm.Util.ImageFileUtil.copyImagen;
import static com.stp.ssm.Util.ImageFileUtil.getRealPathFromURI;
import static com.stp.ssm.Util.ValidUtil.isValidEmail;
import static com.stp.ssm.View.ViewFactory.notificacionToast;
import static java.lang.Integer.parseInt;


public class FormularioActivity extends BaseActivity {

    private TextView lbl_time;
    private TextView lblseccion;
    private TextView lblformulario;
    private TextView lblcontcapturas;
    private TextView lblcontadj;
    private ViewPager vpFormulario;
    private TextView lblIndice;
    private ImageButton btnImgAnt;
    private ImageButton btnImgSgt;
    private Motivos motivos;
    private Proyecto proyecto;
    private Cronometro cron;
    private DrawerLayout drawerLayout;
    private ListView lstSecciones;
    private ListSeccionesAdapter seccionesAdapter;
    private FormularioPageAdapter formularioPageAdapter;
    private long idvisita;
    private long idvisitaRestore;
    private long segundos = 0;
    private int actualPosition = 0;
    private boolean isrunning = true;
    private String beneficiario;
    private int tipo_visita;
    private int tipo_destinatario;
    private int sw = 0;

    private ArrayList<Capturas> capturas;
    private ArrayList<String> adjuntos;

    private Bundle bundle;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_formulario_1);
        setTitle("");
        this.getWindow().setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        inicializar();
        eventBus.register(this);
        bundle = getIntent().getExtras();

        capturas = new ArrayList<>();
        adjuntos = new ArrayList<>();

        getSupportActionBar().setHomeAsUpIndicator(ic_nav_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(drawer_layout);

        idvisita = bundle.getLong("idvisita");
        motivos = (Motivos) bundle.getSerializable("motivo");
        if (bundle.getBoolean("recuperar")) {
            idvisitaRestore = bundle.getLong("idvisitarestore");
            motivos.setSecciones(dbFuntions.getSecciones(motivos.getCodmotivo(), idvisitaRestore));
        } else {
            motivos.setSecciones(dbFuntions.getSecciones(motivos.getCodmotivo(), idvisita));
        }
        proyecto = (Proyecto) getIntent().getExtras().getSerializable("proyecto");
        beneficiario = bundle.getString("beneficiario");
        tipo_visita = bundle.getInt("tipo_visita");
        /*if(tipo_visita == Visita.TipoVisita.VISITA_CORREGIR.getCodigo()){
            capturas = bundle.getStringArrayList("capturas");
            adjuntos = bundle.getStringArrayList("adjuntos");
        }*/
        tipo_destinatario = bundle.getInt("tipo_destinatario");

        lblseccion = (TextView) findViewById(id.lblseccion);
        lblseccion.setText(motivos.getSecciones().get(0).getDescripSeccion());
        lblformulario = (TextView) findViewById(id.lblformulario);
        lblcontcapturas = (TextView) findViewById(id.lblcontcapturas);
        lblcontadj = (TextView) findViewById(id.lblcontadj);
        lblformulario.setText(motivos.getDescripcionForm());

        vpFormulario = (ViewPager) findViewById(id.vpFormulario);
        lbl_time = (TextView) findViewById(lbl_time_visita);
        lblIndice = (TextView) findViewById(id.lblIndice);

        btnImgAnt = (ImageButton) findViewById(id.btnImgAnt);
        btnImgSgt = (ImageButton) findViewById(id.btnImgSgt);

        NavigationView navigationView = (NavigationView) findViewById(navview);
        View header = navigationView.getRootView();

        lstSecciones = (ListView) findViewById(id.lstSecciones);

        if (savedInstanceState != null) {
            motivos = (Motivos) savedInstanceState.getSerializable("motivossave");
        }

        segundos = bundle.getLong("segundos");
        inicializarConometro(segundos);

        cargarPaginas(motivos.getSecciones());
        cargarEventos();
    }

    @Override
    protected void onStart() {
        restaurarDatos();
        restaurarDatos(idvisita);
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
            case home:
                drawerLayout.openDrawer(START);
                break;
            case act_adj:
                //CellUtils.adjuntarFichero(this);
                DialogAcpView selecAdjunto = new DialogAcpView(getString(dialog_title_select),
                        getString(dialog_msg_select),
                        getString(lbl_archivos),
                        getString(lbl_img_galeria));
                selecAdjunto.setOnClickDialogListener(new OnClickDialogListener() {
                    @Override
                    public void OnPositiveClick(DialogInterface dialog, String tag) {
                        adjuntarFichero(FormularioActivity.this);
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
                takeScan(this);
                break;
        }
        return true;
    }

    private void cargarPaginas(ArrayList<Secciones> secciones) {
        lblIndice.setText(getString(lbl_seccion) + "1/" + secciones.size());
        seccionesAdapter = new ListSeccionesAdapter(getApplicationContext(), secciones);
        lstSecciones.setAdapter(seccionesAdapter);
        formularioPageAdapter = new FormularioPageAdapter(getApplicationContext(), secciones, getSupportFragmentManager(), beneficiario, parseInt(proyecto.getCodigo()));
        vpFormulario.setAdapter(formularioPageAdapter);
    }


    private void cargarEventos() {
        btnImgAnt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                retroceder();
            }
        });
        btnImgSgt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                avanzar();
            }
        });
        vpFormulario.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == (vpFormulario.getAdapter().getCount() - 1)) {/*Si esta en la pagina final*/
                    lblIndice.setText(getString(lbl_end_form));
                    lblseccion.setVisibility(GONE);
                } else {
                    lblseccion.setVisibility(VISIBLE);
                    lblseccion.setText(motivos.getSecciones().get(position).getDescripSeccion());
                    lblIndice.setText(getString(lbl_seccion) + (position + 1) + "/" + motivos.getSecciones().size());
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void avanzar() {
        if (vpFormulario.getCurrentItem() < (vpFormulario.getAdapter().getCount() - 1)) {
            if (motivos.getSecciones().get(actualPosition).getTipo() == 1 || motivos.getSecciones().get(actualPosition).getTipo() == 3) {
                if (validarSeccion(motivos.getSecciones().get(actualPosition))) {
                    dbFuntions.borrarRespuestas(motivos.getSecciones().get(actualPosition).getPreguntas(), idvisita);
                    dbFuntions.guardarRespuestas(motivos.getSecciones().get(actualPosition).getPreguntas(), idvisita, beneficiario, 0);

                    actualPosition = vpFormulario.getCurrentItem();
                    actualPosition++;
                    if (motivos.getSecciones().size() > actualPosition) {
                        while (!motivos.getSecciones().get(actualPosition).isVisible()) {
                            actualPosition++;
                        }
                    }
                    vpFormulario.setCurrentItem(actualPosition);
                }
            } else {
                actualPosition = vpFormulario.getCurrentItem();
                actualPosition++;
                if (motivos.getSecciones().size() > actualPosition) {
                    while (!motivos.getSecciones().get(actualPosition).isVisible()) {
                        actualPosition++;
                    }
                }
                vpFormulario.setCurrentItem(actualPosition);
            }
        }
    }

    private void retroceder() {
        if (vpFormulario.getCurrentItem() > 0) {
            actualPosition = vpFormulario.getCurrentItem();
            actualPosition--;
            while (!motivos.getSecciones().get(actualPosition).isVisible()) {
                actualPosition--;
            }
            vpFormulario.setCurrentItem(actualPosition);
        }
    }

    public void finalizar() {
        if (validarCapturas()) {
            for (Secciones secciones : motivos.getSecciones()) {

                switch (secciones.getTipo()) {
                    case 1:
                        dbFuntions.borrarRespuestas(secciones.getPreguntas(), idvisita);
                        dbFuntions.guardarRespuestas(secciones.getPreguntas(), idvisita, beneficiario, 0);
                        break;
                    case 2:
                        dbFuntions.borrarRespuestas(secciones.getPreguntas(), idvisita);
                        dbFuntions.guardarRespuestasSubform(secciones.getSubFormularios(), idvisita, beneficiario);
                        break;
                    case 3:
                        if (secciones.getHasfamily() == 0) {
                            dbFuntions.borrarRespuestas(secciones.getPreguntas(), idvisita);
                            dbFuntions.guardarRespuestasHogar(secciones.getBeneficiarios(), idvisita, beneficiario, secciones.getCodSeccion());
                        }
                        break;
                }
            }
            dbFuntions.borrarAdjuntosByVisita(idvisita);
            dbFuntions.addAdjuntos(adjuntos, idvisita, 0);
            dbFuntions.addCapturas2(capturas, idvisita, beneficiario, 0);
            dbFuntions.finalizarVisita(idvisita, segundos);

            setResult(RESULT_OK);
            isrunning = false;
            finish();
        }
    }


    private void limpiarBase() {
        for (Secciones secciones : motivos.getSecciones()) {
            dbFuntions.borrarRespuestas(secciones.getPreguntas(), idvisita);
        }
    }

    public void onEvent(FinalizoFormEvt evt) {
        finalizar();
    }


    public void onEvent(NewSubFormularioEvt evt) {
        motivos.getSecciones().get(evt.getPosicion()).setSubFormularios(evt.getSubFormularios());
        formularioPageAdapter.notifyDataSetChanged();
    }


    public void onEvent(AddMiembroFamiliaEvt evt) {
        motivos.getSecciones().get(evt.getPosicion()).addBeneficiario(evt.getBeneficiario());
        formularioPageAdapter.notifyDataSetChanged();
    }


    public void onEvent(RemoveMiembroFamEvt evt) {
        motivos.getSecciones().get(evt.getSeccioPosition()).removeBeneficiario(evt.getLstPositon());
        formularioPageAdapter.notifyDataSetChanged();
    }

    public void onEvent(ValidatePreguntaEvt evt) {
        for (Secciones secciones : motivos.getSecciones()) {
            for (Pregunta pregunta : secciones.getPreguntas()) {
                if (pregunta.getIdpregunta() == evt.getIdpregunta()) {
                    if (pregunta.isVisible()) {
                        if (!evt.isVisible()) {
                            pregunta.setVisible(false);
                            formularioPageAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (evt.isVisible()) {
                            pregunta.setVisible(true);
                            formularioPageAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                }
            }
        }
    }

    public void onEvent(ValidarSeccion evt) {
        for (Secciones secciones : motivos.getSecciones()) {
            if (secciones.getCodSeccion() > evt.getIdseccionCondicionante() &&
                    secciones.getCodSeccion() < evt.getIdseccionSiguiente()) {
                secciones.setVisible(evt.isVisible());
            }
        }
    }


    private void addAdjunto(String path) {
        adjuntos.add(path);
        notificacionToast(getApplicationContext(), getString(toast_msj_add_file));
    }

    @Override
    public void onBackPressed() {
        DialogAcpView dialog = new DialogAcpView(getString(dialog_title_alert_confir),
                getString(dialog_msj_alert_confir_out));
        dialog.setOnClickDialogListener(new OnClickDialogListener() {
            @Override
            public void OnPositiveClick(DialogInterface dialog, String tag) {
                dialog.dismiss();
                limpiarBase();
                isrunning = false;
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
            case 100:
                if (resultCode == RESULT_OK) {
                    addAdjunto(data.getStringExtra(EX_PATH_RESULT));
                }
                lblcontadj.setText(Integer.toString(adjuntos.size()));
                break;
            case 200:
                if (resultCode == RESULT_CANCELED) {
                    capturas.remove(capturas.size() - 1);
                }
                lblcontcapturas.setText(Integer.toString(capturas.size()));
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
                        String path = bitmapToFile(bitmap, beneficiario, capturas.size());
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

    private void restaurarDatos() {
        ArrayList<Capturas> restoreCap = dbFuntions.getCapturas(0);
        if (!restoreCap.isEmpty()) {
            capturas = restoreCap;
            dbFuntions.borrarCapturasByVisita(0);
        }
        lblcontcapturas.setText(Integer.toString(capturas.size()));

        ArrayList<String> restoreAdj = dbFuntions.getAdjuntos(0);
        if (!restoreAdj.isEmpty()) {
            adjuntos = restoreAdj;
            dbFuntions.borrarAdjuntosByVisita(0);
        }
        lblcontadj.setText(Integer.toString(adjuntos.size()));
    }

    private void restaurarDatos(long idvisita) {
        ArrayList<Capturas> restoreCap = dbFuntions.getCapturas(idvisita);
        if (!restoreCap.isEmpty()) {
            capturas.addAll(restoreCap);
        }
        dbFuntions.borrarCapturasByVisita(idvisita);
        lblcontcapturas.setText(Integer.toString(capturas.size()));

        ArrayList<String> restoreAdj = dbFuntions.getAdjuntos(idvisita);
        if (!restoreCap.isEmpty()) {
            adjuntos.addAll(restoreAdj);
        }
        dbFuntions.borrarAdjuntosByVisita(idvisita);
        lblcontadj.setText(Integer.toString(adjuntos.size()));

        if (sessionData.getLastIdVisita() == idvisita) {
            segundos = sessionData.getLastTimeVisita();
        } else {
            segundos = 0;
        }

        if (sw == 1) {
            inicializarConometro(segundos);
        }
        sw = 1;
    }

    private void inicializarConometro(long seg) {
        segundos = seg;
        if (cron != null) {
            cron.finalizar();
        }
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

    private boolean validarSeccion(Secciones secciones) {
        if ((secciones.getTipo() == 3 && secciones.getHasfamily() == 0)) {
            if (secciones.getBeneficiarios() == null || secciones.getBeneficiarios().isEmpty()) {
                new DialogAlertSimple(getString(dialog_title_sin_familia),
                        getString(dialog_msg_sin_familia))
                        .show(getSupportFragmentManager(), "");
                secciones.setHasResponse(false);
                return false;
            }
        }

        for (Pregunta pregunta : secciones.getPreguntas()) {
            if (pregunta.isRequerido() && pregunta.isVisible()) {
                if (pregunta.getTipo().equals(CHECKBOX)) {
                    if (pregunta.getSelecresp().isEmpty()) {
                        notificacionToast(getApplicationContext(), getString(toast_msj_err_requerido));
                        secciones.setHasResponse(false);
                        return false;
                    }
                } else if (pregunta.getTipo().equals(CUADRO_TEXTO_EMAIL) && !isValidEmail(pregunta.getTxtrespuesta())) {
                    notificacionToast(getApplicationContext(), getResources().getString(toast_msj_email_valid));
                    secciones.setHasResponse(false);
                    return false;
                } else {
                    if (pregunta.getTxtrespuesta().equals("")) {
                        notificacionToast(getApplicationContext(), getString(toast_msj_err_requerido));
                        secciones.setHasResponse(false);
                        return false;
                    }
                }
            }
        }
        secciones.setHasResponse(true);
        seccionesAdapter.notifyDataSetChanged();
        return true;
    }

    private boolean validarCapturas() {
        if (tipo_visita == VISITA_NUEVO_BENEFICIARIO.getCodigo() && tipo_destinatario == 0) {
            if (capturas.size() < proyecto.getCant_min_img()) {
                new DialogAlertSimple(getString(dialog_title_alert_capt), "Debe agregar al menos " + proyecto.getCant_min_img() + " capturas del Destinatario").show(getSupportFragmentManager(), "");
                return false;
            }
        }

        if (tipo_destinatario == 1) {
            if (capturas.size() < proyecto.getCant_min_img()) {
                new DialogAlertSimple(getString(dialog_title_alert_capt), "Debe agregar al menos " + proyecto.getCant_min_img() + "evidencias de la Actividad").show(getSupportFragmentManager(), "");
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        if (isrunning) {
            dbFuntions.addCapturas2(capturas, 0, "", 2);
            dbFuntions.addAdjuntos(adjuntos, 0, 2);
        }
        if (!isrunning && cron != null) {
            cron.finalizar();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("motivossave", motivos);
        super.onSaveInstanceState(outState);
    }
}