package com.example.espalearn.preview


import com.example.espalearn.ui.components.EtapaMapaUi
import com.example.espalearn.ui.screens.resumen.ResumenPresentacion
import com.example.espalearn.ui.screens.sesion.OpcionRespuestaUi
import com.example.espalearn.ui.screens.sesion.SesionPresentacion

internal val navEtapasDemo = listOf(
    EtapaMapaUi(
        id = "demo_descubrir",
        titulo = "1. Descubrir",
        descripcion = "Conoce las palabras.",
        estado = "Completada",
        accion = "Repetir",
        habilitada = true
    ),
    EtapaMapaUi(
        id = "demo_con_apoyo",
        titulo = "2. Con apoyo",
        descripcion = "Practica con ayuda.",
        estado = "Disponible",
        accion = "Comenzar",
        habilitada = true
    ),
    EtapaMapaUi(
        id = "demo_menos_apoyo",
        titulo = "3. Menos apoyo",
        descripcion = "Consulta la ayuda cuando la necesites.",
        estado = "Bloqueada",
        accion = "Bloqueada",
        habilitada = false
    )
)

internal val navDescubrirDemo = SesionPresentacion(
    clavePaso = "demo_descubrir_1",
    tituloEtapa = "Descubrir",
    posicionTexto = "Paso de prueba 1 de 4",
    palabraEspanol = "Hola",
    apoyoTurco = "[Apoyo turco de prueba]",
    mostrarContinuar = true
)

internal val navPracticaDemo = SesionPresentacion(
    clavePaso = "demo_con_apoyo_1",
    tituloEtapa = "Con apoyo",
    posicionTexto = "Paso de prueba 1 de 4",
    apoyoTurco = "[Apoyo turco de prueba]",
    opciones = listOf(
        OpcionRespuestaUi("demo_a", "Opción A"),
        OpcionRespuestaUi("demo_b", "Opción B")
    ),
    eleccionHabilitada = true
)

internal val navResumenDemo = ResumenPresentacion(
    titulo = "Resumen de prueba",
    mensaje = "Este resultado está prefijado; no se completó una etapa.",
    progresoTexto = "1 de 3 etapas completadas — dato de prueba",
    textoAccionPrincipal = "Ver Con apoyo de prueba"
)