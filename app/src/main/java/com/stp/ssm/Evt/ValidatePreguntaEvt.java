package com.stp.ssm.Evt;

public class ValidatePreguntaEvt {
    private boolean visible;
    private long idpregunta;
    private Integer idView;

    public ValidatePreguntaEvt(boolean visible, long idpregunta, Integer idView) {
        this.visible = visible;
        this.idpregunta = idpregunta;
        this.idView = idView;
    }

    public boolean isVisible() {
        return visible;
    }

    public long getIdpregunta() {
        return idpregunta;
    }

    @Override
    public String toString() {
        return "ValidatePreguntaEvt{" +
                "visible=" + visible +
                ", idpregunta=" + idpregunta +
                '}';
    }
}
