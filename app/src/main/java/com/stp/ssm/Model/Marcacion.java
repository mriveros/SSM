package com.stp.ssm.Model;

import com.stp.ssm.Util.CoordenadasUtils;

public class Marcacion {

    private String hora_marcacion;
    private Coordenadas coordenadas;
    private TipoMarcacion tipoMarcacion;
    private int estado;

    public Marcacion(String hora_marcacion, Coordenadas coordenadas, TipoMarcacion tipoMarcacion, int estado) {
        this.hora_marcacion = hora_marcacion;
        this.coordenadas = coordenadas;
        this.tipoMarcacion = tipoMarcacion;
        this.estado = estado;
    }

    public String getHora_marcacion() {
        return hora_marcacion;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public int getEstado() {
        return estado;
    }

    public TipoMarcacion getTipoMarcacion() {
        return tipoMarcacion;
    }

    public enum TipoMarcacion {
        ENTRADA(3), SALIDA(4);

        private final int codigo;

        TipoMarcacion(int codigo) {
            this.codigo = codigo;
        }

        public int getCodigo() {
            return codigo;
        }

        public static TipoMarcacion findByCodigo(int codigo) {
            for (TipoMarcacion estado : values()) {
                if (estado.codigo == codigo) {
                    return estado;
                }
            }
            return null;
        }

    }
}
