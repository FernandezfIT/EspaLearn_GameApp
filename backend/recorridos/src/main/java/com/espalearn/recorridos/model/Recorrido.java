package com.espalearn.recorridos.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Función: persistir un lote fijo y ordenado, independiente de las sesiones de pantalla. */
@Entity
@Table(name = "recorridos")
public class Recorrido {
    // Estados del contrato: F2 confirma las tres etapas sin exigir nota mínima.
    public enum Tipo { NORMAL, REPASO }
    public enum Estado { ACTIVO, COMPLETADO }

    // Cabecera: identifica dueño, versión y momento del recorrido sin datos personales.
    @Id private String id;
    @Column(name = "aprendiz_id", nullable = false) private String aprendizId;
    @Column(name = "version_catalogo", nullable = false, length = 40) private String versionCatalogo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Tipo tipo;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Estado estado;
    @Column(name = "creado_en", nullable = false) private Instant creadoEn;

    // Selección: @OrderColumn guarda la posición, no depende del orden de un SELECT.
    @ManyToMany
    @JoinTable(name = "recorrido_palabras", joinColumns = @JoinColumn(name = "recorrido_id"),
            inverseJoinColumns = @JoinColumn(name = "palabra_id"))
    @OrderColumn(name = "posicion")
    private List<Palabra> palabras = new ArrayList<>();

    // Etapas: cada confirmación se persiste en orden; el lote de palabras no se modifica.
    @ElementCollection
    @CollectionTable(name = "recorrido_etapas", joinColumns = @JoinColumn(name = "recorrido_id"))
    @OrderColumn(name = "posicion") @Column(name = "etapa", length = 20)
    private List<String> etapasConfirmadas = new ArrayList<>();

    // Creación: exige tres palabras distintas antes de abrir un recorrido persistente.
    protected Recorrido() { }
    public Recorrido(String aprendizId, String versionCatalogo, Tipo tipo, List<Palabra> palabras) {
        if (palabras.size() != 3 || palabras.stream().map(Palabra::getPalabraId).distinct().count() != 3) {
            throw new IllegalArgumentException("El recorrido requiere tres palabras distintas.");
        }
        this.id = UUID.randomUUID().toString();
        this.aprendizId = aprendizId;
        this.versionCatalogo = versionCatalogo;
        this.tipo = tipo;
        this.estado = Estado.ACTIVO;
        this.creadoEn = Instant.now();
        this.palabras = new ArrayList<>(palabras);
    }

    // Transición: el servicio ya validó identidad, contenido e idempotencia antes de llamarla.
    public void confirmar(String etapa) {
        var orden = List.of("descubrir", "con_apoyo", "menos_apoyo");
        if (estado != Estado.ACTIVO || etapasConfirmadas.size() >= orden.size()
                || !orden.get(etapasConfirmadas.size()).equals(etapa)) {
            throw new IllegalStateException("Transición de etapa inválida.");
        }
        etapasConfirmadas.add(etapa);
        if (etapasConfirmadas.size() == orden.size()) estado = Estado.COMPLETADO;
    }

    // Salida: el servicio transforma esta entidad en DTO dentro de la transacción.
    public String getId() { return id; }
    public String getVersionCatalogo() { return versionCatalogo; }
    public Tipo getTipo() { return tipo; }
    public Estado getEstado() { return estado; }
    public List<Palabra> getPalabras() { return List.copyOf(palabras); }
    public List<String> getEtapasConfirmadas() { return List.copyOf(etapasConfirmadas); }
}
