package com.stp.ssm.Model;
//Created by desarrollo on 25/08/16.

import java.io.Serializable;
import java.util.ArrayList;

import static com.stp.ssm.Model.Pregunta.TIPO;
import static com.stp.ssm.Model.Pregunta.TIPO.CUADRO_TEXTO;
import static com.stp.ssm.Model.Pregunta.TIPO.CUADRO_TEXTO_EMAIL;
import static com.stp.ssm.Model.Pregunta.TIPO.CUADRO_TEXTO_NUMERICO;
import static com.stp.ssm.Model.Pregunta.TIPO.LIST_SELECT;
import static java.lang.Integer.parseInt;

public class SubFormulario implements Serializable {
    private String idseccion;
    private ArrayList<Pregunta> preguntas;
    private int nro_subformulario;

    public SubFormulario(String idseccion, ArrayList<Pregunta> preguntas) {
        this.idseccion = idseccion;
        this.preguntas = preguntas;
    }

    public SubFormulario() {
    }

    public String getIdseccion() {
        return idseccion;
    }

    public ArrayList<Pregunta> getPreguntas() {
        return preguntas;
    }

    public int getNro_subformulario() {
        return nro_subformulario;
    }

    public void setIdseccion(String idseccion) {
        this.idseccion = idseccion;
    }

    public void setPreguntas(ArrayList<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }

    public void setNro_subformulario(int nro_subformulario) {
        this.nro_subformulario = nro_subformulario;
    }

    @Override
    public String toString() {
        String result = "";
        for (Pregunta pregunta : preguntas) {
            if (pregunta.getListable() == 1) {
                if (pregunta.getTipo().equals(CUADRO_TEXTO) || pregunta.getTipo().equals(CUADRO_TEXTO_EMAIL)) {
                    result = result + pregunta.getTxtrespuesta() + "\n";
                    //return ;
                } else if (pregunta.getTipo().equals(LIST_SELECT)) {
                    int codresp = parseInt(pregunta.getTxtrespuesta());
                    //int codresp = Integer.parseInt(pregunta.getSelecresp().get(0));
                    String respuesta = "";
                    for (PosiblesRespuestas posiblesRespuestas : pregunta.getRespuestas()) {
                        if (posiblesRespuestas.getCodigo() == codresp) {
                            respuesta = posiblesRespuestas.getTexto();
                            break;
                        }
                    }
                    result = result + respuesta + "\n";
                } else if (pregunta.getTipo().equals(CUADRO_TEXTO_NUMERICO)) {
                    result = result + pregunta.getPregunta() + ":" + pregunta.getTxtrespuesta() + "\n";
                }
            }
        }
        return result;
    }
}
