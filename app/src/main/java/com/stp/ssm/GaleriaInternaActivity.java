package com.stp.ssm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.stp.ssm.Adapters.CapturasAdapter;
import com.stp.ssm.AsyncTask.SendImagenesAsyncTask;
import com.stp.ssm.Interfaces.OnSendImgFinishListener;
import com.stp.ssm.Model.Capturas;
import com.stp.ssm.Servicios.NotificacionService;
import com.stp.ssm.Util.CellUtils;
import com.stp.ssm.Util.ImageFileUtil;
import com.stp.ssm.Util.SessionData;
import com.stp.ssm.View.ViewFactory;
import com.stp.ssm.databases.BDFuntions;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;
import static android.widget.AdapterView.OnItemSelectedListener;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.act_sync;
import static com.stp.ssm.R.id.lbl_spinne_item;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_galeria_interna;
import static com.stp.ssm.R.layout.spinner_item_1;
import static com.stp.ssm.R.menu;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msj_progress_run;
import static com.stp.ssm.R.string.help_btn_menu_sycn;
import static com.stp.ssm.R.string.help_title_sycn;
import static com.stp.ssm.R.string.lbl_sp_img_db;
import static com.stp.ssm.R.string.lbl_sp_img_device;
import static com.stp.ssm.R.string.title_galeria;
import static com.stp.ssm.R.string.toast_msj_sin_con;
import static com.stp.ssm.Util.CellUtils.checkNetworkConnect;
import static com.stp.ssm.Util.ImageFileUtil.PATH_GALLERY;
import static com.stp.ssm.Util.ImageFileUtil.getListOfFile;
import static com.stp.ssm.Util.SessionData.getInstance;
import static com.stp.ssm.View.ViewFactory.notificacionToast;
import static com.takusemba.spotlight.SimpleTarget.Builder;
import static com.takusemba.spotlight.Spotlight.with;


public class GaleriaInternaActivity extends BaseActivity {

    private Spinner sp_imagenes;
    private GridView grid_galeria_interna;
    private CapturasAdapter adapter;
    private List<String> arr_imagenes;
    private BDFuntions dbFuntions;
    private SessionData sessionData;
    private String options[];

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_galeria_interna);
        setTitle(getString(title_galeria));

        try {
            stopService(new Intent(getApplicationContext(), NotificacionService.class));
        } catch (Exception ex) {
        }

        options = new String[]{getString(lbl_sp_img_device),
                getString(lbl_sp_img_db)};

        sessionData = getInstance(getApplicationContext());
        dbFuntions = new BDFuntions(getApplicationContext());
        sp_imagenes = (Spinner) findViewById(id.sp_imagenes);
        sp_imagenes.setAdapter(new ArrayAdapter<String>(this, spinner_item_1,
                lbl_spinne_item,
                options));
        grid_galeria_interna = (GridView) findViewById(id.grid_galeria_interna);

        if (sessionData.isFistLoad(GaleriaInternaActivity.class.getName())) {
            displayTuto();
        }
        asignarEventos();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu.menu_sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == act_sync) {
            if (checkNetworkConnect(getApplicationContext())) {
                SendImagenesAsyncTask sendImagenesAsyncTask = new SendImagenesAsyncTask(getApplicationContext());
                showProgressDialog("", getString(dialog_msj_progress_run));
                sendImagenesAsyncTask.setOnSendImgFinishListener(new OnSendImgFinishListener() {
                    @Override
                    public void OnSendImgFinish() {
                        offProgressDialog();
                        cargarImagenes();
                    }
                });
                sendImagenesAsyncTask.execute(arr_imagenes);
            } else {
                notificacionToast(getApplicationContext(), getString(toast_msj_sin_con));
            }
        }
        return true;
    }

    private void asignarEventos() {
        sp_imagenes.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    arr_imagenes = getListOfFile(getExternalStorageDirectory() + PATH_GALLERY);
                    adapter = new CapturasAdapter(getApplicationContext(), new JSONArray(arr_imagenes));
                } else {
                    ArrayList<Capturas> arr_capturas = dbFuntions.getCapturas();
                    JSONArray j_arr_capturas = convertJSONArray(arr_capturas);
                    adapter = new CapturasAdapter(getApplicationContext(), j_arr_capturas);
                }
                grid_galeria_interna.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void cargarImagenes() {
        if (sp_imagenes.getSelectedItemPosition() == 0) {
            arr_imagenes = getListOfFile(getExternalStorageDirectory() + PATH_GALLERY);
            adapter = new CapturasAdapter(getApplicationContext(), new JSONArray(arr_imagenes));
        } else {
            ArrayList<Capturas> arr_capturas = dbFuntions.getCapturas();
            JSONArray j_arr_capturas = convertJSONArray(arr_capturas);
            adapter = new CapturasAdapter(getApplicationContext(), j_arr_capturas);
        }
        grid_galeria_interna.setAdapter(adapter);
    }

    private JSONArray convertJSONArray(ArrayList<Capturas> array) {
        JSONArray jsonArray = new JSONArray();
        for (Capturas captura : array) {
            jsonArray.put(captura.getPath());
        }
        return jsonArray;
    }

    private void displayTuto() {
        sessionData.setFistLoad(GaleriaInternaActivity.class.getName());
        SimpleTarget simpleTarget = new Builder(this)
                .setPoint(1000f, 200f)
                .setRadius(100f)
                .setTitle(getString(help_title_sycn))
                .setDescription(getString(help_btn_menu_sycn))
                .build();

        with(this)
                .setDuration(1000L)
                .setAnimation(new DecelerateInterpolator(2f))
                .setTargets(simpleTarget)
                .start();
    }
}