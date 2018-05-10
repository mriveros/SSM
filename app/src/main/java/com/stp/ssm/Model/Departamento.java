package com.stp.ssm.Model;

import java.util.ArrayList;

public class Departamento {

    private String codigo;
    private String nombre;
    private ArrayList<Distrito> distritos;

    public Departamento(String codigo, String nombre, ArrayList<Distrito> distritos) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.distritos = distritos;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<Distrito> getDistritos() {
        return distritos;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
