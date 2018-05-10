package com.stp.ssm.Util;

import com.stp.ssm.Model.HoraServidor;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

public class FechaUtil {


    public static String getFechaActual() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }


    public static String getFechaDia() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }


    public static String getFechaCadena() {
        return new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(new Date());
    }

    public static String getFechaCadena2() {
        return new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
    }


    public static String milisegunToDate(long time) {
        Date date = new Date(time);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date);
    }


    public static String SegundoToHora(long time) {
        long milisegundos = time * 1000;
        long diffsegundos = milisegundos / 1000 % 60;
        long diffminutos = milisegundos / (60 * 1000) % 60;
        long diffHoras = milisegundos / (60 * 60 * 1000) % 24;

        DecimalFormat df = new DecimalFormat("00");
        return format("%s:%s:%s", df.format(diffHoras), df.format(diffminutos), df.format(diffsegundos));
    }

    public static boolean validFechaServidor(HoraServidor horaServidor) {
        Date date = new Date();
        if (parseInt(new SimpleDateFormat("yyyy").format(date)) != horaServidor.getAnho()) {
            return false;
        }
        if (parseInt(new SimpleDateFormat("MM").format(date)) != horaServidor.getMes()) {
            return false;
        }
        if (parseInt(new SimpleDateFormat("dd").format(date)) != horaServidor.getDia()) {
            return false;
        }
        if (parseInt(new SimpleDateFormat("HH").format(date)) != horaServidor.getHora()) {
            return false;
        }
        int mesDevice = parseInt(new SimpleDateFormat("mm").format(date));
        int diferencia = mesDevice - horaServidor.getMinuto();
        if (diferencia < 0) {
            diferencia = diferencia * (-1);
        }
        if (diferencia > 5) {
            return false;
        }
        return true;
    }
}
