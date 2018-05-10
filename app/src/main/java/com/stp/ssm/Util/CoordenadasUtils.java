package com.stp.ssm.Util;

import android.location.Location;


public class CoordenadasUtils {

    public static String decimalToDegree(double coordenada,TipoCoordenada tipo){
        StringBuilder builder = new StringBuilder();
        if(tipo.equals(TipoCoordenada.LATITUD)){
            if (coordenada < 0) {
                builder.append("S ");
            } else {
                builder.append("N ");
            }
        }else{
            if (coordenada < 0) {
                builder.append("W ");
            }else{
                builder.append("E ");
            }
        }

        String degrees = Location.convert(Math.abs(coordenada), Location.FORMAT_SECONDS);
        String[] longitudeSplit = degrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        return builder.toString();
    }

    public enum TipoCoordenada {
        LONGITUD,LATITUD;
    }
}
