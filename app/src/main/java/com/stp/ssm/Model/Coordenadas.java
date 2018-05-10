package com.stp.ssm.Model;

import android.location.Location;
import java.io.Serializable;
import static android.location.Location.FORMAT_SECONDS;
import static android.location.Location.convert;
import static java.lang.Double.parseDouble;
import static java.lang.Math.abs;

public class Coordenadas implements Serializable {

    private String longitud;
    private String latitud;
    private float precision;
    private double altitud;
    private String proveedor;
    private String hora;
    private String descripcion;


    public Coordenadas(String longitud, String latitud, float precision) {
        this.longitud = longitud;
        this.latitud = latitud;
        this.precision = precision;
    }


    public Coordenadas(String longitud, String latitud, float precision, double altitud, String proveedor, String hora) {
        this.longitud = longitud;
        this.latitud = latitud;
        this.precision = precision;
        this.altitud = altitud;
        this.proveedor = proveedor;
        this.hora = hora;
    }

    public Coordenadas(String longitud, String latitud, float precision, double altitud, String proveedor, String hora, String descripcion) {
        this.longitud = longitud;
        this.latitud = latitud;
        this.precision = precision;
        this.altitud = altitud;
        this.proveedor = proveedor;
        this.hora = hora;
        this.descripcion = descripcion;
    }


    public String getLongitud() {
        return longitud;
    }


    public String getLatitud() {
        return latitud;
    }


    public float getPrecision() {
        return precision;
    }

    public double getAltitud() {
        return altitud;
    }


    public String getProveedor() {
        return proveedor;
    }


    public String getHora() {
        return hora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String convertLatitud() {
        double latitude = parseDouble(latitud);
        StringBuilder builder = new StringBuilder();

        if (latitude < 0) {
            builder.append("S ");
        } else {
            builder.append("N ");
        }

        String latitudeDegrees = convert(abs(latitude), FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("°");
        builder.append(latitudeSplit[1]);
        builder.append("'");
        builder.append(latitudeSplit[2]);
        builder.append("\"");

        return builder.toString();
    }

    public String convertLongitud() {
        double longitude = parseDouble(longitud);
        StringBuilder builder = new StringBuilder();

        if (longitude < 0) {
            builder.append("W ");
        } else {
            builder.append("E ");
        }

        String longitudeDegrees = convert(abs(longitude), FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("°");
        builder.append(longitudeSplit[1]);
        builder.append("'");
        builder.append(longitudeSplit[2]);
        builder.append("\"");

        return builder.toString();
    }


    @Override
    public String toString() {
        return "Coordenadas{" +
                "longitud='" + longitud + '\'' +
                ", latitud='" + latitud + '\'' +
                ", precision=" + precision +
                ", altitud=" + altitud +
                ", proveedor='" + proveedor + '\'' +
                ", hora='" + hora + '\'' +
                '}';
    }
}
