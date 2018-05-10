package com.stp.ssm;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Model.Pregunta;
import com.stp.ssm.Model.Secciones;
import com.stp.ssm.Util.ValidUtil;
import com.stp.ssm.View.DialogAcpView;
import com.stp.ssm.View.SeccionView;
import com.stp.ssm.View.ViewFactory;

import static android.view.View.OnClickListener;
import static com.stp.ssm.Model.Pregunta.TIPO;
import static com.stp.ssm.Model.Pregunta.TIPO.CHECKBOX;
import static com.stp.ssm.Model.Pregunta.TIPO.CUADRO_TEXTO_EMAIL;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout;
import static com.stp.ssm.R.layout.activity_simple_formulario;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msg_repetir;
import static com.stp.ssm.R.string.dialog_title_repetir;
import static com.stp.ssm.R.string.lbl_aceptar;
import static com.stp.ssm.R.string.lbl_cancelar;
import static com.stp.ssm.R.string.toast_msj_email_valid;
import static com.stp.ssm.R.string.toast_msj_err_requerido;
import static com.stp.ssm.Util.ValidUtil.isValidEmail;
import static com.stp.ssm.View.ViewFactory.notificacionToast;

public class FormularioSimpleActivity extends BaseActivity {

    private SeccionView seccion_view;
    private LinearLayout layout_secciones;
    private Button btn_finalizar;
    private Secciones seccion;
    private Long idvisita;
    private String beneficiario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_simple_formulario);
        inicializar();

        idvisita = getIntent().getExtras().getLong("idvisita");
        beneficiario = getIntent().getExtras().getString("beneficiario");
        seccion = (Secciones) getIntent().getExtras().getSerializable("seccion");
        seccion_view = new SeccionView(getApplicationContext(), getSupportFragmentManager());
        layout_secciones = (LinearLayout) findViewById(id.layout_secciones);
        layout_secciones.addView(seccion_view);
        btn_finalizar = (Button) findViewById(id.btn_finalizar);

        cargaDatos();
        asignarEventos();
    }

    private void cargaDatos() {
        seccion_view.cargarPreguntas(seccion.getPreguntas(), seccion.getCondicionsSiguiente(), seccion.getCodSeccion());
    }

    private void asignarEventos() {
        btn_finalizar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarSeccion(seccion)) {
                    dbFuntions.borrarRespuestas(seccion.getPreguntas(), idvisita);
                    dbFuntions.guardarRespuestas(seccion.getPreguntas(), idvisita, beneficiario, 0);
                    seccion.setHasResponse(true);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private boolean validarSeccion(Secciones secciones) {
        for (Pregunta pregunta : secciones.getPreguntas()) {
            if (pregunta.isRequerido() && pregunta.isVisible()) {
                if (pregunta.getTipo().equals(CHECKBOX)) {
                    if (pregunta.getSelecresp().isEmpty()) {
                        notificacionToast(getApplicationContext(), getString(toast_msj_err_requerido));
                        return false;
                    }
                } else if (pregunta.getTipo().equals(CUADRO_TEXTO_EMAIL) && !isValidEmail(pregunta.getTxtrespuesta())) {
                    notificacionToast(getApplicationContext(), getResources().getString(toast_msj_email_valid));
                    return false;
                } else {
                    if (pregunta.getTxtrespuesta().equals("")) {
                        notificacionToast(getApplicationContext(), getString(toast_msj_err_requerido));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DialogAcpView dialogAcpView = new DialogAcpView(getString(dialog_title_repetir),
                getString(dialog_msg_repetir),
                getString(lbl_aceptar),
                getString(lbl_cancelar));
        dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
            @Override
            public void OnPositiveClick(DialogInterface dialog, String tag) {
                setResult(RESULT_CANCELED);
                finish();
            }

            @Override
            public void OnNegativeClick(DialogInterface dialog, String tag) {
                dialog.dismiss();
            }
        });
        dialogAcpView.show(getSupportFragmentManager(), "");
    }
}
