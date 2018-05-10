package com.stp.ssm.Evt;

public class RemoveMiembroFamEvt {
    private int seccioPosition;
    private int lstPositon;

    public RemoveMiembroFamEvt(int seccioPosition, int lstPositon) {
        this.seccioPosition = seccioPosition;
        this.lstPositon = lstPositon;
    }


    public int getSeccioPosition() {
        return seccioPosition;
    }

    public int getLstPositon() {
        return lstPositon;
    }
}
