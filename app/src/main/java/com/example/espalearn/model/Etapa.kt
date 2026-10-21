package com.example.espalearn.model

/**
 * Tres etapas con acceso secuencial: Descubrir → Con apoyo → Menos apoyo.
 * Los identificadores son estables porque se persisten.
 */
enum class Etapa(val id: String) {

    DESCUBRIR("descubrir"),

    CON_APOYO("con_apoyo"),

    MENOS_APOYO("menos_apoyo");

    /** Etapa previa en el orden secuencial, o null si es la primera. */
    val anterior: Etapa?
        get() = entries.getOrNull(ordinal - 1)

    /** Etapa siguiente en el orden secuencial, o null si es la última. */
    val siguiente: Etapa?
        get() = entries.getOrNull(ordinal + 1)

    companion object {
        fun desdeId(id: String): Etapa? = entries.firstOrNull { it.id == id }
    }
}
