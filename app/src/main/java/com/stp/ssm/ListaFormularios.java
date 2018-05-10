package com.stp.ssm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.stp.ssm.Model.Motivos;

import java.util.ArrayList;

import static android.widget.AdapterView.OnItemClickListener;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.lbl_spinne_item;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_lst_formulario;
import static com.stp.ssm.R.layout.spinner_item_1;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.title_formularios;


public class ListaFormularios extends BaseActivity {

    private ListView lstFormularios;
    private ArrayList<Motivos> motivos;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_lst_formulario);
        setTitle(getString(title_formularios));

        inicializar();
        lstFormularios = (ListView) findViewById(id.lstFormularios);
        cargarDatos();
        asignarEventos();
    }


    private void cargarDatos() {
        motivos = dbFuntions.getMotivosForm();
        lstFormularios.setAdapter(new ArrayAdapter<Motivos>(getApplicationContext(),
                spinner_item_1,
                lbl_spinne_item,
                motivos));
    }

    private void asignarEventos() {
        lstFormularios.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), RelevamientoActivity.class);
                intent.putExtra("motivo", motivos.get(position));
                startActivity(intent);
            }
        });
    }
}
