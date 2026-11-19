package com.espalearn.recorridos.controller;

import com.espalearn.recorridos.dto.*;
import com.espalearn.recorridos.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

/** Función: exponer las piezas HTTP mínimas que Android necesita para completar la vertical F2. */
@RestController
@RequestMapping("/api/v1")
public class VerticalController {
    // Inyección: catálogo, avance y reglas de cierre no se implementan dentro del controlador.
    private final ContenidoService contenido;
    private final EtapaService etapas;
    public VerticalController(ContenidoService contenido, EtapaService etapas) {
        this.contenido = contenido;
        this.etapas = etapas;
    }

    // Lecturas: explícitamente sin generación IA ni cambios de progreso por consultar.
    @GetMapping("/catalogo")
    public ResponseEntity<CatalogoResponse> catalogo(@RequestParam String version) {
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(contenido.catalogo(version));
    }
    @GetMapping("/aprendices/{aprendizId}/progreso")
    public ResponseEntity<ContenidoService.ProgresoResponse> progreso(@PathVariable UUID aprendizId,
            @RequestParam String version) {
        return ResponseEntity.ok().header("Cache-Control", "no-store").body(contenido.progreso(aprendizId, version));
    }

    // Escritura: el DTO conserva resultados e identidad del cierre para reintentos seguros.
    @PutMapping("/aprendices/{aprendizId}/recorridos/{recorridoId}/etapas/{etapaId}")
    public ResponseEntity<RecorridoResponse> cerrar(@PathVariable UUID aprendizId, @PathVariable UUID recorridoId,
            @PathVariable String etapaId, @Valid @RequestBody CierreEtapaRequest entrada) {
        return ResponseEntity.ok().header("Cache-Control", "no-store")
                .body(etapas.confirmar(aprendizId, recorridoId, etapaId, entrada));
    }
}
