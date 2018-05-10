package com.stp.ssm.Model;

import java.util.ArrayList;
import java.util.Map;

public class SendData {
    private String id;
    private Map<String, String> parametros;
    private String path;
    private TIPO tipo;

    public SendData(String id, Map<String, String> parametros, TIPO tipo) {
        this.id = id;
        this.parametros = parametros;
        this.tipo = tipo;
    }

    public SendData(String id, Map<String, String> parametros, TIPO tipo, String path) {
        this.id = id;
        this.parametros = parametros;
        this.tipo = tipo;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public Map<String, String> getParametros() {
        return parametros;
    }

    public TIPO getTipo() {
        return tipo;
    }

    public static enum TIPO {
        VISITA(0),
        CAPTURAS(1),
        ADJUNTOS(2),
        UBICACION(3),
        TELEFONIA(4),
        RECORRIDO(5),
        EVENTO(6),
        EVENTO_CAPTURAS(7),
        EVENTO_DEV(8),
        INDENTIFICACIONES(9),
        VERIFIC_CAPTURAS(10),
        CORRECCION(11),
        CAPTURA_INVALIDA(12),
        EVENTO_CAPTURA_INVALIDA(13);
        private final int codigo;

        TIPO(int codigo) {
            this.codigo = codigo;
        }


        public int getCodigo() {
            return codigo;
        }
    }

    public String getPath() {
        return path;
    }
}
