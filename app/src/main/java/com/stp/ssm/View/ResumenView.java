package com.stp.ssm.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stp.ssm.Model.TotalRelevado;
import com.stp.ssm.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.stp.ssm.R.id;
import static com.stp.ssm.R.layout.resumen_view;
import static com.stp.ssm.R.string;
import static com.stp.ssm.R.string.lbl_total_adjuntos;
import static com.stp.ssm.R.string.lbl_total_capturas;
import static com.stp.ssm.R.string.lbl_total_enviado;
import static com.stp.ssm.R.string.lbl_total_relevado;
import static com.stp.ssm.R.string.lbl_total_valid;

public class ResumenView extends LinearLayout {

    private TextView lblTotalRel;
    private TextView lblTotalEnv;
    private TextView lblTotalCapEnv;
    private TextView lblTotalAdjEvn;
    private TextView lblTotalCaptVerif;

    public ResumenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inicializar();
    }

    private void inicializar() {
        String infService = LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(resumen_view, this, true);

        lblTotalRel = (TextView) findViewById(id.lblTotalRel);
        lblTotalEnv = (TextView) findViewById(id.lblTotalEnv);
        lblTotalCapEnv = (TextView) findViewById(id.lblTotalCapEnv);
        lblTotalAdjEvn = (TextView) findViewById(id.lblTotalAdjEvn);
        lblTotalCaptVerif = (TextView) findViewById(id.lblTotalCaptVerif);
    }

    public void cargarDatos(int totalRelevado, int totalEnv, TotalRelevado totalCap, TotalRelevado totalAdj) {
        lblTotalRel.setText(getResources().getString(lbl_total_relevado) + totalRelevado);
        lblTotalEnv.setText(getResources().getString(lbl_total_enviado) + totalEnv);
        if (totalCap != null) {
            lblTotalCapEnv.setText(getResources().getString(lbl_total_capturas) + totalCap.getEnviado() + "/" + totalCap.getTotal());
        }

        if (totalAdj != null) {
            lblTotalAdjEvn.setText(getResources().getString(lbl_total_adjuntos) + totalAdj.getEnviado() + "/" + totalAdj.getTotal());
        }
    }

    public void setTotalCaptVerif(TotalRelevado total) {
        if (total != null) {
            lblTotalCaptVerif.setText(getResources().getString(lbl_total_valid) + total.getEnviado() + "/" + total.getTotal());
        }
    }
}
