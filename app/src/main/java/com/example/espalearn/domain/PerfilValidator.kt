package com.example.espalearn.domain

import com.example.espalearn.model.state.PerfilErrores
import java.text.Normalizer

/**
 * Validación centralizada del perfil ficticio (K1). No depende de Compose:
 * la interfaz recibe los errores ya calculados y los representa.
 *
 * Reglas del nick: normalizar con NFC antes de validar, contar puntos de
 * código (no unidades Char), admitir entre 3 y 20 inclusive, y aceptar
 * únicamente letras Unicode o dígitos ASCII 0–9. Sin recorte automático de
 * espacios. Se permite un nick sólo numérico.
 *
 * Edad simulada: entero entre 7 y 10 inclusive, como propuesta pendiente de
 * confirmación docente. No se guarda vacío, no numérico ni fraccionario.
 */
object PerfilValidator {

    const val NICK_MIN = 3
    const val NICK_MAX = 20
    const val EDAD_MIN = 7
    const val EDAD_MAX = 10

    /** Normaliza el nick antes de validar y persistir. */
    fun normalizarNick(nick: String): String = Normalizer.normalize(nick, Normalizer.Form.NFC)

    /**
     * Valida el nick ya normalizado. Devuelve null si es válido o el
     * mensaje de error correspondiente.
     */
    fun validarNick(nickBruto: String): String? {
        val nick = normalizarNick(nickBruto)

        if (nick.isEmpty()) {
            return "Escribe un nick para continuar."
        }

        val puntos = nick.codePointCount(0, nick.length)

        if (puntos < NICK_MIN) {
            return "El nick debe tener al menos $NICK_MIN caracteres."
        }

        if (puntos > NICK_MAX) {
            return "El nick no puede superar los $NICK_MAX caracteres."
        }

        if (!todosCaracteresPermitidos(nick)) {
            return "El nick sólo admite letras y números, sin espacios ni símbolos."
        }

        return null
    }

    /**
     * Valida la edad del borrador, que llega como texto. Devuelve null si
     * es válida o el mensaje de error correspondiente.
     */
    fun validarEdad(edadTexto: String): String? {
        val limpio = edadTexto.trim()

        if (limpio.isEmpty()) {
            return "Indica una edad simulada."
        }

        val valor = limpio.toIntOrNull()
            ?: return "La edad simulada debe ser un número entero."

        if (valor !in EDAD_MIN..EDAD_MAX) {
            return "La edad simulada debe estar entre $EDAD_MIN y $EDAD_MAX."
        }

        return null
    }

    /** Errores de campo completos del formulario. */
    fun validar(nickBruto: String, edadTexto: String): PerfilErrores = PerfilErrores(
        nick = validarNick(nickBruto),
        edad = validarEdad(edadTexto)
    )

    /**
     * Convierte la edad del borrador en el entero confirmado, o null si la
     * entrada no es válida. Sólo debe usarse después de validar.
     */
    fun edadConfirmada(edadTexto: String): Int? {
        if (validarEdad(edadTexto) != null) return null
        return edadTexto.trim().toIntOrNull()
    }

    private fun todosCaracteresPermitidos(nick: String): Boolean {
        var indice = 0
        while (indice < nick.length) {
            val punto = nick.codePointAt(indice)
            val esLetra = Character.isLetter(punto)
            val esDigitoAscii = punto in '0'.code..'9'.code

            if (!esLetra && !esDigitoAscii) {
                return false
            }

            indice += Character.charCount(punto)
        }
        return true
    }
}
