package com.stp.ssm.Model;

public class TotalRelevado {
    private final int total;
    private final int enviado;

    public TotalRelevado(int total, int enviado) {
        this.total = total;
        this.enviado = enviado;
    }

    public int getTotal() {
        return total;
    }

    public int getEnviado() {
        return enviado;
    }
}
