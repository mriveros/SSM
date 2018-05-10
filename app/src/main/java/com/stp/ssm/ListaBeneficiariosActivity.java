package com.stp.ssm;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stp.ssm.Adapters.ListaBeneficiariosAdapter;
import com.stp.ssm.Model.Beneficiario;
import com.stp.ssm.Model.Proyecto;
import com.stp.ssm.Model.Visita;
import com.stp.ssm.View.DialogAlertSimple;

import java.util.ArrayList;

import static android.widget.AdapterView.OnItemClickListener;
import static com.stp.ssm.Model.Visita.TipoVisita;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_ASIGNADO;
import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_NUEVO_BENEFICIARIO;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.action_new;
import static com.stp.ssm.R.id.action_search;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_lista_beneficiarios;
import static com.stp.ssm.R.menu;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msg_alta_destinatario;
import static com.stp.ssm.R.string.dialog_title_alta_destinatario;
import static com.stp.ssm.R.string.title_lst_beneficiarios;

public class ListaBeneficiariosActivity extends BaseActivity implements OnQueryTextListener {

    private ListView lstBeneficiarios;
    private ArrayList<Beneficiario> listBeneficiarios;
    private ListaBeneficiariosAdapter adapter;
    private Proyecto proyecto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(activity_lista_beneficiarios);
        setTitle(getString(title_lst_beneficiarios));

        inicializar();
        proyecto = (Proyecto) getIntent().getExtras().getSerializable("proyecto");
        lstBeneficiarios = (ListView) findViewById(id.lstBeneficiarios);
        asignarEventos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatos();
    }

    private void cargarDatos() {
        listBeneficiarios = dbFuntions.getListaBeneficiarios(proyecto.getCodigo(), sessionData.getUsuario(), "");
        adapter = new ListaBeneficiariosAdapter(listBeneficiarios, getApplicationContext());
        lstBeneficiarios.setAdapter(adapter);
    }

    private void asignarEventos() {
        lstBeneficiarios.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("beneficiario", listBeneficiarios.get(position));
                bundle.putSerializable("proyecto", proyecto);
                bundle.putInt("tipovisita", VISITA_ASIGNADO.getCodigo());

                /*if(proyecto.getTipo()== Proyecto.TIPO_PROYECTO.CAPACITACION_MULTIMEDIA.getCodigo()){
                    startActivity(new Intent(getApplicationContext(), VisitaCapacitacionActivity.class).putExtras(bundle));
                }else{
                    startActivity(new Intent(getApplicationContext(), VisitaActivity.class).putExtras(bundle));
                }*/
                switch (proyecto.getTipo()) {
                    case 1:
                    case 2:
                        startActivity(new Intent(getApplicationContext(), VisitaActivity.class).putExtras(bundle));
                        break;
                    case 3:
                        bundle.putString("entidad_relevar", proyecto.getEntidad_relevar());
                        startActivity(new Intent(getApplicationContext(), VisitaRelevamientoActivity.class).putExtras(bundle));
                        break;
                    case 4:
                        startActivity(new Intent(getApplicationContext(), VisitaCapacitacionActivity.class).putExtras(bundle));
                        break;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu.mn_search, menu);

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
        if (item.getItemId() == action_new) {
            if (proyecto.getAlta_destinatarios() == 1) {
                Bundle bundle = new Bundle();
                bundle.putInt("tipovisita", VISITA_NUEVO_BENEFICIARIO.getCodigo());
                bundle.putSerializable("proyecto", proyecto);
                /*if(proyecto.getTipo()== Proyecto.TIPO_PROYECTO.CAPACITACION_MULTIMEDIA.getCodigo()){
                    startActivity(new Intent(getApplicationContext(),VisitaCapacitacionActivity.class).putExtras(bundle));
                }else{
                    startActivity(new Intent(getApplicationContext(),VisitaActivity.class).putExtras(bundle));
                }*/

                switch (proyecto.getTipo()) {
                    case 1:
                    case 2:
                        startActivity(new Intent(getApplicationContext(), VisitaActivity.class).putExtras(bundle));
                        break;
                    case 3:
                        bundle.putString("entidad_relevar", proyecto.getEntidad_relevar());
                        startActivity(new Intent(getApplicationContext(), VisitaRelevamientoActivity.class).putExtras(bundle));
                        break;
                    case 4:
                        bundle.putString("entidad_relevar", "");
                        startActivity(new Intent(getApplicationContext(), VisitaCapacitacionActivity.class).putExtras(bundle));
                        break;
                }
            } else {
                new DialogAlertSimple(getString(dialog_title_alta_destinatario), getString(dialog_msg_alta_destinatario)).show(getSupportFragmentManager(), "");
            }
        }
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        listBeneficiarios = dbFuntions.getListaBeneficiarios(proyecto.getCodigo(), sessionData.getUsuario(), query);
        adapter.actualizarDatos(listBeneficiarios);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        listBeneficiarios = dbFuntions.getListaBeneficiarios(proyecto.getCodigo(), sessionData.getUsuario(), newText);
        adapter.actualizarDatos(listBeneficiarios);
        return false;
    }
}
