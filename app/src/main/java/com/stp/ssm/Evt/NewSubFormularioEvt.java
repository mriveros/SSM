package com.stp.ssm.Evt;

import com.stp.ssm.Model.SubFormulario;

import java.util.ArrayList;

public class NewSubFormularioEvt {

    private int posicion;
    private int codigoSeccion;
    private ArrayList<SubFormulario> subFormularios;

    public NewSubFormularioEvt(int posicion, int codigoSeccion, ArrayList<SubFormulario> subFormularios) {
        this.posicion = posicion;
        this.codigoSeccion = codigoSeccion;
        this.subFormularios = subFormularios;
    }

    public int getPosicion() {
        return posicion;
    }


    public int getCodigoSeccion() {
        return codigoSeccion;
    }


    public ArrayList<SubFormulario> getSubFormularios() {
        return subFormularios;
    }
}
