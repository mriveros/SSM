package com.stp.ssm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.util.Log.i;
import static android.widget.AdapterView.OnItemSelectedListener;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_select_galeria;

public class SelectGaleriaActivity extends BaseActivity {

    private GridView grid_img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_select_galeria);
        inicializar();

        grid_img = (GridView) findViewById(id.grid_img);
        cargarImagenes();
        asignarEventos();
    }

    private void cargarImagenes() {
        String path = DIRECTORY_DCIM;
        i("Path", path);
        /*File directory = new File(path);
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            Log.d("Files", "FileName:" + files[i].getName());
        }*/
    }

    private void asignarEventos() {
        grid_img.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
