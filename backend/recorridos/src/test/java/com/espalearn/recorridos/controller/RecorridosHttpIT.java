package com.espalearn.recorridos.controller;

import com.espalearn.recorridos.RecorridosApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.dao.DataIntegrityViolationException;

/** Función: probar HTTP MVC + servicio + MySQL real; ejecutar sólo con -Pintegration y BD separada. */
@SpringBootTest(classes = RecorridosApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("demo")
class RecorridosHttpIT {
    // Entorno: impedir que la suite use accidentalmente la base principal de la aplicación.
    @DynamicPropertySource static void baseSeparada(DynamicPropertyRegistry propiedades) {
        String url = System.getenv().getOrDefault("F1_TEST_DB_URL", "jdbc:mysql://127.0.0.1:3308/espalearn_test?connectionTimeZone=UTC");
        if (!url.contains("/espalearn_test?")) throw new IllegalStateException("Se exige una base espalearn_test separada.");
        propiedades.add("spring.datasource.url", () -> url);
        propiedades.add("spring.datasource.password", () -> Objects.requireNonNull(System.getenv("MYSQL_PASSWORD"), "Define MYSQL_PASSWORD para la BD de pruebas."));
    }
    @LocalServerPort int puerto;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate sql;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    private record Respuesta(int status, JsonNode cuerpo) { }

    // Utilidades HTTP: el test cruza el puerto real, no llama directamente al controlador.
    private Respuesta pedir(String metodo, UUID aprendiz, String ruta, String cuerpo) throws Exception {
        var peticion = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + puerto + "/api/v1/aprendices/" + aprendiz + "/recorridos" + ruta))
                .timeout(Duration.ofSeconds(25)).header("Content-Type", "application/json")
                .method(metodo, cuerpo == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(cuerpo)).build();
        var respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofString());
        return new Respuesta(respuesta.statusCode(), respuesta.body().isBlank() ? json.createObjectNode() : json.readTree(respuesta.body()));
    }
    private String inicio(UUID solicitud, String tipo, String version) {
        return "{\"solicitudId\":\"" + solicitud + "\",\"tipo\":\"" + tipo + "\",\"versionCatalogo\":\"" + version + "\"}";
    }
    private Respuesta crear(UUID aprendiz, UUID solicitud) throws Exception {
        return pedir("POST", aprendiz, "", inicio(solicitud, "NORMAL", "prueba-3-v1"));
    }

    // Contrato: 201 al crear, recuperación idéntica y lista/estado compatibles con F0.
    @Test void creaYConsultaMismoLote() throws Exception {
        UUID aprendiz = UUID.randomUUID();
        var creada = crear(aprendiz, UUID.randomUUID());
        assertEquals(201, creada.status());
        assertEquals("ACTIVO", creada.cuerpo().path("estado").asText());
        assertEquals(3, creada.cuerpo().path("palabraIds").size());
        var ids = new HashSet<String>();
        creada.cuerpo().path("palabraIds").forEach(n -> ids.add(n.asText()));
        assertEquals(Set.of("pan", "manzana", "pera"), ids);
        assertTrue(creada.cuerpo().path("etapasConfirmadas").isEmpty());
        assertEquals(creada.cuerpo(), pedir("GET", aprendiz, "/activo", null).cuerpo());
        assertEquals(creada.cuerpo(), pedir("GET", aprendiz, "/" + creada.cuerpo().path("recorridoId").asText(), null).cuerpo());
        assertEquals(Set.of("recorridoId", "versionCatalogo", "tipo", "estado", "palabraIds", "etapasConfirmadas"), creada.cuerpo().propertyNames());
    }
    @Test void mismaSolicitudYUnaNuevaNoVuelvenASortear() throws Exception {
        UUID aprendiz = UUID.randomUUID(), solicitud = UUID.randomUUID();
        var primera = crear(aprendiz, solicitud);
        var repetida = crear(aprendiz, solicitud);
        var nueva = crear(aprendiz, UUID.randomUUID());
        assertEquals(200, repetida.status());
        assertEquals(200, nueva.status());
        assertEquals(primera.cuerpo(), repetida.cuerpo());
        assertEquals(primera.cuerpo(), nueva.cuerpo());
        assertEquals(1L, sql.queryForObject("SELECT COUNT(*) FROM recorridos WHERE aprendiz_id=?", Long.class, aprendiz.toString()));
        assertEquals(2L, sql.queryForObject("SELECT COUNT(*) FROM solicitudes_inicio WHERE aprendiz_id=?", Long.class, aprendiz.toString()));
    }
    @Test void ausenciaNoCreaDatosNiOcultaErrores() throws Exception {
        UUID aprendiz = UUID.randomUUID();
        assertEquals(204, pedir("GET", aprendiz, "/activo", null).status());
        assertEquals(0L, sql.queryForObject("SELECT COUNT(*) FROM aprendices WHERE id=?", Long.class, aprendiz.toString()));
    }
    @Test void noEntregaUnRecorridoDeOtraIdentidad() throws Exception {
        var propia = crear(UUID.randomUUID(), UUID.randomUUID());
        var ajena = pedir("GET", UUID.randomUUID(), "/" + propia.cuerpo().path("recorridoId").asText(), null);
        assertEquals(404, ajena.status());
        assertEquals("RECORRIDO_NO_ENCONTRADO", ajena.cuerpo().path("codigo").asText());
    }

    // Conflictos: conservar solicitud/activo y no persistir altas parciales tras rollback.
    @Test void reintentoConOtraVersionDaConflicto() throws Exception {
        UUID aprendiz = UUID.randomUUID(), solicitud = UUID.randomUUID();
        crear(aprendiz, solicitud);
        var respuesta = pedir("POST", aprendiz, "", inicio(solicitud, "NORMAL", "otra"));
        assertEquals(409, respuesta.status());
        assertEquals("SOLICITUD_INCOMPATIBLE", respuesta.cuerpo().path("codigo").asText());
    }
    @Test void activoIncompatibleNoCambiaElLote() throws Exception {
        UUID aprendiz = UUID.randomUUID();
        var primera = crear(aprendiz, UUID.randomUUID());
        var respuesta = pedir("POST", aprendiz, "", inicio(UUID.randomUUID(), "REPASO", "prueba-3-v1"));
        assertEquals(409, respuesta.status());
        assertEquals("ACTIVO_INCOMPATIBLE", respuesta.cuerpo().path("codigo").asText());
        assertEquals(primera.cuerpo(), pedir("GET", aprendiz, "/activo", null).cuerpo());
    }
    @Test void versionInexistenteRevierteLaIdentidadInsertada() throws Exception {
        UUID aprendiz = UUID.randomUUID();
        var respuesta = pedir("POST", aprendiz, "", inicio(UUID.randomUUID(), "NORMAL", "no-existe"));
        assertEquals(409, respuesta.status());
        assertEquals("CATALOGO_INCOMPATIBLE", respuesta.cuerpo().path("codigo").asText());
        assertEquals(0L, sql.queryForObject("SELECT COUNT(*) FROM aprendices WHERE id=?", Long.class, aprendiz.toString()));
    }
    @Test void versionDeCatalogoDistingueMayusculas() throws Exception {
        var respuesta = pedir("POST", UUID.randomUUID(), "", inicio(UUID.randomUUID(), "NORMAL", "PRUEBA-3-V1"));
        assertEquals(409, respuesta.status());
        assertEquals("CATALOGO_INCOMPATIBLE", respuesta.cuerpo().path("codigo").asText());
    }
    @Test void repasoSinAvanceSeRechaza() throws Exception {
        var respuesta = pedir("POST", UUID.randomUUID(), "", inicio(UUID.randomUUID(), "REPASO", "prueba-3-v1"));
        assertEquals(409, respuesta.status());
        assertEquals("REPASO_NO_DISPONIBLE", respuesta.cuerpo().path("codigo").asText());
    }
    @Test void validaUuidJsonYCamposObligatorios() throws Exception {
        UUID aprendiz = UUID.randomUUID();
        assertEquals(400, pedir("POST", aprendiz, "", "{}").status());
        assertEquals(400, pedir("POST", aprendiz, "", "{invalid").status());
        assertEquals(400, pedir("GET", aprendiz, "/no-es-uuid", null).status());
        assertEquals(400, pedir("POST", aprendiz, "", inicio(UUID.randomUUID(), "OTRO", "prueba-3-v1")).status());
    }

    // Concurrencia real: solicitudes simultáneas se coordinan en MySQL, no con mocks.
    @Test void solicitudesConcurrentesConservanUnSoloActivo() throws Exception {
        UUID aprendiz = UUID.randomUUID();
        try (var pool = Executors.newFixedThreadPool(8)) {
            var salida = new CountDownLatch(1);
            List<Future<Respuesta>> futuros = new ArrayList<>();
            for (int i = 0; i < 8; i++) futuros.add(pool.submit(() -> { salida.await(); return crear(aprendiz, UUID.randomUUID()); }));
            salida.countDown();
            Set<JsonNode> cuerpos = new HashSet<>();
            int creadas = 0;
            for (var futuro : futuros) {
                var respuesta = futuro.get(40, TimeUnit.SECONDS);
                assertTrue(respuesta.status() == 200 || respuesta.status() == 201, respuesta.toString());
                if (respuesta.status() == 201) creadas++;
                cuerpos.add(respuesta.cuerpo());
            }
            assertEquals(1, creadas);
            assertEquals(1, cuerpos.size());
            assertEquals(1L, sql.queryForObject("SELECT COUNT(*) FROM recorridos WHERE aprendiz_id=?", Long.class, aprendiz.toString()));
        }
    }
    @Test void mismoInicioConcurrenteSeRegistraUnaSolaVez() throws Exception {
        UUID aprendiz = UUID.randomUUID(), solicitud = UUID.randomUUID();
        try (var pool = Executors.newFixedThreadPool(4)) {
            List<Future<Respuesta>> futuros = new ArrayList<>();
            for (int i = 0; i < 4; i++) futuros.add(pool.submit(() -> crear(aprendiz, solicitud)));
            Set<JsonNode> cuerpos = new HashSet<>();
            for (var futuro : futuros) {
                var respuesta = futuro.get(40, TimeUnit.SECONDS);
                assertTrue(respuesta.status() == 200 || respuesta.status() == 201);
                cuerpos.add(respuesta.cuerpo());
            }
            assertEquals(1, cuerpos.size());
            assertEquals(1L, sql.queryForObject("SELECT COUNT(*) FROM solicitudes_inicio WHERE aprendiz_id=?", Long.class, aprendiz.toString()));
        }
    }

    // Historia sintética: verifica exclusión sin fingir que F1 ya implementa cierres desde UI.
    @Test void palabrasCompletadasNoSeSeleccionanEnOtroNormal() throws Exception {
        UUID aprendiz = UUID.randomUUID();
        var creado = crear(aprendiz, UUID.randomUUID());
        String id = creado.cuerpo().path("recorridoId").asText();
        sql.update("UPDATE recorridos SET estado='COMPLETADO' WHERE id=?", id);
        List<String> etapas = List.of("descubrir", "con_apoyo", "menos_apoyo");
        for (int i = 0; i < etapas.size(); i++) {
            sql.update("INSERT INTO recorrido_etapas(recorrido_id,posicion,etapa) VALUES (?,?,?)", id, i, etapas.get(i));
        }
        var respuesta = crear(aprendiz, UUID.randomUUID());
        assertEquals(409, respuesta.status());
        assertEquals("PALABRAS_INSUFICIENTES", respuesta.cuerpo().path("codigo").asText());
        assertEquals(3, pedir("GET", aprendiz, "/" + id, null).cuerpo().path("etapasConfirmadas").size());
    }

    @Test void restriccionSqlImpideDosActivosAunqueSeOmitaElServicio() throws Exception {
        UUID aprendiz = UUID.randomUUID();
        crear(aprendiz, UUID.randomUUID());
        assertThrows(DataIntegrityViolationException.class, () -> sql.update("""
                INSERT INTO recorridos (id, aprendiz_id, version_catalogo, tipo, estado, creado_en)
                VALUES (?, ?, 'prueba-3-v1', 'NORMAL', 'ACTIVO', CURRENT_TIMESTAMP(6))
                """, UUID.randomUUID().toString(), aprendiz.toString()));
    }
}
