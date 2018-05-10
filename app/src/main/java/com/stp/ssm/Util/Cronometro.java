package com.stp.ssm.Util;

import com.stp.ssm.Interfaces.OnTimeCronometroListener;

import java.util.Timer;
import java.util.TimerTask;

public class Cronometro {

    private Timer timer = null;
    private long segundos = 0;
    private OnTimeCronometroListener onTimeCronometroListener;


    public Cronometro() {
    }


    public void inicializar(long segundos) {
        this.segundos = segundos;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Cronometro.this.segundos++;
                onTimeCronometroListener.OnTimeCronometro(Cronometro.this.segundos);
            }
        }, 0, 1000);
    }

    public void setOnTimeCronometroListener(OnTimeCronometroListener onTimeCronometroListener) {
        this.onTimeCronometroListener = onTimeCronometroListener;
    }

    public void finalizar() {
        timer.cancel();
        segundos = 0;
    }
}
