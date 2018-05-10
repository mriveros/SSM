package com.stp.ssm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.stp.ssm.Evt.NewSubFormularioEvt;
import com.stp.ssm.Evt.ValidatePreguntaEvt;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.Model.Secciones;
import com.stp.ssm.Model.SubFormulario;
import com.stp.ssm.Model.TotalResult;
import com.stp.ssm.Util.ValidUtil;
import com.stp.ssm.View.SeccionView;
import com.stp.ssm.View.ViewFactory;

import java.util.ArrayList;

import static android.util.Log.i;
import static android.view.View.OnClickListener;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams;
import static com.stp.ssm.Model.Pregunta.TIPO;
import static com.stp.ssm.Model.Pregunta.TIPO.CHECKBOX;
import static com.stp.ssm.Model.Pregunta.TIPO.CUADRO_TEXTO_EMAIL;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_sub_formulario;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.toast_msj_email_valid;
import static com.stp.ssm.R.string.toast_msj_err_requerido;
import static com.stp.ssm.Util.ValidUtil.isValidEmail;
import static com.stp.ssm.View.ViewFactory.notificacionToast;


public class SubFormularioActivity extends BaseActivity {

    private LinearLayout linearContenido;
    private Button btnFin;
    private Button btnCancelar;
    private Secciones secciones;
    private int posicion;
    private ArrayList<TotalResult> totales;
    private ArrayList<Integer> arr_views;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_sub_formulario);
        setTitle("SUBFORMULARIO");
        inicializar();

        eventBus.register(this);
        posicion = getIntent().getExtras().getInt("posicion");
        secciones = (Secciones) getIntent().getExtras().getSerializable("seccion");
        totales = (ArrayList<TotalResult>) getIntent().getExtras().getSerializable("totales");
        linearContenido = (LinearLayout) findViewById(id.linearContenido);
        btnFin = (Button) findViewById(id.btnFin);
        btnCancelar = (Button) findViewById(id.btnCancelar);

        cargarSeccion();
        asignarEventos();
    }


    private void cargarSeccion() {
        linearContenido.removeAllViews();
        SeccionView seccionView = new SeccionView(getApplicationContext(), getSupportFragmentManager());
        LayoutParams params = new LayoutParams(MATCH_PARENT,
                MATCH_PARENT);
        seccionView.setLayoutParams(params);
        seccionView.cargarPreguntas(secciones.getPreguntas(), null, 0);
        linearContenido.addView(seccionView);

        arr_views = new ArrayList<>();
        for (int i = 0; i < linearContenido.getChildCount(); i++) {
            arr_views.add(linearContenido.getChildAt(i).getId());
        }
    }


    private void asignarEventos() {

        btnFin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarSeccion(secciones)) {
                    if (secciones.addSubFormularios(new SubFormulario(Integer.toString(secciones.getCodSeccion()), secciones.getPreguntas()))) {
                        i("Subformularios", secciones.getSubFormularios().toString());
                        eventBus.post(new NewSubFormularioEvt(posicion, secciones.getCodSeccion(), secciones.getSubFormularios()));
                    } else {
                        notificacionToast(getApplicationContext(), "Los totales no Coinciden");
                    }
                    finish();
                }
            }
        });

        btnCancelar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private boolean validarSeccion(Secciones secciones) {
        for (Pregunta pregunta : secciones.getPreguntas()) {
            if (pregunta.isRequerido()) {
                if (pregunta.getTipo().equals(CHECKBOX)) {
                    if (pregunta.getSelecresp().isEmpty()) {
                        notificacionToast(getApplicationContext(), getString(toast_msj_err_requerido));
                        secciones.setHasResponse(false);
                        return false;
                    }
                } else if (pregunta.getTipo().equals(CUADRO_TEXTO_EMAIL) && !isValidEmail(pregunta.getTxtrespuesta())) {
                    notificacionToast(getApplicationContext(), getResources().getString(toast_msj_email_valid));
                    secciones.setHasResponse(false);
                    return false;
                } else {
                    if (pregunta.getTxtrespuesta().equals("")) {
                        notificacionToast(getApplicationContext(), getString(toast_msj_err_requerido));
                        secciones.setHasResponse(false);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void onEvent(ValidatePreguntaEvt evt) {
        for (Pregunta pregunta : secciones.getPreguntas()) {
            if (pregunta.getIdpregunta() == evt.getIdpregunta()) {
                if (pregunta.isVisible()) {
                    if (!evt.isVisible()) {
                        pregunta.setVisible(false);
                        cargarSeccion();
                    }
                } else {
                    if (evt.isVisible()) {
                        pregunta.setVisible(true);
                        cargarSeccion();
                    }
                }
                break;
            }
        }
    }
}
