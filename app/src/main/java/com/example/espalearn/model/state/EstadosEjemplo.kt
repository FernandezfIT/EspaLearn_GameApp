package com.example.espalearn.model.state

import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import com.example.espalearn.model.TemaUnidad
import com.example.espalearn.model.Unidad
import com.example.espalearn.model.OpcionUnidad

/**
 * Estados de ejemplo para la interfaz: válidos, de error y de guardado.
 * Son datos sintéticos con fines de presentación y pruebas; no representan
 * persistencia ni sustituyen a los estados reales del repositorio.
 */
object EstadosEjemplo {

    val perfilDeEjemplo = Perfil(
        nick = "Deniz123",
        edad = 8,
        avatar = AvatarRef.Predeterminado
    )

    val progresoSinCompletar = Progreso(etapaActiva = Etapa.CON_APOYO)

    val progresoModuloCompletado = Progreso(
        etapasCompletadas = Etapa.entries.toSet(),
        etapaActiva = Etapa.MENOS_APOYO,
        marcoAplicado = true
    )

    val estadoSinPerfil = AppUiState.ListoSinPerfil

    val estadoConPerfil = AppUiState.ListoConPerfil(perfilDeEjemplo, progresoSinCompletar)

    val estadoErrorLectura = AppUiState.ErrorLectura("No se pudo leer el perfil guardado.")

    val formularioValido = PerfilUiState(
        nick = "Deniz123",
        edadTexto = "8",
        validacionActivada = true
    )

    val formularioConErrores = PerfilUiState(
        nick = "De",
        edadTexto = "12",
        validacionActivada = true,
        errores = PerfilErrores(
            nick = "El nick debe tener entre 3 y 20 caracteres.",
            edad = "La edad simulada debe estar entre 7 y 10."
        )
    )

    val formularioGuardando = formularioValido.copy(guardando = true)

    val unidadDeEjemplo = Unidad(
        id = "hola",
        tema = TemaUnidad.SALUDOS,
        textoEspanol = "Hola",
        apoyoTurco = "Merhaba",
        recursoIlustracion = "ilustracion_hola",
        opciones = listOf(
            OpcionUnidad(id = "hola", textoEspanol = "Hola"),
            OpcionUnidad(id = "adios", textoEspanol = "Adiós")
        ),
        respuestaEsperadaId = "hola"
    )

    val sesionDescubriendo = SesionUiState(
        idSesion = "ejemplo",
        etapa = Etapa.DESCUBRIR,
        fase = FaseSesion.PRESENTACION,
        idPaso = "descubrir-hola",
        posicionPaso = 1,
        totalPasos = 4,
        tituloEtapa = "Descubrir",
        palabraEspanol = "Hola",
        apoyoTurco = "Merhaba",
        continuarVisible = true
    )

    val sesionConFeedback = SesionUiState(
        idSesion = "ejemplo",
        etapa = Etapa.CON_APOYO,
        fase = FaseSesion.PRACTICA,
        idPaso = "con_apoyo-hola",
        posicionPaso = 1,
        totalPasos = 4,
        tituloEtapa = "Con apoyo",
        palabraEspanol = "Hola",
        apoyoTurco = "Merhaba",
        apoyoTurcoOculto = false,
        opciones = unidadDeEjemplo.opciones,
        eleccionAdmitida = false,
        opcionElegidaId = "hola",
        acierto = true,
        feedbackVisible = true,
        continuarVisible = true
    )

    val sesionGuardandoFinalizacion = sesionConFeedback.copy(guardandoFinalizacion = true)
}
