package com.stp.ssm.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Motivos implements Serializable {
    private int codmotivo;
    private String descripcionMotivo;
    private int codformulario;
    private String descripcionForm;
    private ArrayList<Secciones> secciones;
    private boolean hasformulario = false;
    private int proyecto;

    public Motivos(int codmotivo, String descripcionMotivo, int codformulario, String descripcionForm) {
        this.codmotivo = codmotivo;
        this.descripcionMotivo = descripcionMotivo;
        this.codformulario = codformulario;
        this.descripcionForm = descripcionForm;
    }

    public Motivos(int codmotivo, String descripcionMotivo, int codformulario, String descripcionForm, boolean hasformulario) {
        this.codmotivo = codmotivo;
        this.descripcionMotivo = descripcionMotivo;
        this.codformulario = codformulario;
        this.hasformulario = hasformulario;
        this.descripcionForm = descripcionForm;
    }

    public Motivos(int codmotivo, String descripcionMotivo, int codformulario, String descripcionForm, boolean hasformulario, int proyecto) {
        this.codmotivo = codmotivo;
        this.descripcionMotivo = descripcionMotivo;
        this.codformulario = codformulario;
        this.descripcionForm = descripcionForm;
        this.hasformulario = hasformulario;
        this.proyecto = proyecto;
    }

    public void setSecciones(ArrayList<Secciones> secciones) {
        if (secciones != null && !secciones.isEmpty()) {
            hasformulario = true;
        }
        this.secciones = secciones;
    }

    public int getCodmotivo() {
        return codmotivo;
    }

    public String getDescripcionMotivo() {
        return descripcionMotivo;
    }

    public int getCodformulario() {
        return codformulario;
    }

    public String getDescripcionForm() {
        return descripcionForm;
    }

    public ArrayList<Secciones> getSecciones() {
        return secciones;
    }

    public boolean hasFormulario() {
        return hasformulario;
    }

    public int getProyecto() {
        return proyecto;
    }

    @Override
    public String toString() {
        return descripcionMotivo;
    }
}
