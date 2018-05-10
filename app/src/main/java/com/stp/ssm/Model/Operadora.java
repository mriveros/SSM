package com.stp.ssm.Model;

public class Operadora {
    private String nombre;
    private String serialSim;
    private String countryISO;
    private String operador;
    private String androidid;

    public Operadora(String nombre, String serialSim, String countryISO, String operador, String androidid) {
        this.nombre = nombre;
        this.serialSim = serialSim;
        this.countryISO = countryISO;
        this.operador = operador;
        this.androidid = androidid;
    }

    public String getNombre() {
        return nombre;
    }

    public String getSerialSim() {
        return serialSim;
    }

    public String getCountryISO() {
        return countryISO;
    }

    public String getOperador() {
        return operador;
    }

    public String getAndroidid() {
        return androidid;
    }

    @Override
    public String toString() {
        return "Operadora{" +
                "nombre='" + nombre + '\'' +
                ", serialSim='" + serialSim + '\'' +
                ", countryISO='" + countryISO + '\'' +
                ", operador='" + operador + '\'' +
                '}';
    }
}
