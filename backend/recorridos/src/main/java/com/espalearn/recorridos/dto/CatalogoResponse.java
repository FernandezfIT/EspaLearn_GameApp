package com.espalearn.recorridos.dto;

import com.espalearn.recorridos.model.Palabra;
import java.util.List;

/** Función: transferir contenido editorial versionado para validarlo y guardarlo en Room. */
public record CatalogoResponse(String versionCatalogo, List<Carta> palabras) {
    // Transporte: las opciones e imagen son revisables; no las produce Groq.
    public record Opcion(String id, String textoEspanol) { }
    public record Carta(String palabraId, String versionCatalogo, String textoEspanol,
                        String apoyoTurco, String explicacion, String imagenClave,
                        List<Opcion> opciones, String respuestaEsperadaId) { }

    // Mapeo: no exponer claves SQL internas; conservar IDs del contrato Android F0.
    public static Carta carta(Palabra p) {
        return new Carta(p.getPalabraId(), p.getVersionCatalogo(), p.getTextoEspanol(),
                p.getApoyoTurco(), p.getExplicacion(), p.getImagenClave(),
                List.of(new Opcion(p.getOpcionUnoId(), p.getOpcionUnoTexto()),
                        new Opcion(p.getOpcionDosId(), p.getOpcionDosTexto())), p.getRespuestaEsperadaId());
    }
}
