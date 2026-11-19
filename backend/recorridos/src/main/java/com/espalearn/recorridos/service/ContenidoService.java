package com.espalearn.recorridos.service;

import com.espalearn.recorridos.dto.CatalogoResponse;
import com.espalearn.recorridos.model.Recorrido;
import com.espalearn.recorridos.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

/** Función: entregar catálogo versionado y avance derivado exclusivamente de cierres confirmados. */
@Service
@Transactional(readOnly = true)
public class ContenidoService {
    // Dependencias: el perfil y el marco heredado permanecen en Android, no en estas consultas.
    private final PalabraRepository palabras;
    private final RecorridoRepository recorridos;
    public ContenidoService(PalabraRepository palabras, RecorridoRepository recorridos) {
        this.palabras = palabras;
        this.recorridos = recorridos;
    }

    // Catálogo: no devolver cartas parcialmente configuradas como si fueran utilizables.
    public CatalogoResponse catalogo(String version) {
        var entradas = palabras.findByVersionCatalogoOrderByPalabraId(version);
        if (entradas.isEmpty()) throw conflicto("CATALOGO_INCOMPATIBLE", "El catálogo no está disponible.");
        if (entradas.stream().anyMatch(p -> !p.contenidoCompleto())) {
            throw conflicto("CATALOGO_INCOMPLETO", "El contenido del catálogo aún no está preparado.");
        }
        return new CatalogoResponse(version, entradas.stream().map(CatalogoResponse::carta).toList());
    }

    // Progreso: tres etapas no triplican las palabras; ausencia de historial es cobertura cero.
    public record ProgresoResponse(int completadasUnicas, int totalPalabras, boolean primerRecorridoCompletado) { }
    public ProgresoResponse progreso(UUID aprendiz, String version) {
        int total = palabras.countByVersionCatalogo(version);
        if (total == 0) throw conflicto("CATALOGO_INCOMPATIBLE", "El catálogo no está disponible.");
        return new ProgresoResponse(recorridos.completadasUnicas(aprendiz.toString()), total,
                recorridos.existsByAprendizIdAndEstado(aprendiz.toString(), Recorrido.Estado.COMPLETADO));
    }
    private ReglaNegocioException conflicto(String codigo, String mensaje) {
        return new ReglaNegocioException(codigo, mensaje, false);
    }
}
