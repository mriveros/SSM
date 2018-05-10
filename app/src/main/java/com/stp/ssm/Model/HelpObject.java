package com.stp.ssm.Model;

import android.view.View;

public class HelpObject {

    private View view;
    private String texto;
    private boolean rectangular;

    public HelpObject(View view, String texto, boolean rectangular) {
        this.view = view;
        this.texto = texto;
        this.rectangular = rectangular;
    }

    public View getView() {
        return view;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isRectangular() {
        return rectangular;
    }
}
