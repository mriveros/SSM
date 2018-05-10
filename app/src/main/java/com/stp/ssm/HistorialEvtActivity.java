package com.stp.ssm;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.stp.ssm.Adapters.ListHistoryAdapter;
import com.stp.ssm.Evt.SendDataSuccessful;
import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Model.Evento;
import com.stp.ssm.Servicios.SendDataService2;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.View.ViewFactory;
import com.stp.ssm.databases.DataBaseMaestro;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.LayoutManager;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.act_delete;
import static com.stp.ssm.R.id.act_send;
import static com.stp.ssm.R.id.list_history;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_history;
import static com.stp.ssm.R.menu;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msj_delete;
import static com.stp.ssm.R.string.dialog_title_delete;
import static com.stp.ssm.R.string.title_Historial;
import static com.stp.ssm.R.string.toast_msj_enviando_pendientes;
import static com.stp.ssm.Util.CellUtils.isMyServiceRunning;
import static com.stp.ssm.View.ViewFactory.notificacionToast;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_CAPTURA_EVENTOS;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_CAPTURA_EVENTOS_envio;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_EVENTOS;
import static com.stp.ssm.databases.DataBaseMaestro.TABLE_EVENTOS_envio;

public class HistorialEvtActivity extends BaseActivity {

    private TextView lblnroreportados;
    private TextView lblnrocapturas;
    private RecyclerView listHistory;
    private LayoutManager mLayoutManager;
    private ArrayList<Evento> listaEventos;
    private ListHistoryAdapter adapter;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(activity_history);
        setTitle(getString(title_Historial));
        inicializar();

        listHistory = (RecyclerView) findViewById(list_history);
        listHistory.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        listHistory.setLayoutManager(mLayoutManager);
        lblnroreportados = (TextView) findViewById(id.lblnroreportados);
        lblnrocapturas = (TextView) findViewById(id.lblnrocapturas);

        cargarResumen();
        cargarDatos();
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
                notificacionToast(getApplicationContext(), getString(toast_msj_enviando_pendientes));
                enviarPendientes();
                break;
            case act_delete:
                DialogAcpView dialog = new DialogAcpView(getString(dialog_title_delete), getString(dialog_msj_delete));
                dialog.setOnClickDialogListener(new OnClickDialogListener() {
                    @Override
                    public void OnPositiveClick(DialogInterface dialog, String tag) {
                        dbFuntions.limpiarEnviadosEvent();
                        ((ListHistoryAdapter) listHistory.getAdapter()).notifyData();
                        dialog.dismiss();
                        cargarDatos();
                    }

                    @Override
                    public void OnNegativeClick(DialogInterface dialog, String tag) {
                        dialog.dismiss();
                    }
                });
                dialog.show(getSupportFragmentManager(), "");
                break;
        }
        return true;
    }


    private void cargarDatos() {
        listaEventos = dbFuntions.getArrayEventos();
        if (!listaEventos.isEmpty()) {
            adapter = new ListHistoryAdapter(listaEventos, getSupportFragmentManager(), getApplicationContext());
            listHistory.setAdapter(adapter);
        }
    }


    private void cargarResumen() {
        int total = dbFuntions.cantidadRegistro(TABLE_EVENTOS, "");
        int enviado = dbFuntions.cantidadRegistro(TABLE_EVENTOS,
                TABLE_EVENTOS_envio + ">0");
        lblnroreportados.setText(enviado + "/" + total);

        total = dbFuntions.cantidadRegistro(TABLE_CAPTURA_EVENTOS, "");
        enviado = dbFuntions.cantidadRegistro(TABLE_CAPTURA_EVENTOS,
                TABLE_CAPTURA_EVENTOS_envio + ">0");
        lblnrocapturas.setText(enviado + "/" + total);
    }


    public void onEvent(SendDataSuccessful evt) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                cargarResumen();
                cargarDatos();
            }
        }, 100);
    }

    private void enviarPendientes() {
        if (!isMyServiceRunning(SendDataService2.class, getApplicationContext())) {
            startService(new Intent(getApplicationContext(), SendDataService2.class));
        }
    }
}