package com.example.espalearn.model

/**
 * Perfil confirmado y guardado.
 *
 * [nick] llega ya normalizado con NFC por la lógica de validación.
 * [edad] es la edad simulada, siempre dentro del rango acordado.
 * [avatar] referencia el archivo privado confirmado o el predeterminado;
 * nunca se persiste un bitmap ni un contexto.
 */
data class Perfil(
    val nick: String,
    val edad: Int,
    val avatar: AvatarRef
)
