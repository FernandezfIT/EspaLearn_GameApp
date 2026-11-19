package com.example.espalearn.repository

import com.example.espalearn.data.local.*
import com.example.espalearn.data.remote.*
import com.example.espalearn.domain.ev3.*
import com.example.espalearn.model.*
import com.google.gson.Gson
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.util.UUID
import java.io.IOException
import kotlinx.coroutines.CancellationException

/** Función: describir la vertical inyectable sin que ViewModels conozcan Retrofit, Room o DataStore. */
interface VerticalRepository {
    suspend fun preferencias(): PreferenciasUsuarioEv3
    suspend fun cargar(): EstadoVertical
    suspend fun preparar(): SesionPreparada
    suspend fun confirmar(preparada: SesionPreparada, resultados: List<ResultadoPalabra>): RecorridoEv3
    suspend fun aplicarMarco(aplicado: Boolean)
}
data class EstadoVertical(val recorrido: RecorridoEv3?, val progreso: ProgresoEv3)
data class SesionPreparada(val recorrido: RecorridoEv3, val etapa: Etapa, val unidades: List<Unidad>)

/** Función: coordinar avance remoto y contenido local sin crear una segunda autoridad de progreso. */
class VerticalRepositoryReal(
    private val api: RecorridosApi,
    private val cache: CacheContenido,
    private val prefs: PreferenciasEv3,
    private val imagenesDisponibles: Set<String>,
    private val version: String = "prueba-3-v1",
    private val gson: Gson = Gson()
) : VerticalRepository {
    // Identidad/preferencias: la migración no convierte etapas EV2 en palabras completadas EV3.
    override suspend fun preferencias() = operacion { prefs.leer() }
    override suspend fun aplicarMarco(aplicado: Boolean) = operacion { prefs.aplicarMarco(aplicado) }

    override suspend fun cargar(): EstadoVertical = operacion {
        val info = preferencias()
        val progreso = api.progreso(info.aprendizId, version).validar()
        val activo = leerActivo(info.aprendizId)
        val recorrido = activo ?: info.ultimoRecorridoId?.let { api.obtener(info.aprendizId, it).validar() }
        recorrido?.let { require(it.versionCatalogo == version) }
        return@operacion EstadoVertical(recorrido, progreso)
    }

    // Preparación: recuperar antes de crear; la caché sólo es válida por ID y versión completos.
    override suspend fun preparar(): SesionPreparada = operacion {
        val info = preferencias()
        val recorrido = leerActivo(info.aprendizId) ?: api.crear(info.aprendizId,
            InicioDto(prefs.solicitudInicio(), versionCatalogo = version)).validar()
        require(recorrido.versionCatalogo == version && recorrido.estado == EstadoRecorrido.ACTIVO)
        prefs.recordarRecorrido(recorrido.recorridoId)
        val etapa = requireNotNull(recorrido.etapaPendiente)
        val guardadas = cache.leer(version, recorrido.palabraIds).mapNotNull { entidad ->
            // Corrupción de contenido: reconstruible desde servidor; un fallo SQL no se oculta aquí.
            try {
                val dto = requireNotNull(gson.fromJson(entidad.contenidoJson, CartaDto::class.java))
                val carta = dto.validar()
                if (carta.palabraId != entidad.palabraId || carta.versionCatalogo != entidad.versionCatalogo) null
                else carta.takeIf { it.imagenClave in imagenesDisponibles }
            } catch (_: IllegalArgumentException) { null }
            catch (_: JsonParseException) { null }
        }.associateBy { it.palabraId }
        val contenido = if (recorrido.palabraIds.all { guardadas[it]?.versionCatalogo == version }) {
            recorrido.palabraIds.map { guardadas.getValue(it) }
        } else {
            val respuesta = api.catalogo(version)
            require(respuesta.versionCatalogo == version)
            val dtos = requireNotNull(respuesta.palabras)
            val cartas = dtos.map { it.validar() }
            require(cartas.all { it.versionCatalogo == version })
            require(cartas.map { it.palabraId }.distinct().size == cartas.size)
            val porId = cartas.associateBy { it.palabraId }
            // Una caché parcial no autoriza cambiar las instantáneas válidas del mismo recorrido.
            val seleccion = recorrido.palabraIds.map { guardadas[it] ?: requireNotNull(porId[it]) }
            require(seleccion.all { it.imagenClave in imagenesDisponibles })
            cache.guardar(seleccion.map { ContenidoEntity(it.palabraId, version, gson.toJson(it)) })
            seleccion
        }
        return@operacion SesionPreparada(recorrido, etapa, contenido.map { carta ->
            Unidad(carta.palabraId, TemaUnidad.ALIMENTOS, carta.textoEspanol, carta.apoyoTurco,
                carta.imagenClave, carta.opciones.map { OpcionUnidad(it.id, it.textoEspanol) },
                carta.respuestaEsperadaId, carta.explicacion)
        })
    }

    // Confirmación: identidad fija por recorrido/etapa; sólo devolver éxito tras respuesta validada.
    override suspend fun confirmar(preparada: SesionPreparada, resultados: List<ResultadoPalabra>): RecorridoEv3 = operacion {
        val info = preferencias()
        val recorrido = preparada.recorrido
        val solicitud = UUID.nameUUIDFromBytes("${info.aprendizId}/${recorrido.recorridoId}/${preparada.etapa.id}".toByteArray(Charsets.UTF_8)).toString()
        val cierre = CierreEtapaEv3(solicitud, version, preparada.etapa, resultados)
        require(resultados.map { it.palabraId } == recorrido.palabraIds)
        val confirmado = api.cerrar(info.aprendizId, recorrido.recorridoId, preparada.etapa.id,
            CierreDto(cierre.solicitudId, cierre.versionCatalogo, cierre.etapa.id, cierre.resultados)).validar()
        require(confirmado.recorridoId == recorrido.recorridoId && confirmado.versionCatalogo == version)
        require(confirmado.palabraIds == recorrido.palabraIds && preparada.etapa in confirmado.etapasConfirmadas)
        return@operacion confirmado
    }

    // Frontera MVVM: traducir tecnología a fallo de dominio; no capturar cancelación como negocio.
    private suspend fun <T> operacion(block: suspend () -> T): T = try { block() }
    catch (error: CancellationException) { throw error }
    catch (error: FalloOperacionEv3) { throw error }
    catch (error: Exception) {
        val mensaje = when (error) {
            is SecurityException -> "Autoriza la conexión local antes de volver a intentarlo."
            is IOException -> "No se pudo leer, guardar o confirmar la operación. Reintenta o consulta el estado desde Inicio."
            is HttpException -> when (error.code()) {
                409 -> "El servidor detectó un conflicto. Actualiza el recorrido antes de continuar."
                404 -> "El recorrido ya no está disponible para esta identidad."
                else -> "El servidor no pudo confirmar la operación. Reintenta."
            }
            else -> "No se pudo preparar o validar el contenido guardado. Reintenta."
        }
        throw FalloOperacionEv3(mensaje, error)
    }

    // Transporte: separar ausencia204 de error HTTP, respuesta vacía o contrato inválido.
    private suspend fun leerActivo(aprendiz: String): RecorridoEv3? {
        val respuesta = api.activo(aprendiz)
        if (respuesta.code() == 204) return null
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
        return requireNotNull(respuesta.body()).validar()
    }
}
