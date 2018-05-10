package com.stp.ssm.Model;

import java.io.Serializable;

public class PosiblesRespuestas implements Serializable {
    private int codigo;
    private String texto;

    public PosiblesRespuestas(int codigo, String texto) {
        this.codigo = codigo;
        this.texto = texto;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getTexto() {
        return texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
