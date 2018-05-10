package com.stp.ssm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.stp.ssm.Adapters.ListaReporteAdapter;
import com.stp.ssm.Evt.ContinuarFormEvt;
import com.stp.ssm.Evt.CorregirRelevamientoEvt;
import com.stp.ssm.Evt.SendDataFinish;
import com.stp.ssm.Evt.SendDataSuccessful;
import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Model.Beneficiario;
import com.stp.ssm.Model.Capturas;
import com.stp.ssm.Model.Motivos;
import com.stp.ssm.Model.Proyecto;
import com.stp.ssm.Model.Reporte;
import com.stp.ssm.Model.TotalRelevado;
import com.stp.ssm.Model.Visita;
import com.stp.ssm.Servicios.SendDataService2;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.View.ResumenView;
import com.stp.ssm.View.ViewFactory;

import java.util.ArrayList;

import static com.stp.ssm.Model.Reporte.Envio;
import static com.stp.ssm.Model.Reporte.Envio.ENVIADO;
import static com.stp.ssm.Model.Reporte.Estado;
import static com.stp.ssm.Model.Reporte.Estado.NO_FINALIZADO;
import static com.stp.ssm.Model.Visita.TipoVisita;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_CORREGIR;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.act_delete;
import static com.stp.ssm.R.id.act_send;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_reporte;
import static com.stp.ssm.R.menu;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msj_delete;
import static com.stp.ssm.R.string.dialog_msj_progress;
import static com.stp.ssm.R.string.dialog_msj_progress_run;
import static com.stp.ssm.R.string.dialog_msj_sinfinalizar;
import static com.stp.ssm.R.string.dialog_title_delete;
import static com.stp.ssm.R.string.dialog_title_pendientes;
import static com.stp.ssm.R.string.dialog_title_sinfinalizar;
import static com.stp.ssm.R.string.title_Reporte;
import static com.stp.ssm.R.string.toast_msj_sin_con;
import static com.stp.ssm.R.string.toast_msj_sinpendientes;
import static com.stp.ssm.Util.CellUtils.hasConnectionInternet;
import static com.stp.ssm.Util.CellUtils.isMyServiceRunning;
import static com.stp.ssm.View.ViewFactory.notificacionToast;

public class ReporteActivity extends BaseActivity {

    private ListView lstReporte;
    private ResumenView rsmResumen;
    private int totalpendiente = 0;
    private int enviado = 0;
    private boolean haspendientes = false;
    private ArrayList<Reporte> listReporte;
    private Handler handler = new Handler();
    private final String TAG_DIALOG_DELETE = "confirmarDelete";
    private final String TAG_DIALOG_FORMULARIO = "continuar_form";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_reporte);
        setTitle(getString(title_Reporte));
        inicializar();

        eventBus.register(this);
        rsmResumen = (ResumenView) findViewById(id.rsmResumen);
        lstReporte = (ListView) findViewById(id.lstReporte);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu.mn_reporte, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case act_send:
                if (haspendientes) {
                    if (hasConnectionInternet(getApplicationContext())) {
                        DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_pendientes),
                                getString(dialog_msj_progress));
                        dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
                            @Override
                            public void OnPositiveClick(DialogInterface dialog, String tag) {
                                enviado = 0;
                                cargarResumen();
                                showProgressBarDialog(getString(dialog_msj_progress_run), totalpendiente);
                                enviarPendientes();
                            }

                            @Override
                            public void OnNegativeClick(DialogInterface dialog, String tag) {
                                enviarPendientes();
                            }
                        });
                        dialogAcpView.show(getSupportFragmentManager(), "");
                    } else {
                        notificacionToast(getApplicationContext(), getString(toast_msj_sin_con));
                    }
                } else {
                    notificacionToast(getApplicationContext(), getResources().getString(toast_msj_sinpendientes));
                }
                break;
            case act_delete:
                DialogAcpView dialog = new DialogAcpView(getString(dialog_title_delete), getString(dialog_msj_delete));
                dialog.setOnClickDialogListener(new OnClickDialogListener() {
                    @Override
                    public void OnPositiveClick(DialogInterface dialog, String tag) {
                        dbFuntions.limpiarEnviados();
                        ((ListaReporteAdapter) lstReporte.getAdapter()).notifyData();
                        dialog.dismiss();
                        cargarDatos();
                    }

                    @Override
                    public void OnNegativeClick(DialogInterface dialog, String tag) {
                        dialog.dismiss();
                    }
                });
                dialog.show(getSupportFragmentManager(), TAG_DIALOG_DELETE);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
    }


    private void cargarDatos() {
        listReporte = dbFuntions.getReporte();

        if (listReporte != null) {
            lstReporte.setAdapter(new ListaReporteAdapter(listReporte, getApplicationContext()));
            cargarResumen();
        } else {
            lstReporte.setAdapter(null);
            rsmResumen.cargarDatos(0, 0, null, null);
            rsmResumen.setTotalCaptVerif(null);
        }
    }


    private void cargarResumen() {
        int totalRelevado = listReporte.size();
        int totalEnviado = getTotalEnviado(listReporte);
        TotalRelevado totalCapturas = dbFuntions.getTotalCapturas();
        TotalRelevado totalAdjuntos = dbFuntions.getTotalAdjuntos();

        rsmResumen.cargarDatos(totalRelevado, totalEnviado, totalCapturas, totalAdjuntos);
        if ((totalRelevado == totalEnviado && totalAdjuntos.getEnviado() == totalAdjuntos.getTotal())
                && (totalCapturas.getTotal() == totalCapturas.getEnviado())) {
            offProgressDialog();
            totalpendiente = 0;
            haspendientes = false;
        } else {
            totalpendiente = (totalRelevado - totalEnviado) + (totalAdjuntos.getTotal() - totalAdjuntos.getEnviado()) + (totalCapturas.getTotal() - totalCapturas.getEnviado());
            haspendientes = true;
        }
    }

    public int getTotalEnviado(ArrayList<Reporte> listReporte) {
        int sum = 0;
        for (Reporte reporte : listReporte) {
            if (reporte.getEnvio().equals(ENVIADO)) {
                sum++;
            }
        }
        return sum;
    }


    public void onEvent(SendDataSuccessful evt) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listReporte = dbFuntions.getReporte();
                cargarDatos();
                if (barProgressDialog != null && barProgressDialog.isShowing()) {
                    if (totalpendiente == 0) {
                        offProgressDialog();
                    } else {
                        enviado++;
                        barProgressDialog.setProgress(enviado);
                    }
                }
            }
        }, 100);
    }


    public void onEvent(SendDataFinish evt) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                listReporte = dbFuntions.getReporte();
                cargarDatos();
                if (barProgressDialog != null && barProgressDialog.isShowing()) {
                    offProgressDialog();
                }
            }
        }, 100);
    }


    public void onEvent(ContinuarFormEvt evt) {
        if (listReporte.get(evt.getPosition()).getEstado().equals(NO_FINALIZADO)) {
            final int position = evt.getPosition();
            DialogAcpView dialog = new DialogAcpView(getString(dialog_title_sinfinalizar), getString(dialog_msj_sinfinalizar));
            dialog.setOnClickDialogListener(new OnClickDialogListener() {
                @Override
                public void OnPositiveClick(DialogInterface dialog, String tag) {
                    Motivos motivos = dbFuntions.getMotivo(listReporte.get(position).getCodmotivo());
                    Proyecto proyecto = dbFuntions.getProyectoByCodMotivo(listReporte.get(position).getCodmotivo());
                    ArrayList<Capturas> capturas = dbFuntions.getCapturas(listReporte.get(position).getIdvisita());
                    ArrayList<String> adjuntos = dbFuntions.getAdjuntos(listReporte.get(position).getIdvisita());

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("motivo", motivos);
                    bundle.putSerializable("proyecto", proyecto);
                    bundle.putParcelableArrayList("capturas", capturas);
                    bundle.putStringArrayList("adjuntos", adjuntos);
                    bundle.putLong("idvisita", listReporte.get(position).getIdvisita());
                    bundle.putLong("idvisitarestore", listReporte.get(position).getIdvisita());
                    bundle.putString("id_key_rel", listReporte.get(position).getId_key_rel());
                    bundle.putBoolean("recuperar", true);

                    Intent intent = new Intent(getApplicationContext(), FormularioActivity.class).putExtras(bundle);
                    startActivity(intent);
                    dialog.dismiss();
                }

                @Override
                public void OnNegativeClick(DialogInterface dialog, String tag) {
                    dialog.dismiss();
                }
            });
            dialog.show(getSupportFragmentManager(), TAG_DIALOG_FORMULARIO);
        }
    }


    public void onEvent(CorregirRelevamientoEvt evt) {
        int position = evt.getPosition();
        Motivos motivos = dbFuntions.getMotivo(listReporte.get(position).getCodmotivo());
        Proyecto proyecto = dbFuntions.getProyectoByCodMotivo(listReporte.get(position).getCodmotivo());
        Beneficiario beneficiario = dbFuntions.getBeneficiarioByDoc(listReporte.get(position).getDocumento());
        ArrayList<Capturas> capturas = dbFuntions.getCapturas(listReporte.get(position).getIdvisita());
        ArrayList<String> adjuntos = dbFuntions.getAdjuntos(listReporte.get(position).getIdvisita());

        Bundle bundle = new Bundle();
        bundle.putSerializable("motivo", motivos);
        bundle.putSerializable("proyecto", proyecto);
        bundle.putSerializable("beneficiario", beneficiario);
        bundle.putParcelableArrayList("capturas", capturas);
        bundle.putStringArrayList("adjuntos", adjuntos);
        bundle.putInt("tipovisita", VISITA_CORREGIR.getCodigo());
        bundle.putString("informe", listReporte.get(position).getComentario());
        bundle.putLong("idvisitarestore", listReporte.get(position).getIdvisita());
        bundle.putString("id_key_rel", listReporte.get(position).getId_key_rel());
        if (proyecto.getTipo() == 3) {
            bundle.putString("entidad_relevar", proyecto.getEntidad_relevar());
            startActivity(new Intent(getApplicationContext(), VisitaRelevamientoActivity.class).putExtras(bundle));
        } else {
            Intent intent = new Intent(getApplicationContext(), VisitaActivity.class).putExtras(bundle);
            startActivity(intent);
        }
    }

    private void enviarPendientes() {
        if (!isMyServiceRunning(SendDataService2.class, getApplicationContext())) {
            startService(new Intent(getApplicationContext(), SendDataService2.class));
        }
    }
}