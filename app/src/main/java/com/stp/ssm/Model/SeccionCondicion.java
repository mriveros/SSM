package com.stp.ssm.Model;

import java.io.Serializable;

public class SeccionCondicion implements Serializable {
    private long idpreguntacondicionante;
    private long idseccionsiguiente;
    private int condicion;
    private String valor;

    public SeccionCondicion(long idpreguntacondicionante, long idseccionsiguiente, int condicion, String valor) {
        this.idpreguntacondicionante = idpreguntacondicionante;
        this.idseccionsiguiente = idseccionsiguiente;
        this.condicion = condicion;
        this.valor = valor;
    }

    public long getIdpreguntacondicionante() {
        return idpreguntacondicionante;
    }

    public long getIdseccionsiguiente() {
        return idseccionsiguiente;
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

        public static PreguntaCondicion.Condiciones findByCodigo(int codigo) {
            for (PreguntaCondicion.Condiciones condiciones : PreguntaCondicion.Condiciones.values()) {
                if (condiciones.getCodigo() == codigo) {
                    return condiciones;
                }
            }
            return null;
        }
    }
}
