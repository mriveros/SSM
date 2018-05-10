package com.stp.ssm;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stp.ssm.Adapters.ListaBeneficiariosAdapter;
import com.stp.ssm.Evt.AddMiembroFamiliaEvt;
import com.stp.ssm.Model.Beneficiario;

import java.util.ArrayList;

import static android.support.v7.widget.SearchView.OnQueryTextListener;
import static android.widget.AdapterView.OnItemClickListener;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.action_search;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_lista_beneficiarios;
import static com.stp.ssm.R.menu;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.title_lst_beneficiarios;

public class BuscarPersonaActivity extends BaseActivity implements OnQueryTextListener {

    private ListView lstBeneficiarios;
    private ArrayList<Beneficiario> listBeneficiarios;
    private ListaBeneficiariosAdapter adapter;
    private ArrayList<String> cedulas;
    private int sw = 0;
    private int codproyecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(activity_lista_beneficiarios);
        setTitle(getString(title_lst_beneficiarios));

        inicializar();
        sw = getIntent().getExtras().getInt("sw");
        cedulas = getIntent().getStringArrayListExtra("ArrCedulas");
        codproyecto = getIntent().getExtras().getInt("codproyecto");
        lstBeneficiarios = (ListView) findViewById(id.lstBeneficiarios);
        asignarEventos();
    }

    protected void onResume() {
        super.onResume();
        cargarDatos();
    }


    private void cargarDatos() {
        listBeneficiarios = dbFuntions.getListaBeneficiarios(sessionData.getUsuario(), "", cedulas, codproyecto);
        adapter = new ListaBeneficiariosAdapter(listBeneficiarios, getApplicationContext());
        lstBeneficiarios.setAdapter(adapter);
    }


    private void asignarEventos() {
        lstBeneficiarios.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                if (sw == 0) {
                    intent.putExtra("beneficiario", listBeneficiarios.get(position));
                    setResult(RESULT_OK, intent);
                } else {
                    eventBus.post(new AddMiembroFamiliaEvt(getIntent().getExtras().getInt("posicion"),
                            listBeneficiarios.get(position)));
                }
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu.mn_search_1, menu);

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        /////
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listBeneficiarios = dbFuntions.getListaBeneficiarios(sessionData.getUsuario(), query, cedulas, codproyecto);
        adapter.actualizarDatos(listBeneficiarios);
        return false;
    }


    @Override
    public boolean onQueryTextChange(String newText) {
        listBeneficiarios = dbFuntions.getListaBeneficiarios(sessionData.getUsuario(), newText, cedulas, codproyecto);
        adapter.actualizarDatos(listBeneficiarios);
        return false;
    }
}
