package com.example.espalearn.repository

/**
 * Resultado de una operación de datos. Las firmas comunican el fallo en
 * lugar de silenciarlo; el mensaje es comprensible para la interfaz.
 */
sealed interface Resultado<out T> {

    data class Exito<T>(val valor: T) : Resultado<T>

    data class Fallo(val mensaje: String) : Resultado<Nothing>
}
