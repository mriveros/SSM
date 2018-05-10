package com.stp.ssm.Model;

import java.io.Serializable;

public class TotalResult implements Serializable {
    private String descripcion;
    private int total;
    int posicion;

    public TotalResult(String descripcion, int total, int posicion) {
        this.descripcion = descripcion;
        this.total = total;
        this.posicion = posicion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPosicion() {
        return posicion;
    }
}
