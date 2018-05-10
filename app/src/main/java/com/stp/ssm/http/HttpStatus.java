package com.stp.ssm.http;

import com.stp.ssm.Evt.DescargaBeneficiarioEvt;

public enum HttpStatus {
    GENERIC(0, "GENERIC"),
    OK(200, "OK"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    CONFLICT(409, "CONFLICT"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR");

    int codigo;
    String descripcion;


    HttpStatus(int codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }


    public int getCodigo() {
        return codigo;
    }


    public String getDescripcion() {
        return descripcion;
    }

    public static HttpStatus findByCodigo(int codigo) {
        for (HttpStatus tipo : values()) {
            if (tipo.codigo == codigo) {
                return tipo;
            }
        }
        return null;
    }
}
