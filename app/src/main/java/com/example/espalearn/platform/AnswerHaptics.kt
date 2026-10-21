package com.example.espalearn.platform

import android.view.HapticFeedbackConstants
import android.view.View

/**
 * Confirmación háptica de una elección admitida (K6).
 *
 * Se usa [View.performHapticFeedback] con una constante neutral, sin exigir
 * el permiso VIBRATE y respetando los ajustes del sistema. El pulso es el
 * mismo para acierto y error, y no se emite por Continuar, por eventos
 * duplicados ni al volver a mostrar la pantalla: sólo lo dispara la lógica
 * cuando admite una elección, mediante [notificarEleccionAdmitida].
 *
 * El recorrido debe seguir siendo comprensible sin vibración: la señal es un
 * refuerzo, no el canal principal de feedback.
 */
class AnswerHaptics internal constructor(private val emitirPulso: () -> Unit) {

    constructor(vista: View?) : this({
        if (vista != null && vista.isHapticFeedbackEnabled) {
            vista.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
        Unit
    })

    private var ultimoContador: Int = 0

    /**
     * Se invoca desde la acción de elegir, sólo si el ViewModel incrementó
     * su contador. No se observa/reproduce el contador al montar la pantalla.
     * Cero, duplicados y contadores anteriores no generan otro pulso.
     */
    fun notificarEleccionAdmitida(contadorSenales: Int) {
        if (contadorSenales <= ultimoContador) return
        ultimoContador = contadorSenales
        emitirPulso()
    }
}
