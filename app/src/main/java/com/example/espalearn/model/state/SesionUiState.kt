package com.example.espalearn.model.state

import com.example.espalearn.model.Etapa
import com.example.espalearn.model.OpcionUnidad

/**
 * Fases de una sesión (K3): primero Descubrir sin pregunta y después
 * práctica con una sola elección admitida por paso.
 */
enum class FaseSesion {
    PRESENTACION,
    PRACTICA
}

/**
 * Estado de una sesión de tarjetas (K3/K4).
 *
 * [idSesion] e [idPaso] son identidad del evento: la lógica debe ignorar
 * elecciones duplicadas o tardías, incluso si la interfaz se deshabilita.
 * Una elección admitida bloquea nuevas respuestas y muestra el feedback;
 * Continuar avanza únicamente por acción manual, sin temporizador ni
 * avance automático. La finalización sólo se anuncia tras confirmar el
 * guardado de la etapa.
 */
data class SesionUiState(
    val idSesion: String = "",
    val etapa: Etapa = Etapa.DESCUBRIR,
    val fase: FaseSesion = FaseSesion.PRESENTACION,
    val idPaso: String = "",
    val posicionPaso: Int = 0,
    val totalPasos: Int = 0,
    val tituloEtapa: String = "",
    val palabraEspanol: String? = null,
    val apoyoTurco: String? = null,
    val apoyoTurcoOculto: Boolean = false,
    val ayudaRevelada: Boolean = false,
    val opciones: List<OpcionUnidad> = emptyList(),
    val eleccionAdmitida: Boolean = false,
    val opcionElegidaId: String? = null,
    val acierto: Boolean? = null,
    val feedbackVisible: Boolean = false,
    val continuarVisible: Boolean = false,
    val guardandoFinalizacion: Boolean = false,
    val etapaCompletada: Boolean = false,
    val errorOperacion: String? = null,
    val recursoIlustracion: String? = null,
    val moduloCompletadoPorPrimeraVez: Boolean = false
)
