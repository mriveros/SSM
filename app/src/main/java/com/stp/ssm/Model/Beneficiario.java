package com.stp.ssm.Model;
//Created by desarrollo on 11/03/16.

import java.io.Serializable;

public class Beneficiario implements Serializable {
    private long idbeneficiario;
    private String nombre;
    private String apellido;
    private String documento;
    private String usuario;
    private String proyecto;
    private Coordenadas coordenadas;
    private TipoBeneficiario tipo;
    private int distrito;
    private int departamento;
    private int localidad;
    private Estado estado;
    private boolean jefe = false;
    private int idregistro;

    public Beneficiario(String nombre, String apellido, String documento, String usuario, String proyecto,
                        TipoBeneficiario tipo, int distrito, int departamento, Estado estado, boolean jefe) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.usuario = usuario;
        this.proyecto = proyecto;
        this.tipo = tipo;
        this.distrito = distrito;
        this.departamento = departamento;
        this.estado = estado;
        this.jefe = jefe;
    }

    public Beneficiario() {
    }


    public String getNombre() {
        return nombre.replace("#", " ");
    }

    public String getRawNombre() {
        return nombre;
    }

    public String getPrimerNombre() {
        if (nombre.indexOf("#") > 0) {
            return nombre.substring(0, nombre.indexOf("#"));
        } else {
            return nombre;
        }
    }

    public String getSegundoNombre() {
        if (nombre.indexOf("#") > 0) {
            return nombre.substring(nombre.indexOf("#") + 1);
        } else {
            return "";
        }
    }


    public String getApellido() {
        return apellido.replace("#", " ");
    }


    public String getRawApellido() {
        return apellido;
    }


    public String getPrimerApellido() {
        if (apellido.indexOf("#") > 0) {
            return apellido.substring(0, apellido.indexOf("#"));
        } else {
            return apellido;
        }
    }


    public String getSegundoApellido() {
        if (apellido.indexOf("#") > 0) {
            return apellido.substring(apellido.indexOf("#") + 1);
        } else {
            return "";
        }
    }


    public String getDocumento() {
        return documento;
    }


    public String getUsuario() {
        return usuario;
    }


    public Coordenadas getCoordenadas() {
        return coordenadas;
    }


    public TipoBeneficiario getTipo() {
        return tipo;
    }


    public Estado getEstado() {
        return estado;
    }


    public void setCoordenadas(Coordenadas coordenadas) {
        this.coordenadas = coordenadas;
    }


    public String getProyecto() {
        return proyecto;
    }


    public int getDistrito() {
        return distrito;
    }


    public int getDepartamento() {
        return departamento;
    }

    public boolean isJefe() {
        return jefe;
    }

    public int getLocalidad() {
        return localidad;
    }

    public void setLocalidad(int localidad) {
        this.localidad = localidad;
    }

    public int getIdregistro() {
        return idregistro;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public void setTipo(TipoBeneficiario tipo) {
        this.tipo = tipo;
    }

    public void setDistrito(int distrito) {
        this.distrito = distrito;
    }

    public void setDepartamento(int departamento) {
        this.departamento = departamento;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public void setJefe(boolean jefe) {
        this.jefe = jefe;
    }

    public void setIdregistro(int idregistro) {
        this.idregistro = idregistro;
    }

    public long getIdbeneficiario() {
        return idbeneficiario;
    }

    public void setIdbeneficiario(long idbeneficiario) {
        this.idbeneficiario = idbeneficiario;
    }

    @Override
    public String toString() {
        return "Destinatario :" + nombre + " " + apellido + "\n" + documento;
    }


    public static enum TipoBeneficiario {
        PERSONA(0, 1, "persona"),
        ENTIDAD(1, 2, "entidad"),
        COMUNIDAD(2, 3, "comunidad"),
        MUNICIPALIDAD(3, 4, "distrito"),
        ENTIDAD_NO_INSTITUCION(4, 5, "entidad_no_institucion");

        private final int codigo;
        private final int codigoDB;
        private final String descripcion;


        TipoBeneficiario(int codigo, int codigoDB, String descripcion) {
            this.codigo = codigo;
            this.codigoDB = codigoDB;
            this.descripcion = descripcion;
        }


        public int getCodigo() {
            return codigo;
        }


        public String getDescripcion() {
            return descripcion;
        }


        public static TipoBeneficiario findByCodigo(int codigo) {
            for (TipoBeneficiario tipo : values()) {
                if (tipo.codigo == codigo) {
                    return tipo;
                }
            }
            return null;
        }


        public static TipoBeneficiario findByDescripcion(String descripcion) {
            for (TipoBeneficiario tipo : values()) {
                if (tipo.getDescripcion().equals(descripcion)) {
                    return tipo;
                }
            }
            return null;
        }

        public static TipoBeneficiario findByCodigoBD(int codigoDB) {
            for (TipoBeneficiario tipo : values()) {
                if (tipo.codigoDB == codigoDB) {
                    return tipo;
                }
            }
            return null;
        }
    }


    public static enum Estado {
        ASIGNADO(0, "ASIGNADO"), NUEVO_NO_VALIDADO(1, "NO VALIDADO"), NUEVO_VALIDADO(2, "VALIDADO");

        private final int codigo;
        private final String descripcion;


        Estado(int codigo, String descripcion) {
            this.codigo = codigo;
            this.descripcion = descripcion;
        }


        public int getCodigo() {
            return codigo;
        }


        public String getDescripcion() {
            return descripcion;
        }


        public static Estado findByCodigo(int codigo) {
            for (Estado estado : values()) {
                if (estado.codigo == codigo) {
                    return estado;
                }
            }
            return null;
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
