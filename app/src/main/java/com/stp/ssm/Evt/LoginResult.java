package com.stp.ssm.Evt;

import com.stp.ssm.Model.Institucion;
import java.util.ArrayList;

public class LoginResult {


    private boolean result = false;

    private String usuario;

    private ArrayList<Institucion> instituciones;

    private String errorRazon;

    private String iddistrito;

    private String iddepartamento;
    private int nivel;

    public LoginResult(String usuario, ArrayList<Institucion> instituciones, String iddistrito, String iddepartamento, int nivel) {
        this.result = true;
        this.usuario = usuario;
        this.instituciones = instituciones;
        this.iddepartamento = iddepartamento;
        this.iddistrito = iddistrito;
        this.nivel = nivel;
    }


    public LoginResult(String errorRazon) {
        this.result = false;
        this.errorRazon = errorRazon;
    }


    public String getUsuario() {
        return usuario;
    }


    public ArrayList<Institucion> getInstituciones() {
        return instituciones;
    }


    public String getErrorRazon() {
        return errorRazon;
    }

    public String getIddistrito() {
        return iddistrito;
    }


    public String getIddepartamento() {
        return iddepartamento;
    }


    public boolean isOK() {
        return result;
    }

    public int getNivel() {
        return nivel;
    }


    public static enum TipoRespuesta {
        OK(0, "OK"),
        ERROR(1, "Usuario o Contraseña Incorrecto. Reintente"),
        USUARIO_DESHABILITADO(2, "El Usuario esta deshabilitado"),
        USUARIO_ENUSO(3, "El Usuario se encuentra en uso"),
        ERROR_SERVIDOR(4, "Error de Servidor"),
        ERROR_PARSEO(5, "Respuesta Desconocida"),
        ERROR_CONEXION(6, "Error al tratar de conectar al Servidor");

        private final int codigo;
        private final String descripcion;

        TipoRespuesta(int codigo, String descripcion) {
            this.codigo = codigo;
            this.descripcion = descripcion;
        }


        public int getCodigo() {
            return codigo;
        }


        public String getDescripcion() {
            return descripcion;
        }


        public static TipoRespuesta findByCodigo(int codigo) {
            for (TipoRespuesta tipo : values()) {
                if (tipo.codigo == codigo) {
                    return tipo;
                }
            }
            return null;
        }
    }
}
