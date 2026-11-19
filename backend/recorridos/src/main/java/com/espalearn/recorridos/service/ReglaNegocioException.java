package com.espalearn.recorridos.service;

/** Función: comunicar conflictos o ausencias de negocio sin acoplar el servicio a HTTP. */
public class ReglaNegocioException extends RuntimeException {
    // Clasificación: el controlador de errores traducirá ausencia a 404 y conflicto a 409.
    private final String codigo;
    private final boolean noEncontrado;

    // Construcción: sólo se incluyen mensajes públicos controlados por la aplicación.
    public ReglaNegocioException(String codigo, String mensaje, boolean noEncontrado) {
        super(mensaje);
        this.codigo = codigo;
        this.noEncontrado = noEncontrado;
    }
    public String getCodigo() { return codigo; }
    public boolean isNoEncontrado() { return noEncontrado; }
}
