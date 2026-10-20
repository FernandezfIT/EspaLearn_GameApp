package com.example.espalearn.model

/**
 * Catálogo de contenido del módulo de EV2: cuatro unidades reutilizadas en
 * las tres etapas (12 pasos, 8 con elección).
 *
 * Los textos en español y turco y las parejas de opciones son la propuesta
 * acordada. La [Unidad.recursoIlustracion] apunta a una ilustración que
 * TODAVÍA NO existe en el proyecto: mientras no se incorpore material
 * autorizado, la interfaz dibuja un marcador de posición. Ver
 * `docs/contenido-y-licencias.md` antes de sustituirlo.
 *
 * ADVERTENCIA: este catálogo no está validado lingüísticamente ni
 * pedagógicamente. No presentarlo como contenido revisado.
 */
object ContenidoUnidades {

    /** Saludos: escenas ilustradas con dos unidades. */
    val HOLA = Unidad(
        id = "hola",
        tema = TemaUnidad.SALUDOS,
        textoEspanol = "Hola",
        apoyoTurco = "Merhaba",
        recursoIlustracion = "ilustracion_hola",
        opciones = listOf(
            OpcionUnidad(id = "hola", textoEspanol = "Hola"),
            OpcionUnidad(id = "buenas_noches", textoEspanol = "Buenas noches")
        ),
        respuestaEsperadaId = "hola"
    )

    val ADIOS = Unidad(
        id = "adios",
        tema = TemaUnidad.SALUDOS,
        textoEspanol = "Adiós",
        apoyoTurco = "Hoşça kal",
        recursoIlustracion = "ilustracion_adios",
        opciones = listOf(
            OpcionUnidad(id = "adios", textoEspanol = "Adiós"),
            OpcionUnidad(id = "gracias", textoEspanol = "Gracias")
        ),
        respuestaEsperadaId = "adios"
    )

    /** Alimentos: dos unidades. */
    val PAN = Unidad(
        id = "pan",
        tema = TemaUnidad.ALIMENTOS,
        textoEspanol = "Pan",
        apoyoTurco = "Ekmek",
        recursoIlustracion = "ilustracion_pan",
        opciones = listOf(
            OpcionUnidad(id = "pan", textoEspanol = "Pan"),
            OpcionUnidad(id = "agua", textoEspanol = "Agua")
        ),
        respuestaEsperadaId = "pan"
    )

    val MANZANA = Unidad(
        id = "manzana",
        tema = TemaUnidad.ALIMENTOS,
        textoEspanol = "Manzana",
        apoyoTurco = "Elma",
        recursoIlustracion = "ilustracion_manzana",
        opciones = listOf(
            OpcionUnidad(id = "manzana", textoEspanol = "Manzana"),
            OpcionUnidad(id = "platano", textoEspanol = "Plátano")
        ),
        respuestaEsperadaId = "manzana"
    )

    /** Las cuatro unidades, en el orden en que se recorren en Descubrir. */
    val todas: List<Unidad> = listOf(HOLA, ADIOS, PAN, MANZANA)

    /** Sin contenido real cargado, la referencia es un marcador vacío. */
    fun unidadPorId(id: String): Unidad? = todas.firstOrNull { it.id == id }
}
