package com.espalearn.recorridos.model;

import jakarta.persistence.*;

/** Función: representar una entrada del catálogo versionado seleccionable por el servidor. */
@Entity
@Table(name = "palabras")
public class Palabra {
    // Clave interna: no se expone como identidad de palabra al cliente Android.
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "palabra_id", nullable = false, length = 80)
    private String palabraId;
    @Column(name = "version_catalogo", nullable = false, length = 40)
    private String versionCatalogo;
    @Column(name = "texto_espanol", nullable = false, length = 100)
    private String textoEspanol;

    // Contenido: sin valores inventados para entradas anteriores que aún no fueron completadas.
    @Column(name = "apoyo_turco", length = 100) private String apoyoTurco;
    @Column(length = 500) private String explicacion;
    @Column(name = "imagen_clave", length = 100) private String imagenClave;
    @Column(name = "opcion_uno_id", length = 80) private String opcionUnoId;
    @Column(name = "opcion_uno_texto", length = 100) private String opcionUnoTexto;
    @Column(name = "opcion_dos_id", length = 80) private String opcionDosId;
    @Column(name = "opcion_dos_texto", length = 100) private String opcionDosTexto;
    @Column(name = "respuesta_esperada_id", length = 80) private String respuestaEsperadaId;

    // Lectura: los fixtures se insertan mediante Flyway, no desde el controlador.
    protected Palabra() { }
    public String getPalabraId() { return palabraId; }
    public String getVersionCatalogo() { return versionCatalogo; }
    public String getTextoEspanol() { return textoEspanol; }
    public String getApoyoTurco() { return apoyoTurco; }
    public String getExplicacion() { return explicacion; }
    public String getImagenClave() { return imagenClave; }
    public String getOpcionUnoId() { return opcionUnoId; }
    public String getOpcionUnoTexto() { return opcionUnoTexto; }
    public String getOpcionDosId() { return opcionDosId; }
    public String getOpcionDosTexto() { return opcionDosTexto; }
    public String getRespuestaEsperadaId() { return respuestaEsperadaId; }

    // Integridad: no entregar cartas parciales a Android ni fingir que están revisadas.
    public boolean contenidoCompleto() {
        return java.util.stream.Stream.of(apoyoTurco, explicacion, imagenClave, opcionUnoId,
                opcionUnoTexto, opcionDosId, opcionDosTexto, respuestaEsperadaId)
                .allMatch(v -> v != null && !v.isBlank())
                && !opcionUnoId.equals(opcionDosId)
                && (respuestaEsperadaId.equals(opcionUnoId) || respuestaEsperadaId.equals(opcionDosId));
    }
}
