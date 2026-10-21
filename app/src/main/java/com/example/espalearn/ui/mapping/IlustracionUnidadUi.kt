package com.example.espalearn.ui.mapping

/** IDs del catálogo hacia dibujos propios; no se deducen de la respuesta o de su posición. */
enum class IlustracionUnidadUi(val recurso: String, val descripcion: String) {
    LLEGADA(
        "ilustracion_hola",
        "Una persona llega a una casa y levanta la mano; una flecha apunta hacia la entrada."
    ),
    DESPEDIDA(
        "ilustracion_adios",
        "Una persona se aleja de una casa y levanta la mano; una flecha apunta hacia fuera."
    ),
    PIEZA_HORNEADA(
        "ilustracion_pan",
        "Una pieza horneada alargada, dorada y con cortes en la corteza."
    ),
    FRUTA_ROJA(
        "ilustracion_manzana",
        "Una fruta redonda roja, con tallo y una hoja verde."
    );

    companion object {
        fun desdeRecurso(recurso: String?): IlustracionUnidadUi? =
            entries.firstOrNull { it.recurso == recurso }
    }
}
