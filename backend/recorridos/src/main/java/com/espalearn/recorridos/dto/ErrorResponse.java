package com.espalearn.recorridos.dto;

import java.util.Map;

/** Función: devolver errores estables al cliente sin filtrar SQL, credenciales ni trazas. */
public record ErrorResponse(String codigo, String mensaje, Map<String, String> campos) {
    // Error global: los errores de validación por campo utilizan el constructor completo.
    public static ErrorResponse de(String codigo, String mensaje) {
        return new ErrorResponse(codigo, mensaje, Map.of());
    }
}
