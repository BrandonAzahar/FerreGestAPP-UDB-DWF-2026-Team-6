package com.ferregest.dto;

import java.io.Serializable;

/**
 * DTO para estandarizar las respuestas JSON de la API REST (éxito, validaciones, errores).
 * Permite que los clientes externos (JavaScript, React, Vue, etc.) reciban una estructura
 * predecible y uniforme en todas las respuestas HTTP.
 */
public class MensajeRespuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean exito;
    private String mensaje;
    private int codigo;

    public MensajeRespuesta() {
    }

    public MensajeRespuesta(boolean exito, String mensaje, int codigo) {
        this.exito = exito;
        this.mensaje = mensaje;
        this.codigo = codigo;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
}

