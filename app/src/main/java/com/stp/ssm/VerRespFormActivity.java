package com.stp.ssm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ExpandableListView;

import com.stp.ssm.Adapters.VerFormularioAdaptares;
import com.stp.ssm.Model.Secciones;

import java.util.ArrayList;

import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_ver_respuestas;


public class VerRespFormActivity extends BaseActivity {
    ///Variables
    private ExpandableListView lst_ex_respuestas;
    private ArrayList<Secciones> arrsecciones;
    private VerFormularioAdaptares adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_ver_respuestas);
        inicializar();

        lst_ex_respuestas = (ExpandableListView) findViewById(id.lst_ex_respuestas);
        int codmotivo = getIntent().getExtras().getInt("codmotivo");
        long idvisita = getIntent().getExtras().getLong("idvisita");
        setTitle(getIntent().getExtras().getString("formulario"));
        cargarDatos(codmotivo, idvisita);
    }


    private void cargarDatos(int codmotivo, long idvisita) {
        arrsecciones = dbFuntions.getSecciones(codmotivo, idvisita);
        adapter = new VerFormularioAdaptares(arrsecciones, getApplicationContext(), idvisita);
        lst_ex_respuestas.setAdapter(adapter);
    }
}
