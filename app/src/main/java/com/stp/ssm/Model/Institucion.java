package com.stp.ssm.Model;

public class Institucion {
    private final String descripcion;
    private final String codigo;

    public Institucion(String descripcion, String codigo) {
        this.descripcion = descripcion;
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
