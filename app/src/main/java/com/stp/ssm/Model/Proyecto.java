package com.stp.ssm.Model;
//Created by desarrollo on 05/05/16.

import java.io.Serializable;
import java.util.ArrayList;

public class Proyecto implements Serializable {

    private String descripcion;
    private String codigo;
    private String institucion;
    private String institucionNom;
    private int tipo;
    private ArrayList<Motivos> motivos;
    private int cant_min_img;
    private int alta_destinatarios;
    private String entidad_relevar;

    public Proyecto(String descripcion, String codigo, String institucion, String institucionNom, ArrayList<Motivos> motivos,
                    int tipo, int cant_min_img, int alta_destinatarios, String entidad_relevar) {
        this.descripcion = descripcion;
        this.institucionNom = institucionNom;
        this.codigo = codigo;
        this.institucion = institucion;
        this.motivos = motivos;
        this.tipo = tipo;
        this.cant_min_img = cant_min_img;
        this.alta_destinatarios = alta_destinatarios;
        this.entidad_relevar = entidad_relevar;
    }

    public Proyecto(String descripcion, String codigo, String institucionNom, int tipo,
                    int cant_min_img, int alta_destinatarios, String entidad_relevar) {
        this.descripcion = descripcion;
        this.codigo = codigo;
        this.institucionNom = institucionNom;
        this.tipo = tipo;
        this.cant_min_img = cant_min_img;
        this.alta_destinatarios = alta_destinatarios;
        this.entidad_relevar = entidad_relevar;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getInstitucion() {
        return institucion;
    }

    public ArrayList<Motivos> getMotivos() {
        return motivos;
    }

    public String getInstitucionNom() {
        return institucionNom;
    }

    public int getTipo() {
        return tipo;
    }

    public int getCant_min_img() {
        return cant_min_img;
    }

    public int getAlta_destinatarios() {
        return alta_destinatarios;
    }

    public String getEntidad_relevar() {
        return entidad_relevar;
    }

    @Override
    public String toString() {
        return descripcion;
    }

    public enum TIPO_PROYECTO {
        ENCUESTA(1),
        MONITOREO(2),
        RELEVAMIENTO(3),
        CAPACITACION_MULTIMEDIA(4);

        private final int codigo;

        TIPO_PROYECTO(int codigo) {
            this.codigo = codigo;
        }

        public int getCodigo() {
            return codigo;
        }
    }
}