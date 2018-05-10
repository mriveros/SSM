package com.stp.ssm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.stp.ssm.Adapters.ListNotificacionesAdapter;
import com.stp.ssm.Model.Notificacion;
import com.stp.ssm.Servicios.NotificacionService;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.LayoutManager;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.list_history;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_history;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.lbl_btn_notificacion;


public class ListNotificationsActivity extends BaseActivity {

    private RecyclerView listNotificaciones;
    private LayoutManager mLayoutManager;
    private ArrayList<Notificacion> notificaciones;
    private ListNotificacionesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_history);

        setTitle(getString(lbl_btn_notificacion));
        inicializar();

        try {
            stopService(new Intent(getApplicationContext(), NotificacionService.class));
        } catch (Exception ex) {
        }

        listNotificaciones = (RecyclerView) findViewById(list_history);
        listNotificaciones.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        listNotificaciones.setLayoutManager(mLayoutManager);

        cargarDatos();
    }

    private void cargarDatos() {
        notificaciones = dbFuntions.getArrayNotificaciones();
        if (!notificaciones.isEmpty()) {
            adapter = new ListNotificacionesAdapter(notificaciones);
            listNotificaciones.setAdapter(adapter);
        }
    }
}
