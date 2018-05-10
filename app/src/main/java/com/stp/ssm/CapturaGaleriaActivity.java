package com.stp.ssm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.GridView;
import com.stp.ssm.Adapters.CapturasAdapter;
import com.stp.ssm.Model.Capturas;

import org.json.JSONArray;
import java.util.ArrayList;

import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_capturas_galeria;

public class CapturaGaleriaActivity extends BaseActivity {

    private GridView grid_capturas;
    private long idvisita;
    private CapturasAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_capturas_galeria);
        inicializar();

        idvisita = getIntent().getExtras().getLong("idvisita");
        grid_capturas = (GridView) findViewById(id.grid_capturas);
        cargarImagenes(idvisita);
    }

    private void cargarImagenes(long idvisita) {
        ArrayList<Capturas> arr_capturas = dbFuntions.getCapturas(idvisita);
        JSONArray j_arr_capturas = convertJSONArray(arr_capturas);
        adapter = new CapturasAdapter(getApplicationContext(), j_arr_capturas);
        grid_capturas.setAdapter(adapter);
    }

    private JSONArray convertJSONArray(ArrayList<Capturas> array) {
        JSONArray jsonArray = new JSONArray();
        for (Capturas captura : array) {
            jsonArray.put(captura.getPath());
        }
        return jsonArray;
    }
}