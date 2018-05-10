package com.stp.ssm.Evt;

public class ValidarSeccion {
    private boolean visible;
    private long idseccionCondicionante;
    private long idseccionSiguiente;

    public ValidarSeccion(boolean visible, long idseccionCondicionante, long idseccionSiguiente) {
        this.visible = !visible;
        this.idseccionCondicionante = idseccionCondicionante;
        this.idseccionSiguiente = idseccionSiguiente;
    }

    public boolean isVisible() {
        return visible;
    }

    public long getIdseccionCondicionante() {
        return idseccionCondicionante;
    }

    public long getIdseccionSiguiente() {
        return idseccionSiguiente;
    }
}
