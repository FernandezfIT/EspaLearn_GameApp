package com.example.espalearn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.OpcionUnidad
import com.example.espalearn.model.Unidad
import com.example.espalearn.model.state.FaseSesion
import com.example.espalearn.model.state.SesionUiState
import com.example.espalearn.repository.AppRepository
import com.example.espalearn.repository.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Sesión de tarjetas de una etapa (K3/K4).
 *
 * Contenido de EV2: Descubrir no pregunta; las dos etapas de práctica usan
 * dos opciones y una única elección admitida por paso. Botón y swipe envían
 * el mismo identificador de opción, así que la deduplicación se resuelve
 * aquí por identidad de sesión y paso: una elección duplicada o tardía no
 * registra respuesta ni avanza. Continuar es siempre manual y la etapa sólo
 * se anuncia completada tras confirmar su guardado.
 *
 * El catálogo de unidades se inyecta para poder probar con datos sintéticos;
 * el contenido real y sus ilustraciones llegan con su bloque correspondiente.
 */
class SesionViewModel(
    private val repositorio: AppRepository,
    private val etapa: Etapa,
    private val unidades: List<Unidad>,
    idSesion: String = UUID.randomUUID().toString()
) : ViewModel() {

    private val pasos: List<Paso> = construirPasos(etapa, unidades)

    private val _estado = MutableStateFlow(
        SesionUiState(
            idSesion = idSesion,
            etapa = etapa,
            totalPasos = pasos.size,
            tituloEtapa = tituloDeEtapa(etapa)
        ).conPaso(pasos.firstOrNull(), 0)
    )
    val estado: StateFlow<SesionUiState> = _estado.asStateFlow()

    /** Se emite una vez por elección admitida: la háptica se conecta aquí (K6). */
    private val _eleccionesAdmitidas = MutableStateFlow(0)
    val eleccionesAdmitidas: StateFlow<Int> = _eleccionesAdmitidas.asStateFlow()

    private val _etapaFinalizada = MutableStateFlow(false)
    val etapaFinalizada: StateFlow<Boolean> = _etapaFinalizada.asStateFlow()

    /** Revela el equivalente turco en Menos apoyo; sólo cambia el apoyo visible. */
    fun verAyuda() {
        _estado.update { actual ->
            if (actual.apoyoTurcoOculto) actual.copy(ayudaRevelada = true) else actual
        }
    }

    /**
     * Admite una única elección por paso. Un identificador desconocido,
     * repetido o de otro paso se ignora sin efectos.
     */
    fun elegir(opcionId: String) {
        val actual = _estado.value

        if (actual.fase != FaseSesion.PRACTICA) return
        if (actual.eleccionAdmitida) return
        if (actual.opciones.none { it.id == opcionId }) return

        val opcion = actual.opciones.first { it.id == opcionId }
        val respuestaEsperada = pasos
            .firstOrNull { it.id == actual.idPaso }
            ?.respuestaEsperadaId

        _estado.update {
            it.copy(
                eleccionAdmitida = true,
                opcionElegidaId = opcion.id,
                acierto = opcion.id == respuestaEsperada,
                feedbackVisible = true,
                continuarVisible = true
            )
        }
        // Cuenta como una única señal para la confirmación háptica.
        _eleccionesAdmitidas.update { it + 1 }
    }

    /**
     * Avanza al siguiente paso o solicita la finalización de la etapa. No
     * avanza si no hay feedback previo ni de forma automática.
     */
    fun continuar() {
        val actual = _estado.value
        if (actual.guardandoFinalizacion || _etapaFinalizada.value) return

        if (actual.fase == FaseSesion.PRACTICA && !actual.feedbackVisible) return

        val indiceActual = pasos.indexOfFirst { it.id == actual.idPaso }
        val siguiente = indiceActual + 1

        if (siguiente < pasos.size) {
            _estado.update { it.conPaso(pasos[siguiente], siguiente) }
            return
        }

        finalizarEtapa()
    }

    private fun finalizarEtapa() {
        _estado.update { it.copy(guardandoFinalizacion = true, errorOperacion = null) }

        viewModelScope.launch {
            when (val resultado = repositorio.finalizarEtapa(etapa)) {
                is Resultado.Exito -> {
                    // La etapa no se anuncia completada antes de confirmar el guardado.
                    _estado.update {
                        it.copy(guardandoFinalizacion = false, etapaCompletada = true)
                    }
                    _etapaFinalizada.value = true
                }
                is Resultado.Fallo -> {
                    _estado.update {
                        it.copy(
                            guardandoFinalizacion = false,
                            errorOperacion = resultado.mensaje
                        )
                    }
                }
            }
        }
    }

    /** Consumido por la interfaz tras navegar por la etapa completada. */
    fun finalizacionConsumida() {
        _etapaFinalizada.value = false
    }

    // --- Paso interno ---

    private data class Paso(
        val id: String,
        val fase: FaseSesion,
        val unidad: Unidad,
        val apoyoTurcoOculto: Boolean,
        val respuestaEsperadaId: String?
    )

    private fun SesionUiState.conPaso(paso: Paso?, indice: Int): SesionUiState {
        if (paso == null) return this
        return copy(
            fase = paso.fase,
            idPaso = paso.id,
            posicionPaso = indice + 1,
            tituloEtapa = tituloDeEtapa(etapa),
            palabraEspanol = paso.unidad.textoEspanol,
            apoyoTurco = paso.unidad.apoyoTurco,
            apoyoTurcoOculto = paso.apoyoTurcoOculto,
            ayudaRevelada = false,
            opciones = if (paso.fase == FaseSesion.PRACTICA) paso.unidad.opciones else emptyList(),
            eleccionAdmitida = false,
            opcionElegidaId = null,
            acierto = null,
            feedbackVisible = false,
            continuarVisible = paso.fase == FaseSesion.PRESENTACION,
            guardandoFinalizacion = false,
            etapaCompletada = false,
            errorOperacion = null
        )
    }

    private fun construirPasos(etapa: Etapa, unidades: List<Unidad>): List<Paso> = when (etapa) {
        Etapa.DESCUBRIR -> unidades.map { unidad ->
            Paso(
                id = "${etapa.id}-${unidad.id}",
                fase = FaseSesion.PRESENTACION,
                unidad = unidad,
                apoyoTurcoOculto = false,
                respuestaEsperadaId = null
            )
        }
        Etapa.CON_APOYO -> unidades.map { unidad ->
            Paso(
                id = "${etapa.id}-${unidad.id}",
                fase = FaseSesion.PRACTICA,
                unidad = unidad,
                apoyoTurcoOculto = false,
                respuestaEsperadaId = unidad.respuestaEsperadaId
            )
        }
        Etapa.MENOS_APOYO -> unidades.map { unidad ->
            Paso(
                id = "${etapa.id}-${unidad.id}",
                fase = FaseSesion.PRACTICA,
                unidad = unidad,
                apoyoTurcoOculto = true,
                respuestaEsperadaId = unidad.respuestaEsperadaId
            )
        }
    }

    private fun tituloDeEtapa(etapa: Etapa): String = when (etapa) {
        Etapa.DESCUBRIR -> "Descubrir"
        Etapa.CON_APOYO -> "Con apoyo"
        Etapa.MENOS_APOYO -> "Menos apoyo"
    }

    companion object {
        /** Identificadores estables de las opciones sintéticas de prueba. */
        fun opcionSintetica(id: String, texto: String) = OpcionUnidad(id, texto)
    }
}
