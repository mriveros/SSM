package com.stp.ssm.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Capturas implements Parcelable {

    private String path;
    private int origen;
    private String hash;
    private int estado;
    private int sync;

    public Capturas(String path, int origen) {
        this.path = path;
        this.origen = origen;
    }

    public Capturas(String path, int origen, String hash, int estado, int sync) {
        this.path = path;
        this.origen = origen;
        this.hash = hash;
        this.estado = estado;
        this.sync = sync;
    }

    protected Capturas(Parcel in) {
        path = in.readString();
        origen = in.readInt();
    }

    public static final Creator<Capturas> CREATOR = new Creator<Capturas>() {
        @Override
        public Capturas createFromParcel(Parcel in) {
            return new Capturas(in);
        }

        @Override
        public Capturas[] newArray(int size) {
            return new Capturas[size];
        }
    };

    public String getPath() {
        return path;
    }

    public int getOrigen() {
        return origen;
    }

    public String getHash() {
        return hash;
    }

    public int getEstado() {
        return estado;
    }

    public int getSync() {
        return sync;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(origen);
    }

    public enum TIPO_ORIGEN {
        CAMARA(1),
        GALERIA(2);

        private final int codigo;

        TIPO_ORIGEN(int codigo) {
            this.codigo = codigo;
        }

        public int getCodigo() {
            return codigo;
        }
    }

    public enum ESTADOS {
        NO_ENVIADO(0),
        ENVIADO(1),
        ERROR_IMAGEN(2);

        private final int codigo;

        ESTADOS(int codigo) {
            this.codigo = codigo;
        }

        public int getCodigo() {
            return codigo;
        }
    }
}
