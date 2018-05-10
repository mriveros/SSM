package com.stp.ssm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.stp.ssm.Adapters.ListVideoAdapter;
import com.stp.ssm.Interfaces.OnTimeCronometroListener;
import com.stp.ssm.Model.Motivos;
import com.stp.ssm.Model.Proyecto;
import com.stp.ssm.Model.Secciones;
import com.stp.ssm.Util.Cronometro;
import com.stp.ssm.Util.FechaUtil;
import com.stp.ssm.View.ViewFactory;

import java.util.ArrayList;

import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;
import static android.widget.AdapterView.OnItemClickListener;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.id.lbl_time_visita;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_video_capacitacion;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.toast_msj_seccion_complet;
import static com.stp.ssm.Util.FechaUtil.SegundoToHora;
import static com.stp.ssm.View.ViewFactory.notificacionToast;

public class VideoCapacitacionActivity extends BaseActivity {

    private ListView list_videos;
    private TextView lbl_time;
    private Button btn_finalizar_capacitacion;

    private long segundos = 0;
    private long idvisita;
    private String beneficiario;
    private int tipo_visita;
    private int tipo_destinatario;
    private ArrayList<Secciones> arr_secciones;

    private Motivos motivos;
    private Proyecto proyecto;
    private Cronometro crononemtro;
    private Handler handler = new Handler();
    private ListVideoAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_video_capacitacion);
        inicializar();


        lbl_time = (TextView) findViewById(lbl_time_visita);
        list_videos = (ListView) findViewById(id.list_videos);
        btn_finalizar_capacitacion = (Button) findViewById(id.btn_finalizar_capacitacion);

        Bundle bundle = getIntent().getExtras();
        segundos = bundle.getLong("segundos");
        inicializarConometro(segundos);
        idvisita = bundle.getLong("idvisita");
        motivos = (Motivos) bundle.getSerializable("motivo");
        motivos.setSecciones(dbFuntions.getSecciones(motivos.getCodmotivo(), idvisita));
        proyecto = (Proyecto) getIntent().getExtras().getSerializable("proyecto");
        beneficiario = bundle.getString("beneficiario");
        tipo_visita = bundle.getInt("tipo_visita");
        tipo_destinatario = bundle.getInt("tipo_destinatario");
        cargarDatos();
        asignarEventos();
    }


    private void cargarDatos() {
        arr_secciones = motivos.getSecciones();
        adapter = new ListVideoAdapter(arr_secciones, getApplicationContext());
        list_videos.setAdapter(adapter);
    }

    private void asignarEventos() {
        list_videos.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!arr_secciones.get(position).isHasResponse()) {
                    Intent intent = new Intent(getApplicationContext(), ViewVideoActivity.class);
                    intent.putExtra("video", arr_secciones.get(position).getMultimedia());
                    intent.putExtra("seccion", arr_secciones.get(position));
                    intent.putExtra("idvisita", idvisita);
                    intent.putExtra("beneficiario", beneficiario);
                    intent.putExtra("posicion", position);
                    startActivityForResult(intent, 100);
                } else {
                    notificacionToast(getApplicationContext(), getString(toast_msj_seccion_complet));
                }
            }
        });
        btn_finalizar_capacitacion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dbFuntions.finalizarVisita(idvisita, segundos);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private void inicializarConometro(long seg) {
        segundos = seg;
        ///Inicializa el cronometro
        if (crononemtro != null) {
            crononemtro.finalizar();
        }
        crononemtro = new Cronometro();
        crononemtro.setOnTimeCronometroListener(new OnTimeCronometroListener() {
            @Override
            public void OnTimeCronometro(final long time) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        segundos = time;
                        sessionData.setLastTimeVisita(segundos);
                        lbl_time.setText(SegundoToHora(segundos));
                    }
                }, 100);
            }
        });
        crononemtro.inicializar(segundos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                motivos.getSecciones().get(data.getExtras().getInt("posicion")).setHasResponse(true);
                boolean completado = true;
                for (Secciones secciones : motivos.getSecciones()) {
                    if (!secciones.isHasResponse()) {
                        completado = false;
                        break;
                    }
                }
                if (completado) {
                    btn_finalizar_capacitacion.setVisibility(VISIBLE);
                }
            }
        }
    }
}
