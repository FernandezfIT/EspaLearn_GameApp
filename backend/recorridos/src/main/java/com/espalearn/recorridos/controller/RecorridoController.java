package com.espalearn.recorridos.controller;

import com.espalearn.recorridos.dto.*;
import com.espalearn.recorridos.service.RecorridoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

/** Función: adaptar HTTP a las reglas de recorridos; en esta API REST la vista es JSON. */
@RestController
@RequestMapping("/api/v1/aprendices/{aprendizId}/recorridos")
public class RecorridoController {
    // Inyección: ninguna consulta SQL ni selección aleatoria se realiza en el controlador.
    private final RecorridoService servicio;
    public RecorridoController(RecorridoService servicio) { this.servicio = servicio; }

    // Comando: 201 y Location al crear; 200 al recuperar un resultado ya existente.
    @PostMapping
    public ResponseEntity<RecorridoResponse> crear(@PathVariable UUID aprendizId,
                                                  @Valid @RequestBody CrearRecorridoRequest entrada) {
        var inicio = servicio.crearORecuperar(aprendizId, entrada);
        var respuesta = inicio.creado()
                ? ResponseEntity.created(URI.create("/api/v1/aprendices/" + aprendizId + "/recorridos/" + inicio.recorrido().recorridoId()))
                : ResponseEntity.ok();
        return respuesta.header("Cache-Control", "no-store").body(inicio.recorrido());
    }

    // Consulta de activo: 204 indica ausencia; no se utiliza 204 para ocultar fallos de la BD.
    @GetMapping("/activo")
    public ResponseEntity<RecorridoResponse> activo(@PathVariable UUID aprendizId) {
        return servicio.activo(aprendizId)
                .map(r -> ResponseEntity.ok().header("Cache-Control", "no-store").body(r))
                .orElseGet(() -> ResponseEntity.noContent().header("Cache-Control", "no-store").build());
    }

    // Consulta por identidad: un ID ajeno devuelve 404, no el recorrido de otro aprendiz.
    @GetMapping("/{recorridoId}")
    public ResponseEntity<RecorridoResponse> obtener(@PathVariable UUID aprendizId, @PathVariable UUID recorridoId) {
        return ResponseEntity.ok().header("Cache-Control", "no-store")
                .body(servicio.obtener(aprendizId, recorridoId));
    }
}
