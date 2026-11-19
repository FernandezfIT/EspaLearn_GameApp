package com.espalearn.recorridos.controller;

import com.espalearn.recorridos.RecorridosApplication;
import com.espalearn.recorridos.dto.CierreEtapaRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.*;
import tools.jackson.databind.*;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** Función: verificar catálogo, cierres y cobertura F2 por HTTP contra MySQL real aislado. */
@SpringBootTest(classes = RecorridosApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
class VerticalHttpIT {
    // Entorno: reutilizar sólo la instancia de pruebas, nunca la base de la app.
    @DynamicPropertySource static void base(DynamicPropertyRegistry p) {
        String url = System.getenv().getOrDefault("F1_TEST_DB_URL", "jdbc:mysql://127.0.0.1:3308/espalearn_test?connectionTimeZone=UTC");
        if (!url.contains("/espalearn_test?")) throw new IllegalStateException("La integración exige espalearn_test.");
        p.add("spring.datasource.url", () -> url);
        p.add("spring.datasource.password", () -> Objects.requireNonNull(System.getenv("MYSQL_PASSWORD")));
    }
    @LocalServerPort int puerto;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate sql;
    private record Respuesta(int status, JsonNode cuerpo) { }
    private final HttpClient http = HttpClient.newHttpClient();

    // Utilidades: cuerpos reales del contrato y orden recibido del servidor, no de un fixture fijo.
    private String ruta(UUID a) { return "/api/v1/aprendices/" + a; }
    private Respuesta pedir(String metodo, String ruta, Object cuerpo) throws Exception {
        var req = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + ruta)).timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json").method(metodo, cuerpo == null ? HttpRequest.BodyPublishers.noBody()
                        : HttpRequest.BodyPublishers.ofString(json.writeValueAsString(cuerpo))).build();
        var respuesta = http.send(req, HttpResponse.BodyHandlers.ofString());
        return new Respuesta(respuesta.statusCode(), json.readTree(respuesta.body()));
    }
    private JsonNode crear(UUID a) throws Exception {
        var r = pedir("POST", ruta(a) + "/recorridos", Map.of("solicitudId", UUID.randomUUID(), "tipo", "NORMAL", "versionCatalogo", "prueba-3-v1"));
        assertEquals(201, r.status()); return r.cuerpo();
    }
    private CierreEtapaRequest cierre(JsonNode r, String etapa, UUID solicitud) throws Exception {
        JsonNode catalogo = pedir("GET", "/api/v1/catalogo?version=prueba-3-v1", null).cuerpo();
        var cartas = new HashMap<String, JsonNode>();
        catalogo.path("palabras").forEach(p -> cartas.put(p.path("palabraId").asText(), p));
        var resultados = new ArrayList<CierreEtapaRequest.Resultado>();
        for (JsonNode id : r.path("palabraIds")) {
            String opcion = etapa.equals("descubrir") ? null : cartas.get(id.asText()).path("opciones").get(0).path("id").asText();
            resultados.add(new CierreEtapaRequest.Resultado(id.asText(), opcion, etapa.equals("menos_apoyo")));
        }
        return new CierreEtapaRequest(solicitud, "prueba-3-v1", etapa, resultados);
    }
    private Respuesta cerrar(UUID a, JsonNode r, CierreEtapaRequest c) throws Exception {
        return pedir("PUT", ruta(a) + "/recorridos/" + r.path("recorridoId").asText() + "/etapas/" + c.etapa(), c);
    }

    // Vertical: nueve presentaciones/seis elecciones; equivocarse no bloquea la finalización.
    @Test void confirmaTresEtapasYCuentaTresPalabrasUnaVez() throws Exception {
        UUID a = UUID.randomUUID(); JsonNode r = crear(a);
        CierreEtapaRequest ultimo = null;
        for (String etapa : List.of("descubrir", "con_apoyo", "menos_apoyo")) {
            ultimo = cierre(r, etapa, UUID.randomUUID());
            var respuesta = cerrar(a, r, ultimo);
            assertEquals(200, respuesta.status());
            assertEquals(r.path("palabraIds"), respuesta.cuerpo().path("palabraIds"));
            int cobertura = pedir("GET", ruta(a) + "/progreso?version=prueba-3-v1", null).cuerpo().path("completadasUnicas").asInt();
            assertEquals(etapa.equals("menos_apoyo") ? 3 : 0, cobertura);
        }
        assertEquals("COMPLETADO", cerrar(a, r, ultimo).cuerpo().path("estado").asText());
        assertEquals(3L, sql.queryForObject("SELECT COUNT(*) FROM cierres_etapa WHERE recorrido_id=?", Long.class, r.path("recorridoId").asText()));
        var progreso = pedir("GET", ruta(a) + "/progreso?version=prueba-3-v1", null).cuerpo();
        assertTrue(progreso.path("primerRecorridoCompletado").asBoolean());
        assertEquals(3, progreso.path("totalPalabras").asInt());
    }
    @Test void noPermiteSaltarEtapas() throws Exception {
        UUID a = UUID.randomUUID(); JsonNode r = crear(a);
        var respuesta = cerrar(a, r, cierre(r, "con_apoyo", UUID.randomUUID()));
        assertEquals(409, respuesta.status());
        assertEquals("ETAPA_NO_DISPONIBLE", respuesta.cuerpo().path("codigo").asText());
        assertEquals(0L, sql.queryForObject("SELECT COUNT(*) FROM cierres_etapa WHERE recorrido_id=?", Long.class, r.path("recorridoId").asText()));
    }
    @Test void rechazaLoteIncompatibleSinConfirmar() throws Exception {
        UUID a = UUID.randomUUID(); JsonNode r = crear(a);
        var correcto = cierre(r, "descubrir", UUID.randomUUID());
        var invertido = new ArrayList<>(correcto.resultados()); Collections.reverse(invertido);
        var respuesta = cerrar(a, r, new CierreEtapaRequest(correcto.solicitudId(), correcto.versionCatalogo(), correcto.etapa(), invertido));
        assertEquals(409, respuesta.status());
        assertEquals("LOTE_INCOMPATIBLE", respuesta.cuerpo().path("codigo").asText());
    }
    @Test void noAceptaAciertosInventadosNiOpcionesAjenas() throws Exception {
        UUID a = UUID.randomUUID(); JsonNode r = crear(a);
        cerrar(a, r, cierre(r, "descubrir", UUID.randomUUID()));
        var c = cierre(r, "con_apoyo", UUID.randomUUID());
        var resultados = c.resultados().stream().map(x -> new CierreEtapaRequest.Resultado(x.palabraId(), "opcion-ajena", false)).toList();
        assertEquals(409, cerrar(a, r, new CierreEtapaRequest(c.solicitudId(), c.versionCatalogo(), c.etapa(), resultados)).status());
    }
    @Test void reintentoConOtrosResultadosNoSobrescribeHistoria() throws Exception {
        UUID a = UUID.randomUUID(); JsonNode r = crear(a);
        var original = cierre(r, "descubrir", UUID.randomUUID());
        assertEquals(200, cerrar(a, r, original).status());
        assertEquals(409, cerrar(a, r, new CierreEtapaRequest(UUID.randomUUID(), original.versionCatalogo(),
                original.etapa(), original.resultados())).status());
        var alterados = original.resultados().stream().map(x -> new CierreEtapaRequest.Resultado(x.palabraId(), "inventada", false)).toList();
        assertEquals(409, cerrar(a, r, new CierreEtapaRequest(UUID.randomUUID(), original.versionCatalogo(), original.etapa(), alterados)).status());
        assertEquals(409, cerrar(a, r, cierre(r, "con_apoyo", original.solicitudId())).status());
    }
    @Test void cierreConcurrenteConfirmaUnaSolaEtapa() throws Exception {
        UUID a = UUID.randomUUID(); JsonNode r = crear(a);
        var c = cierre(r, "descubrir", UUID.randomUUID());
        try (var pool = Executors.newFixedThreadPool(4)) {
            var futuros = new ArrayList<Future<Respuesta>>();
            for (int i = 0; i < 4; i++) futuros.add(pool.submit(() -> cerrar(a, r, c)));
            for (var futuro : futuros) assertEquals(200, futuro.get().status());
        }
        assertEquals(1L, sql.queryForObject("SELECT COUNT(*) FROM cierres_etapa WHERE recorrido_id=?", Long.class, r.path("recorridoId").asText()));
    }
    @Test void identidadAjenaNoPuedeConfirmarEtapa() throws Exception {
        JsonNode r = crear(UUID.randomUUID());
        assertEquals(404, cerrar(UUID.randomUUID(), r, cierre(r, "descubrir", UUID.randomUUID())).status());
    }
}
