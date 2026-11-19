package com.espalearn.recorridos.controller;

import com.espalearn.recorridos.dto.ErrorResponse;
import com.espalearn.recorridos.service.ReglaNegocioException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.LinkedHashMap;

/** Función: traducir fallos a respuestas públicas controladas, separadas de los detalles internos. */
@RestControllerAdvice
public class ApiExceptionHandler {
    // Negocio: diferenciar ausencia real de conflictos de versión, activo o solicitud.
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> negocio(ReglaNegocioException error) {
        return ResponseEntity.status(error.isNoEncontrado() ? 404 : 409)
                .body(ErrorResponse.de(error.getCodigo(), error.getMessage()));
    }

    // Validación: mantener errores por campo sin devolver el cuerpo original del usuario.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException error) {
        var campos = new LinkedHashMap<String, String>();
        error.getBindingResult().getFieldErrors().forEach(e -> campos.put(e.getField(), "Valor inválido o ausente."));
        return ResponseEntity.badRequest().body(new ErrorResponse("PETICION_INVALIDA", "Revisa los campos enviados.", campos));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> formato(Exception error) {
        return ResponseEntity.badRequest().body(ErrorResponse.de("FORMATO_INVALIDO", "Revisa el JSON y los identificadores UUID."));
    }

    // Persistencia: una restricción no equivale a éxito; una caída de BD no equivale a ausencia.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException error) {
        return ResponseEntity.status(409).body(ErrorResponse.de("CONFLICTO_PERSISTENCIA", "La operación entra en conflicto con el estado guardado."));
    }
    @ExceptionHandler({DataAccessException.class, TransactionException.class})
    public ResponseEntity<ErrorResponse> dependencia(Exception error) {
        return ResponseEntity.status(503).body(ErrorResponse.de("PERSISTENCIA_NO_DISPONIBLE", "No se pudo confirmar la operación. Consulta su estado antes de reintentar."));
    }
}
