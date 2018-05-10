package com.stp.ssm.Evt;

public class ValidIdentificacionesEvt {
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

    public ValidIdentificacionesEvt(String primerNombre, String segundoNombre, String primerApellido, String segundoApellido) {
        this.primerNombre = primerNombre;
        this.segundoNombre = segundoNombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
    }


    public String getPrimerNombre() {
        return primerNombre;
    }


    public String getSegundoNombre() {
        return segundoNombre;
    }


    public String getPrimerApellido() {
        return primerApellido;
    }


    public String getSegundoApellido() {
        return segundoApellido;
    }
}
