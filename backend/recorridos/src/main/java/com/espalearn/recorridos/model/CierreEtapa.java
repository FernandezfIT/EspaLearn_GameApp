package com.espalearn.recorridos.model;

import jakarta.persistence.*;
import java.util.UUID;

/** Función: retener el contenido aceptado de un cierre para deduplicar y preparar feedback futuro. */
@Entity
@Table(name = "cierres_etapa")
public class CierreEtapa {
    // Identificadores y huella: la BD protege etapa/solicitud por recorrido con UNIQUE.
    @Id private String id;
    @Column(name = "recorrido_id", nullable = false) private String recorridoId;
    @Column(nullable = false, length = 20) private String etapa;
    @Column(name = "solicitud_id", nullable = false) private String solicitudId;
    @Column(nullable = false, length = 64) private String huella;
    @Column(name = "resultados_json", nullable = false, columnDefinition = "TEXT") private String resultadosJson;

    // Persistencia: sólo se construye después de validar resultados contra el catálogo del lote.
    protected CierreEtapa() { }
    public CierreEtapa(String recorridoId, String etapa, String solicitudId, String huella, String resultadosJson) {
        this.id = UUID.randomUUID().toString();
        this.recorridoId = recorridoId;
        this.etapa = etapa;
        this.solicitudId = solicitudId;
        this.huella = huella;
        this.resultadosJson = resultadosJson;
    }
    public String getSolicitudId() { return solicitudId; }
    public String getHuella() { return huella; }
    public String getEtapa() { return etapa; }
}
