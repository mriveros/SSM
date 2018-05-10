package com.stp.ssm.Model;

import java.util.ArrayList;

public class Evento {

    private Coordenadas coordenadas;
    private String descripcion;
    private ArrayList<String> capturas;
    private String fecha;
    private int estado;

    public Evento(Coordenadas coordenadas, String descripcion, ArrayList<String> capturas, String fecha) {
        this.coordenadas = coordenadas;
        this.descripcion = descripcion;
        this.capturas = capturas;
        this.fecha = fecha;
    }

    public Evento(Coordenadas coordenadas, String descripcion, ArrayList<String> capturas, String fecha, int estado) {
        this.coordenadas = coordenadas;
        this.descripcion = descripcion;
        this.capturas = capturas;
        this.fecha = fecha;
        this.estado = estado;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public ArrayList<String> getCapturas() {
        return capturas;
    }

    public String getFecha() {
        return fecha;
    }

    public int getEstado() {
        return estado;
    }
}
