package com.stp.ssm.Model;

import java.io.Serializable;

import static com.stp.ssm.Model.Visita.TipoVisita.VISITA_CORREGIR;

public class Visita implements Serializable {

    private long idvisita;
    private String horainicio;
    private String horafin;
    private Beneficiario beneficiario;
    private int codmotivo;
    private Coordenadas coordenadas;
    private String observacion;
    private TipoVisita tipo;
    private int proyecto;
    private boolean formulario = false;
    private long tiempo = 0;
    private String id_key;
    private int original = 1;

    public Visita(String horainicio, TipoVisita tipo) {
        this.horainicio = horainicio;
        this.tipo = tipo;
        if (tipo.equals(VISITA_CORREGIR)) {
            this.original = 0;
        }
    }

    public String getHorainicio() {
        return horainicio;
    }

    public String getHorafin() {
        return horafin;
    }

    public Beneficiario getBeneficiario() {
        return beneficiario;
    }

    public int getCodmotivo() {
        return codmotivo;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public String getObservacion() {
        return observacion;
    }

    public TipoVisita getTipo() {
        return tipo;
    }

    public void setHorafin(String horafin) {
        this.horafin = horafin;
    }

    public void setBeneficiario(Beneficiario beneficiario) {
        this.beneficiario = beneficiario;
    }

    public void setCodmotivo(int codmotivo) {
        this.codmotivo = codmotivo;
    }

    public void setCoordenadas(Coordenadas coordenadas) {
        this.coordenadas = coordenadas;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public void setHasFormulario() {
        formulario = true;
    }

    public boolean hasFormulario() {
        return formulario;
    }

    public long getIdvisita() {
        return idvisita;
    }

    public void setIdvisita(long idvisita) {
        this.idvisita = idvisita;
    }

    public int getProyecto() {
        return proyecto;
    }

    public void setProyecto(int proyecto) {
        this.proyecto = proyecto;
    }

    public long getTiempo() {
        return tiempo;
    }

    public int getOriginal() {
        return original;
    }

    public void setTiempo(long tiempo) {
        this.tiempo = tiempo;
    }

    public String getId_key() {
        return id_key;
    }

    public void setId_key(String id_key) {
        this.id_key = id_key;
    }

    public enum TipoVisita {
        VISITA_ASIGNADO(0),
        VISITA_NUEVO_BENEFICIARIO(1),
        VISITA_SIN_BENEFICIARIO(2),
        VISITA_CORREGIR(3);
        private final int codigo;

        /**
         * Constructor
         *
         * @param codigo Codigo del tipo de visita
         */
        TipoVisita(int codigo) {
            this.codigo = codigo;
        }

        /**
         * @return Codigo del tipo de visita
         */
        public int getCodigo() {
            return codigo;
        }
    }
}