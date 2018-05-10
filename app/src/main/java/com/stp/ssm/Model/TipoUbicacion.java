package com.stp.ssm.Model;

public enum TipoUbicacion {
    UBICACION_PERIODICA(0), INICIO_RECORRIDO(1), FIN_RECORRIDO(2), UBICACION_BENEFICIARIO(3);
    private final int codigo;

    TipoUbicacion(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static TipoUbicacion findByCodigo(int codigo) {
        for (TipoUbicacion estado : values()) {
            if (estado.codigo == codigo) {
                return estado;
            }
        }
        return null;
    }
}
