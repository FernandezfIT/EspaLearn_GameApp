package com.example.espalearn.model

/**
 * Temática de una unidad. EV2 sólo usa saludos y alimentos.
 */
enum class TemaUnidad {
    SALUDOS,
    ALIMENTOS
}

/**
 * Alternativa escrita de un paso de práctica, identificada de forma estable.
 */
data class OpcionUnidad(
    val id: String,
    val textoEspanol: String
)

/**
 * Unidad de contenido: cuatro en total (Hola, Adiós, Pan y Manzana).
 *
 * [recursoIlustracion] es una referencia a una ilustración autorizada;
 * no se carga ni se dibuja desde este modelo. Las opciones y la respuesta
 * esperada se usan únicamente en los pasos de práctica.
 */
data class Unidad(
    val id: String,
    val tema: TemaUnidad,
    val textoEspanol: String,
    val apoyoTurco: String,
    val recursoIlustracion: String,
    val opciones: List<OpcionUnidad>,
    val respuestaEsperadaId: String
)
