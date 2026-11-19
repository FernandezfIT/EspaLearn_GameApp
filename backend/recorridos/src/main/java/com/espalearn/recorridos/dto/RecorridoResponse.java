package com.espalearn.recorridos.dto;

import com.espalearn.recorridos.model.Palabra;
import com.espalearn.recorridos.model.Recorrido;
import java.util.List;
import java.util.UUID;

/** Función: exponer el contrato F0 sin serializar entidades, relaciones JPA o datos privados. */
public record RecorridoResponse(UUID recorridoId, String versionCatalogo, Recorrido.Tipo tipo,
                                Recorrido.Estado estado, List<String> palabraIds,
                                List<String> etapasConfirmadas) {
    // Mapeo: ejecutar dentro de la transacción para leer las colecciones JPA con seguridad.
    public static RecorridoResponse desde(Recorrido recorrido) {
        return new RecorridoResponse(UUID.fromString(recorrido.getId()), recorrido.getVersionCatalogo(),
                recorrido.getTipo(), recorrido.getEstado(),
                recorrido.getPalabras().stream().map(Palabra::getPalabraId).toList(),
                recorrido.getEtapasConfirmadas());
    }
}
