package com.espalearn.recorridos.model;

import jakarta.persistence.*;
import java.util.UUID;

/** Función: recordar qué resultado tuvo una petición de inicio y detectar reintentos incompatibles. */
@Entity
@Table(name = "solicitudes_inicio")
public class SolicitudInicio {
    // Identidad: la BD impone también unicidad de aprendizId + solicitudId.
    @Id private String id;
    @Column(name = "aprendiz_id", nullable = false) private String aprendizId;
    @Column(name = "solicitud_id", nullable = false) private String solicitudId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Recorrido.Tipo tipo;
    @Column(name = "version_catalogo", nullable = false, length = 40) private String versionCatalogo;
    @Column(name = "recorrido_id", nullable = false) private String recorridoId;

    // Registro: se inserta en la misma transacción que crea o recupera el recorrido.
    protected SolicitudInicio() { }
    public SolicitudInicio(String aprendizId, String solicitudId, Recorrido.Tipo tipo,
                           String versionCatalogo, String recorridoId) {
        this.id = UUID.randomUUID().toString();
        this.aprendizId = aprendizId;
        this.solicitudId = solicitudId;
        this.tipo = tipo;
        this.versionCatalogo = versionCatalogo;
        this.recorridoId = recorridoId;
    }

    // Consulta: permite comparar el contenido original sin volver a seleccionar palabras.
    public Recorrido.Tipo getTipo() { return tipo; }
    public String getVersionCatalogo() { return versionCatalogo; }
    public String getRecorridoId() { return recorridoId; }
}
