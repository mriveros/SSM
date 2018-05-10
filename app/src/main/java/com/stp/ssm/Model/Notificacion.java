package com.stp.ssm.Model;
//Created by desarrollo on 05/09/16.

public class Notificacion {
    private String topic;
    private String mensajeid;
    private String mensaje;
    private String fecha;
    private boolean leido;

    public Notificacion(String topic, String mensajeid, String mensaje, boolean leido) {
        this.topic = topic;
        this.mensajeid = mensajeid;
        this.mensaje = mensaje;
        this.leido = leido;
    }

    public Notificacion(String topic, String mensajeid, String mensaje, String fecha, boolean leido) {
        this.topic = topic;
        this.mensajeid = mensajeid;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.leido = leido;
    }

    public String getTopic() {
        return topic;
    }

    public String getMensajeid() {
        return mensajeid;
    }

    public String getMensaje() {
        return mensaje;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido() {
        this.leido = true;
    }

    public String getFecha() {
        return fecha;
    }
}
