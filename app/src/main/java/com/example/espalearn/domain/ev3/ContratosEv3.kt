package com.example.espalearn.domain.ev3

import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Progreso

/** Contrato de dominio F0. No es un cliente HTTP ni sustituye validaciones del servidor. */
enum class TipoRecorrido { NORMAL, REPASO }
enum class EstadoRecorrido { ACTIVO, COMPLETADO }
enum class EstadoFeedback { PENDIENTE, GENERANDO, DISPONIBLE, ALTERNATIVO }
enum class OrigenFeedback { GROQ, LOCAL }

/** Función: transferir identidad/preferencias sin exponer DataStore al ViewModel. */
data class PreferenciasUsuarioEv3(val aprendizId: String, val ultimoRecorridoId: String?,
                                 val marcoLegado: Boolean, val marcoAplicado: Boolean)

/** Función: comunicar un fallo público de operación sin filtrar excepciones de Retrofit/SQL a UI. */
class FalloOperacionEv3(val mensajePublico: String, cause: Throwable? = null) : Exception(mensajePublico, cause)

data class OpcionCatalogo(val id: String, val textoEspanol: String) {
    init { require(id.isNotBlank() && textoEspanol.isNotBlank()) }
}

/** Contenido editorial, no generado por Groq. La clave de imagen no es un entero R. */
data class PalabraCatalogo(
    val palabraId: String,
    val versionCatalogo: String,
    val textoEspanol: String,
    val apoyoTurco: String,
    val explicacion: String,
    val imagenClave: String,
    val opciones: List<OpcionCatalogo>,
    val respuestaEsperadaId: String
) {
    init {
        require(listOf(palabraId, versionCatalogo, textoEspanol, apoyoTurco, explicacion, imagenClave)
            .all { it.isNotBlank() })
        require(opciones.size == 2 && opciones.map { it.id }.distinct().size == 2)
        require(opciones.any { it.id == respuestaEsperadaId })
    }
}

data class RecorridoEv3(
    val recorridoId: String,
    val versionCatalogo: String,
    val tipo: TipoRecorrido,
    val estado: EstadoRecorrido,
    val palabraIds: List<String>,
    val etapasConfirmadas: List<Etapa>
) {
    init {
        require(recorridoId.isNotBlank() && versionCatalogo.isNotBlank())
        require(palabraIds.size == 3 && palabraIds.all { it.isNotBlank() })
        require(palabraIds.distinct().size == 3)
        require(etapasConfirmadas == Etapa.entries.take(etapasConfirmadas.size))
        require((estado == EstadoRecorrido.COMPLETADO) == (etapasConfirmadas.size == 3))
    }

    val etapaPendiente: Etapa? get() = Etapa.entries.getOrNull(etapasConfirmadas.size)
}

/** Descubrir no lleva opción ni aciertos; en práctica el servidor calcula la corrección. */
data class ResultadoPalabra(
    val palabraId: String,
    val opcionElegidaId: String?,
    val ayudaUsada: Boolean
) {
    init {
        require(palabraId.isNotBlank())
        require(opcionElegidaId == null || opcionElegidaId.isNotBlank())
    }
}

data class CierreEtapaEv3(
    val solicitudId: String,
    val versionCatalogo: String,
    val etapa: Etapa,
    val resultados: List<ResultadoPalabra>
) {
    init {
        require(solicitudId.isNotBlank() && versionCatalogo.isNotBlank())
        require(resultados.size == 3 && resultados.map { it.palabraId }.distinct().size == 3)
        if (etapa == Etapa.DESCUBRIR) {
            require(resultados.all { it.opcionElegidaId == null && !it.ayudaUsada })
        } else {
            require(resultados.all { it.opcionElegidaId != null })
        }
        if (etapa == Etapa.CON_APOYO) require(resultados.none { it.ayudaUsada })
    }

    /** Valida identidad y opciones contra una instantánea, sin fingir idempotencia SQL. */
    fun validarContra(recorrido: RecorridoEv3, palabras: List<PalabraCatalogo>) {
        require(recorrido.estado == EstadoRecorrido.ACTIVO && etapa == recorrido.etapaPendiente)
        require(versionCatalogo == recorrido.versionCatalogo)
        require(resultados.map { it.palabraId } == recorrido.palabraIds)
        require(palabras.map { it.palabraId } == recorrido.palabraIds)
        require(palabras.all { it.versionCatalogo == versionCatalogo })
        resultados.zip(palabras).forEach { (resultado, palabra) ->
            require(resultado.opcionElegidaId == null ||
                palabra.opciones.any { it.id == resultado.opcionElegidaId })
        }
    }
}

/** Cobertura no implica dominio. El total puede ser 3 en pruebas, 150 en entrega. */
data class ProgresoEv3(
    val completadasUnicas: Int,
    val totalPalabras: Int,
    val primerRecorridoCompletado: Boolean
) {
    init {
        require(totalPalabras > 0 && totalPalabras % 3 == 0)
        require(completadasUnicas in 0..totalPalabras && completadasUnicas % 3 == 0)
    }
    val repasoDisponible: Boolean get() = completadasUnicas == totalPalabras
}

data class FeedbackEtapaEv3(
    val estado: EstadoFeedback,
    val origen: OrigenFeedback?,
    val mensaje: String?,
    val versionFeedback: String
) {
    init {
        require(versionFeedback.isNotBlank())
        when (estado) {
            EstadoFeedback.PENDIENTE, EstadoFeedback.GENERANDO ->
                require(origen == null && mensaje == null)
            EstadoFeedback.DISPONIBLE -> require(origen == OrigenFeedback.GROQ && !mensaje.isNullOrBlank())
            EstadoFeedback.ALTERNATIVO -> require(origen == OrigenFeedback.LOCAL && !mensaje.isNullOrBlank())
        }
        require(mensaje == null || mensaje.codePointCount(0, mensaje.length) <= 240)
    }
}

/** Decisión de Felipe: preservar recompensa antigua sin inventar avance remoto nuevo. */
data class RecompensaLegada(val desbloqueada: Boolean, val aplicada: Boolean) {
    init { require(!aplicada || desbloqueada) }

    fun puedeUsarMarco(progreso: ProgresoEv3): Boolean =
        desbloqueada || progreso.primerRecorridoCompletado

    companion object {
        fun desdeEv2(progreso: Progreso): RecompensaLegada = RecompensaLegada(
            desbloqueada = progreso.moduloCompletado,
            aplicada = progreso.moduloCompletado && progreso.marcoAplicado
        )
    }
}

/** Puertos para implementar en F2/F3; por ahora no se inyectan en la app EV2. */
interface RecorridosEv3 {
    suspend fun crearORecuperar(solicitudId: String, tipo: TipoRecorrido, versionCatalogo: String): RecorridoEv3
    suspend fun obtenerActivo(): RecorridoEv3?
    suspend fun obtener(recorridoId: String): RecorridoEv3
    suspend fun confirmarEtapa(recorridoId: String, cierre: CierreEtapaEv3): RecorridoEv3
    suspend fun descartar(recorridoId: String)
    suspend fun obtenerProgreso(): ProgresoEv3
}

interface ContenidoEv3 {
    suspend fun preparar(recorrido: RecorridoEv3): List<PalabraCatalogo>
}

interface FeedbackEv3 {
    suspend fun solicitar(recorridoId: String, etapa: Etapa): FeedbackEtapaEv3
    suspend fun consultar(recorridoId: String, etapa: Etapa): FeedbackEtapaEv3
}
