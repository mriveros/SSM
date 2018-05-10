package com.stp.ssm.Model;

import com.stp.ssm.Evt.LoginResult;

import java.io.Serializable;

public class PreguntaCondicion implements Serializable {

    private long id_pregunta_condicionada;
    private int condicion;
    private String valor;

    public PreguntaCondicion(long id_pregunta_condicionada, int condicion, String valor) {
        this.id_pregunta_condicionada = id_pregunta_condicionada;
        this.condicion = condicion;
        this.valor = valor;
    }

    public long getId_pregunta_condicionada() {
        return id_pregunta_condicionada;
    }

    public int getCondicion() {
        return condicion;
    }

    public String getValor() {
        return valor;
    }

    public static enum Condiciones {
        IGUAL(1),
        MAYOR(2),
        MENOR(3),
        MAYOR_IGUAL(4),
        MENOR_IGUAL(5),
        DISTINTO(6);

        private final int codigo;

        Condiciones(int codigo) {
            this.codigo = codigo;
        }

        public int getCodigo() {
            return codigo;
        }

        public static Condiciones findByCodigo(int codigo) {
            for (Condiciones condiciones : values()) {
                if (condiciones.codigo == codigo) {
                    return condiciones;
                }
            }
            return null;
        }
    }
}
