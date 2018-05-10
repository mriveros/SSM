package com.stp.ssm.Model;

import java.io.Serializable;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class Secciones implements Serializable {

    private int codSeccion;
    private String descripSeccion;
    private ArrayList<Pregunta> preguntas;
    private ArrayList<SubFormulario> subFormularios;
    private ArrayList<Beneficiario> beneficiarios;
    private int hasfamily = 0;
    private boolean hasResponse = false;
    private int tipo;
    private int totalizable;
    private int condicionable;
    private ArrayList<TotalResult> totales;
    private ArrayList<SeccionCondicion> condicionsSiguiente;
    private boolean visible = true;
    private String multimedia;

    public Secciones(int codSeccion, String descripSeccion, ArrayList<Pregunta> preguntas, int tipo, int totalizable, int condicionable) {
        this.subFormularios = new ArrayList<>();
        this.codSeccion = codSeccion;
        this.descripSeccion = descripSeccion;
        this.preguntas = preguntas;
        this.tipo = tipo;
        this.totalizable = totalizable;
        this.condicionable = condicionable;
    }

    public Secciones(int codSeccion, String descripSeccion, ArrayList<Pregunta> preguntas, int tipo, int totalizable, int condicionable, ArrayList<SeccionCondicion> condicionsSiguiente) {
        this.subFormularios = new ArrayList<>();
        this.codSeccion = codSeccion;
        this.descripSeccion = descripSeccion;
        this.preguntas = preguntas;
        this.tipo = tipo;
        this.totalizable = totalizable;
        this.condicionable = condicionable;
        this.condicionsSiguiente = condicionsSiguiente;
    }

    public int getCodSeccion() {
        return codSeccion;
    }

    public String getDescripSeccion() {
        return descripSeccion;
    }

    public ArrayList<Pregunta> getPreguntas() {
        return preguntas;
    }

    public boolean isHasResponse() {
        return hasResponse;
    }

    public void setHasResponse(boolean hasResponse) {
        this.hasResponse = hasResponse;
    }

    public int getTipo() {
        return tipo;
    }

    public String getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(String multimedia) {
        this.multimedia = multimedia;
    }

    public ArrayList<SubFormulario> getSubFormularios() {
        return subFormularios;
    }

    public boolean addSubFormularios(SubFormulario subFormulario) {
        int cantidad;
        if (totalizable == 1) {
            int t = 0;
            for (TotalResult totalResult : totales) {
                for (SubFormulario subFormulario1 : subFormularios) {
                    cantidad = parseInt(subFormulario1.getPreguntas().get(totalResult.getPosicion()).getTxtrespuesta());
                    t = t + cantidad;
                }
                cantidad = parseInt(subFormulario.getPreguntas().get(totalResult.getPosicion()).getTxtrespuesta());
                t = t + cantidad;
                if (t > totalResult.getTotal()) {
                    return false;
                }
            }
        }
        subFormularios.add(subFormulario);
        return true;
    }

    public void setSubFormularios(ArrayList<SubFormulario> subFormularios) {
        this.subFormularios = subFormularios;
    }

    public ArrayList<Beneficiario> getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(ArrayList<Beneficiario> beneficiarios) {
        this.beneficiarios = beneficiarios;
    }

    public void addBeneficiario(Beneficiario beneficiario) {
        if (beneficiarios == null) {
            beneficiarios = new ArrayList<Beneficiario>();
        }
        beneficiarios.add(beneficiario);
    }

    public void removeBeneficiario(int posicion) {
        if (beneficiarios != null) {
            beneficiarios.remove(posicion);
        }
    }

    public int getTotalizable() {
        return totalizable;
    }

    public int getCondicionable() {
        return condicionable;
    }

    public void setTotales(ArrayList<TotalResult> totales) {
        this.totales = totales;
    }

    public ArrayList<TotalResult> getTotales() {
        return totales;
    }

    public int getHasfamily() {
        return hasfamily;
    }

    public void setHasfamily(int hasfamily) {
        this.hasfamily = hasfamily;
    }

    public ArrayList<SeccionCondicion> getCondicionsSiguiente() {
        return condicionsSiguiente;
    }

    public void setCondicionsSiguiente(ArrayList<SeccionCondicion> condicionsSiguiente) {
        this.condicionsSiguiente = condicionsSiguiente;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String toString() {
        return descripSeccion;
    }
}
