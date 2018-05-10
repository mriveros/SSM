package com.stp.ssm.View;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.stp.ssm.Adapters.ListaBeneficiariosAdapter;
import com.stp.ssm.Evt.RemoveMiembroFamEvt;
import com.stp.ssm.Interfaces.OnAddPersonaListener;
import com.stp.ssm.Interfaces.OnClickDialogListener;
import com.stp.ssm.Interfaces.OnDeleteListener;
import com.stp.ssm.Interfaces.OnSetFamiliaSimpleListener;
import com.stp.ssm.Model.Beneficiario;
import com.stp.ssm.R;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.widget.CompoundButton.OnCheckedChangeListener;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout.view_list_subformulario;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.dialog_msg_aceptar_sin_familia;
import static com.stp.ssm.R.string.dialog_title_sin_familia;
import static de.greenrobot.event.EventBus.getDefault;

public class LstAddPersonaView extends LinearLayout {

    private ListView lstPersonas;
    private Button btnAgregar;
    private OnAddPersonaListener onAddPersonaListener;
    private OnSetFamiliaSimpleListener onSetFamiliaSimpleListener;
    private ListaBeneficiariosAdapter adapter;
    private EventBus eventBus;
    private ToggleButton tgbtnSinFam;
    private DialogAcpView dialogAcpView;
    private FragmentManager fragmentManager;

    public LstAddPersonaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar();
    }

    public LstAddPersonaView(Context context) {
        super(context);
        inicializar();
    }

    public LstAddPersonaView(Context context, FragmentManager fragmentManager) {
        super(context);
        this.fragmentManager = fragmentManager;
        inicializar();
    }

    private void inicializar() {
        String infService = LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(view_list_subformulario, this, true);

        eventBus = getDefault();
        lstPersonas = (ListView) findViewById(id.lstPersonas);
        btnAgregar = (Button) findViewById(id.btnAgregar);
        tgbtnSinFam = (ToggleButton) findViewById(id.tgbtnSinFam);
        asignarEventos();
    }

    private void asignarEventos() {
        btnAgregar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddPersonaListener.OnAddPersona();
            }
        });

        tgbtnSinFam.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialogAcpView = new DialogAcpView(getContext().getString(dialog_title_sin_familia),
                            getContext().getString(dialog_msg_aceptar_sin_familia));
                    dialogAcpView.setOnClickDialogListener(new OnClickDialogListener() {
                        @Override
                        public void OnPositiveClick(DialogInterface dialog, String tag) {
                            btnAgregar.setEnabled(false);
                            lstPersonas.setVisibility(INVISIBLE);
                            onSetFamiliaSimpleListener.OnSetFamiliaSimple(1);
                            dialogAcpView.dismiss();
                        }

                        @Override
                        public void OnNegativeClick(DialogInterface dialog, String tag) {
                            tgbtnSinFam.setChecked(false);
                            dialogAcpView.dismiss();
                        }
                    });
                    dialogAcpView.show(fragmentManager, "");
                } else {
                    btnAgregar.setEnabled(true);
                    lstPersonas.setVisibility(VISIBLE);
                    onSetFamiliaSimpleListener.OnSetFamiliaSimple(0);
                }
            }
        });
    }

    public void setLisBeneficiario(ArrayList<Beneficiario> beneficiarios, final int seccionPosition) {
        if (beneficiarios != null) {
            adapter = new ListaBeneficiariosAdapter(beneficiarios, getContext(), false);
            lstPersonas.setAdapter(adapter);
            adapter.setOnDeleteListener(new OnDeleteListener() {
                @Override
                public void OnDelete(int position) {
                    eventBus.post(new RemoveMiembroFamEvt(seccionPosition, position));
                }
            });
        }
    }

    public void setOnAddPersonaListener(OnAddPersonaListener onAddPersonaListener) {
        this.onAddPersonaListener = onAddPersonaListener;
    }

    public void setOnSetFamiliaSimpleListener(OnSetFamiliaSimpleListener onSetFamiliaSimpleListener) {
        this.onSetFamiliaSimpleListener = onSetFamiliaSimpleListener;
    }
}
