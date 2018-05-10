package com.stp.ssm.Model;
//Created by desarrollo on 28/03/16.

import java.io.Serializable;
import java.util.ArrayList;

import static com.stp.ssm.Model.Pregunta.REQUERIDO.OBLIGATORIO;
import static com.stp.ssm.Model.Pregunta.REQUERIDO.findByCodigo;
import static com.stp.ssm.Model.Pregunta.TIPO.CHECKBOX;
import static com.stp.ssm.Model.Pregunta.TIPO.LISTA_DINAMICA;

public class Pregunta implements Serializable {

    private int idpregunta;
    private String pregunta;
    private TIPO tipo;
    private ArrayList<PosiblesRespuestas> respuestas;
    private String txtrespuesta = "";
    private ArrayList<String> selecresp;
    private REQUERIDO requerido;
    private ArrayList<PreguntaCondicion> preguntaCondicions;
    private boolean visible = true;
    private int totalizable;
    private int listable;
    private boolean hasfoco = false;

    public Pregunta(int idpregunta, String pregunta, TIPO tipo) {
        this.idpregunta = idpregunta;
        this.pregunta = pregunta;
        this.tipo = tipo;
    }

    public Pregunta(int idpregunta, String pregunta, TIPO tipo, int requerido, ArrayList<PreguntaCondicion> preguntaCondicions, int totalizable, int listable) {
        this.idpregunta = idpregunta;
        this.pregunta = pregunta;
        this.tipo = tipo;
        this.requerido = findByCodigo(requerido);
        this.selecresp = new ArrayList<>();
        this.preguntaCondicions = preguntaCondicions;
        this.totalizable = totalizable;
        this.listable = listable;
    }

    public void setRespuestas(ArrayList<PosiblesRespuestas> respuestas) {
        this.respuestas = respuestas;
    }

    public void responder(String respuesta) {
        if (tipo.equals(CHECKBOX) || tipo.equals(LISTA_DINAMICA)) {
            if (selecresp == null) {
                selecresp = new ArrayList<>();
            }
            selecresp.add(respuesta);
        } else {
            txtrespuesta = respuesta;
        }
    }

    public void cargarRespuesta(ArrayList<String> resp) {
        if (!resp.isEmpty()) {
            if (tipo.equals(CHECKBOX)) {
                selecresp = resp;
            } else {
                txtrespuesta = resp.get(0);
            }
        }
    }

    public int getTotalizable() {
        return totalizable;
    }

    public int getListable() {
        return listable;
    }

    public void removeRespuesta(String respuesta) {
        selecresp.remove(respuesta);
    }

    public int getIdpregunta() {
        return idpregunta;
    }

    public String getPregunta() {
        return pregunta;
    }

    public TIPO getTipo() {
        return tipo;
    }

    public ArrayList<PosiblesRespuestas> getRespuestas() {
        return respuestas;
    }

    public String getTxtrespuesta() {
        return txtrespuesta;
    }

    public ArrayList<String> getSelecresp() {
        return selecresp;
    }

    public boolean isRequerido() {
        return requerido.equals(OBLIGATORIO);
    }

    public void setPreguntaCondicions(ArrayList<PreguntaCondicion> preguntaCondicions) {
        this.preguntaCondicions = preguntaCondicions;
    }

    public ArrayList<PreguntaCondicion> getPreguntaCondicions() {
        return preguntaCondicions;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public REQUERIDO getRequerido() {
        return requerido;
    }

    public boolean isHasfoco() {
        return hasfoco;
    }

    public void setHasfoco(boolean hasfoco) {
        this.hasfoco = hasfoco;
    }

    public static enum TIPO {
        CHECKBOX(1),
        LIST_SELECT(2),
        CUADRO_TEXTO(3),
        CUADRO_TEXTO_NUMERICO(4),
        CUADRO_TEXTO_EMAIL(5),
        CUADRO_TEXTO_FECHA(6),
        LISTA_HOGAR(7),
        FECHA_CALCULO(8),
        TELEFONO(9),
        CELULAR(10),
        HORA(11),
        LISTA_DINAMICA(12),
        LECTOR_QR(13),
        IMAGEN(14),
        GEOPOSICION(15),
        LISTA_PUNTOS(16);

        private final int codigo;

        /**
         * Constructor
         *
         * @param codigo
         */
        TIPO(int codigo) {
            this.codigo = codigo;
        }

        /**
         * @return Codigo del tipo de pregunta
         */
        public int getCodigo() {
            return codigo;
        }

        /**
         * Buscador segun el codigo
         *
         * @param codigo codigo a buscar
         * @return TIPO segun el codigo
         */
        public static TIPO findByCodigo(int codigo) {
            for (TIPO tipo : values()) {
                if (tipo.codigo == codigo) {
                    return tipo;
                }
            }
            return null;
        }
    }

    public static enum REQUERIDO {
        NO_OBLIGATORIO(0), OBLIGATORIO(1);

        private final int codigo;

        /**
         * Codigo
         *
         * @param codigo Codigo del Enum Requerido
         */
        REQUERIDO(int codigo) {
            this.codigo = codigo;
        }

        /**
         * @return codigo del Enum REQUERIDO
         */
        public int getCodigo() {
            return codigo;
        }

        /**
         * Buscador del enum Requerido segun el codigo
         *
         * @param codigo Codigo a buscar
         * @return Enum Reqierido segun codigo
         */
        public static REQUERIDO findByCodigo(int codigo) {
            for (REQUERIDO requerido : values()) {
                if (requerido.codigo == codigo) {
                    return requerido;
                }
            }
            return null;
        }
    }
}
