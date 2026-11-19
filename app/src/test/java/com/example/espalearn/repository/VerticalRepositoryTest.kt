package com.example.espalearn.repository

import androidx.datastore.preferences.core.*
import com.example.espalearn.data.local.*
import com.example.espalearn.data.remote.*
import com.example.espalearn.domain.ev3.*
import com.example.espalearn.model.Etapa
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TemporaryFolder
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.File
import java.util.UUID

/** Función: probar protocolo/caché/migración sin confundir estos dobles con Room o MySQL reales. */
class VerticalRepositoryTest {
    @get:Rule val temporal = TemporaryFolder()
    private lateinit var scope: CoroutineScope
    private lateinit var prefs: PreferenciasEv3
    private lateinit var api: ApiPrueba
    private lateinit var cache: CachePrueba
    private lateinit var repo: VerticalRepositoryReal
    private lateinit var store: androidx.datastore.core.DataStore<Preferences>

    @Before fun preparar() {
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        store = PreferenceDataStoreFactory.create(scope = scope) { File(temporal.root, "perfil.preferences_pb") }
        prefs = PreferenciasEv3(store)
        api = ApiPrueba(); cache = CachePrueba()
        repo = VerticalRepositoryReal(api, cache, prefs, setOf("ilustracion_pan", "ilustracion_manzana", "ilustracion_pera"))
    }
    @After fun limpiar() { scope.cancel() }

    // Preparación: el orden viene del servidor y la lectura siguiente reutiliza contenido válido.
    @Test fun `prepara tres cartas y usa cache en la siguiente apertura`() = runTest {
        val primera = repo.preparar()
        val segunda = repo.preparar()
        assertEquals(listOf("pan", "manzana", "pera"), primera.unidades.map { it.id })
        assertEquals(primera, segunda)
        assertEquals(1, api.creaciones)
        assertEquals(1, api.catalogos)
        assertEquals(3, cache.cartas.size)
    }
    @Test fun `cache malformada se reconstruye sin inventar avance`() = runTest {
        repo.preparar()
        cache.cartas[0] = ContenidoEntity("pan", "prueba-3-v1", "{}")
        val lista = repo.preparar()
        assertEquals(3, lista.unidades.size)
        assertEquals(2, api.catalogos)
        assertEquals(0, repo.cargar().progreso.completadasUnicas)
    }
    @Test fun `cache parcial conserva las instantaneas validas del recorrido`() = runTest {
        repo.preparar()
        val pan = cache.cartas.first()
        cache.cartas[0] = ContenidoEntity(pan.palabraId, pan.versionCatalogo,
            pan.contenidoJson.replace("Explicación sintética", "Instantánea previa"))
        cache.cartas[1] = ContenidoEntity("manzana", "prueba-3-v1", "null")
        assertEquals("Instantánea previa", repo.preparar().unidades.first().explicacion)
        assertEquals("Instantánea previa", repo.preparar().unidades.first().explicacion)
        assertEquals(2, api.catalogos)
    }
    @Test fun `error HTTP no se interpreta como ausencia ni crea otro recorrido`() = runTest {
        api.falloActivo = true
        try { repo.preparar(); fail("Se esperaba fallo HTTP") } catch (e: FalloOperacionEv3) { assertEquals(503, (e.cause as HttpException).code()) }
        assertEquals(0, api.creaciones)
    }
    @Test fun `recurso grafico desconocido bloquea preparacion`() = runTest {
        api.imagen = "imagen_inexistente"
        try { repo.preparar(); fail("No debe presentar una imagen incorrecta") } catch (e: FalloOperacionEv3) { assertTrue(e.cause is IllegalArgumentException) }
        assertEquals(0, cache.cartas.size)
    }
    @Test fun `cierre envia etapa wire y conserva identidad para reintentos`() = runTest {
        val preparada = repo.preparar()
        val resultados = preparada.unidades.map { ResultadoPalabra(it.id, null, false) }
        repo.confirmar(preparada, resultados)
        repo.confirmar(preparada, resultados)
        assertEquals("descubrir", api.cierres.first().etapa)
        assertEquals(api.cierres.first(), api.cierres.last())
        assertEquals(preparada.recorrido.palabraIds, api.cierres.first().resultados.map { it.palabraId })
    }

    // Legado: mantener identidad/recompensa y no convertir etapas antiguas en cobertura remota.
    @Test fun `marco obtenido pero desactivado se conserva sin sumar palabras`() = runTest {
        store.edit {
            it[stringPreferencesKey("etapas_completadas")] = "descubrir,con_apoyo,menos_apoyo"
            it[booleanPreferencesKey("marco_aplicado")] = false
            it[stringPreferencesKey("perfil_nick")] = "PerfilFicticio"
        }
        val inicial = prefs.leer()
        assertTrue(inicial.marcoLegado)
        assertFalse(inicial.marcoAplicado)
        assertEquals(inicial.aprendizId, prefs.leer().aprendizId)
        assertEquals(0, repo.cargar().progreso.completadasUnicas)
        assertFalse(repo.cargar().progreso.primerRecorridoCompletado)
    }
    @Test fun `solicitud incierta y preferencia se conservan entre lecturas`() = runTest {
        assertEquals(prefs.solicitudInicio(), prefs.solicitudInicio())
        val id = UUID.randomUUID().toString()
        prefs.recordarRecorrido(id)
        prefs.aplicarMarco(true)
        assertEquals(id, prefs.leer().ultimoRecorridoId)
        assertTrue(prefs.leer().marcoAplicado)
    }

    // Dobles explícitos: respuestas sintéticas, no simulación de garantías transaccionales SQL.
    private class CachePrueba : CacheContenido {
        val cartas = mutableListOf<ContenidoEntity>()
        override suspend fun leer(version: String, ids: List<String>) = cartas.filter { it.versionCatalogo == version && it.palabraId in ids }
        override suspend fun guardar(cartas: List<ContenidoEntity>) { this.cartas.clear(); this.cartas.addAll(cartas) }
    }
    private class ApiPrueba : RecorridosApi {
        var actual: RecorridoDto? = null
        var creaciones = 0; var catalogos = 0; var falloActivo = false
        var imagen = "ilustracion_pan"
        val cierres = mutableListOf<CierreDto>()
        override suspend fun activo(aprendiz: String): Response<RecorridoDto> =
            if (falloActivo) Response.error(503, "{}".toResponseBody()) else actual?.let { Response.success(it) } ?: Response.success<RecorridoDto>(204, null)
        override suspend fun obtener(aprendiz: String, id: String) = requireNotNull(actual)
        override suspend fun progreso(aprendiz: String, version: String) = ProgresoDto(0, 3, false)
        override suspend fun crear(aprendiz: String, entrada: InicioDto): RecorridoDto {
            creaciones++
            return RecorridoDto(UUID.randomUUID().toString(), entrada.versionCatalogo, TipoRecorrido.NORMAL,
                EstadoRecorrido.ACTIVO, listOf("pan", "manzana", "pera"), emptyList()).also { actual = it }
        }
        override suspend fun catalogo(version: String): CatalogoDto {
            catalogos++
            return CatalogoDto(version, listOf("pan", "manzana", "pera").map { id ->
                CartaDto(id, version, id, "Apoyo sintético", "Explicación sintética", if (id == "pan") imagen else "ilustracion_$id",
                    listOf(OpcionUnidadDto("${id}_a", id), OpcionUnidadDto("${id}_b", "Otra")), "${id}_a")
            })
        }
        override suspend fun cerrar(aprendiz: String, id: String, etapa: String, entrada: CierreDto): RecorridoDto {
            cierres.add(entrada)
            return requireNotNull(actual).copy(etapasConfirmadas = listOf(etapa))
        }
    }
}
