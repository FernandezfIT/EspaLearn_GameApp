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
class AnswerHaptics(private val vista: View?) {

    private var ultimoContador: Int? = null

    /**
     * Emite un único pulso neutral por elección admitida. [contadorSenales]
     * es el recuento que expone el ViewModel; si no avanzó respecto de la
     * última señal, no se repite por recomposición ni por restauración.
     */
    fun notificarEleccionAdmitida(contadorSenales: Int) {
        if (ultimoContador == contadorSenales) return
        ultimoContador = contadorSenales

        val destino = vista ?: return
        if (!destino.isHapticFeedbackEnabled) return

        destino.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
}
