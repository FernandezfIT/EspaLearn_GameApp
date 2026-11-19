package com.espalearn.recorridos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;

/** Función: recibir resultados de una etapa; el cliente no decide si una opción era correcta. */
public record CierreEtapaRequest(
        // Identidad estable: reintentar no crea un segundo cierre.
        @NotNull UUID solicitudId,
        @NotBlank @Pattern(regexp = "[a-zA-Z0-9._-]{1,40}") String versionCatalogo,
        @NotNull @Pattern(regexp = "descubrir|con_apoyo|menos_apoyo") String etapa,
        @NotNull @Size(min = 3, max = 3) List<@NotNull @Valid Resultado> resultados) {
    // Resultado: opción nula sólo en Descubrir, ayuda explícita sólo en Menos apoyo.
    public record Resultado(@NotBlank @Size(max = 80) String palabraId,
                            @Size(max = 80) String opcionElegidaId, @NotNull Boolean ayudaUsada) { }
}
