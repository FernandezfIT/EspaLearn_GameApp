package com.espalearn.recorridos.dto;

import com.espalearn.recorridos.model.Recorrido;
import jakarta.validation.constraints.*;
import java.util.UUID;

/** Función: definir y validar el JSON de inicio, sin aceptar un lote elegido por Android. */
public record CrearRecorridoRequest(
        // Reintento: el cliente conserva este UUID hasta conocer el resultado de la operación.
        @NotNull UUID solicitudId,
        @NotNull Recorrido.Tipo tipo,
        // Compatibilidad: no sustituir una versión desconocida por otra silenciosamente.
        @NotBlank @Pattern(regexp = "[a-zA-Z0-9._-]{1,40}") String versionCatalogo
) { }
