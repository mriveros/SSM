package com.stp.ssm.Evt;

import com.stp.ssm.Model.Beneficiario;

public class AddMiembroFamiliaEvt {
    private int posicion;
    private Beneficiario beneficiario;


    public AddMiembroFamiliaEvt(int posicion, Beneficiario beneficiario) {
        this.posicion = posicion;
        this.beneficiario = beneficiario;
    }


    public int getPosicion() {
        return posicion;
    }


    public Beneficiario getBeneficiario() {
        return beneficiario;
    }
}
