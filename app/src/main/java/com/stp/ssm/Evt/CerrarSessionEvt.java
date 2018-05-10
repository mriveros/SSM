package com.stp.ssm.Evt;

import static com.stp.ssm.Evt.CerrarSessionEvt.RESULTADO.findByCodigo;

public class CerrarSessionEvt {
    private RESULTADO resultado;


    public CerrarSessionEvt(int result) {
        resultado = findByCodigo(result);
    }


    public RESULTADO getResultado() {
        return resultado;
    }

    public static enum RESULTADO {
        OK(0), ERROR(1);

        private final int codigo;

        RESULTADO(int codigo) {
            this.codigo = codigo;
        }


        public int getCodigo() {
            return codigo;
        }


        public static RESULTADO findByCodigo(int codigo) {
            for (RESULTADO resultado : values()) {
                if (resultado.codigo == codigo) {
                    return resultado;
                }
            }
            return null;
        }
    }
}
