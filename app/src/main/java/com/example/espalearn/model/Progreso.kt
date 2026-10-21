package com.example.espalearn.model

/**
 * Progreso persistido del perfil: etapas completadas, etapa activa y
 * preferencia del marco. El logro del módulo se deriva de completar las
 * tres etapas; no existe un sistema de puntos independiente.
 *
 * Completar una etapa significa haber recorrido todos sus pasos, sin nota
 * mínima. Repetir una etapa completada no debe duplicar este estado.
 */
data class Progreso(
    val etapasCompletadas: Set<Etapa> = emptySet(),
    val etapaActiva: Etapa = Etapa.DESCUBRIR,
    val marcoAplicado: Boolean = false
) {

    val moduloCompletado: Boolean
        get() = etapasCompletadas.containsAll(Etapa.entries)

    /** Continuar abre la primera pendiente; repasar empieza en Descubrir sin borrar avance. */
    val etapaParaContinuar: Etapa
        get() = Etapa.entries.firstOrNull { !etapaCompletada(it) } ?: Etapa.DESCUBRIR

    fun etapaCompletada(etapa: Etapa): Boolean = etapa in etapasCompletadas

    /** Acceso secuencial: la primera siempre; las demás requieren la anterior completada. */
    fun etapaDisponible(etapa: Etapa): Boolean {
        val previa = etapa.anterior ?: return true
        return etapaCompletada(previa)
    }
}
