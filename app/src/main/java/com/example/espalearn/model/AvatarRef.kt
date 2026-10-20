package com.example.espalearn.model

/**
 * Origen del avatar. Distingue el avatar geométrico predeterminado de un
 * archivo privado confirmado; sólo se persiste el nombre del archivo.
 */
sealed interface AvatarRef {

    data object Predeterminado : AvatarRef

    data class ArchivoPrivado(val nombreArchivo: String) : AvatarRef
}
