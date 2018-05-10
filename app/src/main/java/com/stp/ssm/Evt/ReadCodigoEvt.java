package com.stp.ssm.Evt;

public class ReadCodigoEvt {
    private String codigo;

    public ReadCodigoEvt(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
