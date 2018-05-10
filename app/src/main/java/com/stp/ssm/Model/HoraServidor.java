package com.stp.ssm.Model;

public class HoraServidor {
    private int anho;
    private int mes;
    private int dia;
    private int hora;
    private int minuto;

    public HoraServidor(int anho, int mes, int dia, int hora, int minuto) {
        this.anho = anho;
        this.mes = mes;
        this.dia = dia;
        this.hora = hora;
        this.minuto = minuto;
    }

    public int getAnho() {
        return anho;
    }

    public int getMes() {
        return mes;
    }

    public int getDia() {
        return dia;
    }

    public int getHora() {
        return hora;
    }

    public int getMinuto() {
        return minuto;
    }
}
