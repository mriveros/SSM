package com.stp.ssm.Model;

public class Reporte {

    private long idvisita;
    private String beneficiario;
    private String beneficiario_verificado;
    private String fechavisita;
    private int codmotivo;
    private String motivo;
    private Coordenadas coordenadas;
    private Estado estado;
    private Envio envio;
    private String documento;
    private Beneficiario.Estado benficiario_estado;
    private boolean hasformulario;
    private String comentario;
    private String id_key_rel;
    private int tipo_proyecto = 0;

    public Reporte(long idvisita, String beneficiario, String beneficiario_verificado,
                   String fechavisita, int codmotivo, String motivo, Coordenadas coordenadas,
                   Estado estado, Envio envio, String documento, Beneficiario.Estado benficiario_estado,
                   String comentario, String id_key_rel) {
        this.idvisita = idvisita;
        this.beneficiario = beneficiario;
        this.beneficiario_verificado = beneficiario_verificado;
        this.fechavisita = fechavisita;
        this.codmotivo = codmotivo;
        this.motivo = motivo;
        this.coordenadas = coordenadas;
        this.estado = estado;
        this.envio = envio;
        this.documento = documento;
        this.benficiario_estado = benficiario_estado;
        this.comentario = comentario;
        this.id_key_rel = id_key_rel;
    }

    public long getIdvisita() {
        return idvisita;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public String getFechavisita() {
        return fechavisita;
    }

    public String getMotivo() {
        return motivo;
    }

    public Coordenadas getCoordenadas() {
        return coordenadas;
    }

    public Estado getEstado() {
        return estado;
    }

    public int getCodmotivo() {
        return codmotivo;
    }

    public Envio getEnvio() {
        return envio;
    }

    public String getDocumento() {
        return documento;
    }

    public Beneficiario.Estado getBenficiario_estado() {
        return benficiario_estado;
    }

    public String getBeneficiario_verificado() {
        return beneficiario_verificado;
    }

    public boolean hasformulario() {
        return hasformulario;
    }

    public void setHasformulario(boolean hasformulario) {
        this.hasformulario = hasformulario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getId_key_rel() {
        return id_key_rel;
    }

    public int getTipo_proyecto() {
        return tipo_proyecto;
    }

    public void setTipo_proyecto(int tipo_proyecto) {
        this.tipo_proyecto = tipo_proyecto;
    }

    public static enum Estado {
        NO_FINALIZADO("NO_FINALIZADO", 0), FINALIZADO("FINALIZADO", 1);

        private final String text;
        private final int codigo;

        /**
         * Constructor
         *
         * @param text   Descripcion del estado
         * @param codigo Codigo del estado
         */
        Estado(String text, int codigo) {
            this.text = text;
            this.codigo = codigo;
        }

        /**
         * buscador segun codigo
         *
         * @param codigo Codigo a buscar
         * @return Enum estado segun codigo
         */
        public static Estado findByCodigo(int codigo) {
            for (Estado estado : values()) {
                if (estado.codigo == codigo) {
                    return estado;
                }
            }
            return null;
        }

        /**
         * @return Descripcion del Estado
         */
        @Override
        public String toString() {
            return text;
        }
    }

    public static enum Envio {
        PENDIENTE("PENDIENTE", 0), ENVIADO("ENVIADO", 1);

        private final String text;
        private final int codigo;

        /**
         * Constructor
         *
         * @param text   Descripcion del Estado de envio
         * @param codigo Codigo del estado de envio
         */
        Envio(String text, int codigo) {
            this.text = text;
            this.codigo = codigo;
        }

        /**
         * Buscador segun el codigo
         *
         * @param codigo a buscar
         * @return Enum estado de envion segun codigo
         */
        public static Envio findByCodigo(int codigo) {
            for (Envio envio : values()) {
                if (envio.codigo == codigo) {
                    return envio;
                }
            }
            return null;
        }

        /**
         * @return Descripcion del Enum Envio
         */
        @Override
        public String toString() {
            return text;
        }
    }
}
