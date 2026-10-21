package com.example.espalearn.ui.mapping

import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Progreso
import com.example.espalearn.model.state.FaseSesion
import com.example.espalearn.model.state.SesionUiState
import com.example.espalearn.ui.components.EtapaMapaUi
import com.example.espalearn.ui.screens.resumen.LogroResumenUi
import com.example.espalearn.ui.screens.resumen.ResumenPresentacion
import com.example.espalearn.ui.screens.sesion.FeedbackUi
import com.example.espalearn.ui.screens.sesion.OpcionRespuestaUi
import com.example.espalearn.ui.screens.sesion.SesionPresentacion

/**
 * Adapta el estado de negocio de la ruta B a los tipos de presentación que
 * ya consumen las pantallas de la ruta A.
 *
 * Los tipos `*Ui` y `*Presentacion` bajo `ui` son proyecciones visuales: no
 * sustituyen a los modelos de B ni se persisten. Este mapeo evita que las
 * pantallas de A dependan de los estados internos y que los ViewModels
 * conozcan los tipos de la interfaz.
 */
object MapeoPresentacion {

    /** Mapa de tres etapas con acceso secuencial y estado de cada una. */
    fun etapasParaMapa(progreso: Progreso, etapaActiva: Etapa): List<EtapaMapaUi> =
        Etapa.entries.map { etapa ->
            val completada = progreso.etapaCompletada(etapa)
            val disponible = progreso.etapaDisponible(etapa)

            EtapaMapaUi(
                id = etapa.id,
                titulo = "${etapa.ordinal + 1}. ${tituloDeEtapa(etapa)}",
                descripcion = descripcionDeEtapa(etapa),
                estado = when {
                    completada -> "Completada"
                    etapa == etapaActiva -> "En curso"
                    disponible -> "Disponible"
                    else -> "Bloqueada"
                },
                accion = when {
                    completada -> "Repetir"
                    disponible -> "Comenzar"
                    else -> "Bloqueada"
                },
                habilitada = disponible
            )
        }

    /** Sesión de tarjetas: Descubrir no pregunta; práctica muestra opciones. */
    fun sesionParaPresentacion(estado: SesionUiState): SesionPresentacion {
        val esPractica = estado.fase == FaseSesion.PRACTICA
        val total = estado.totalPasos.coerceAtLeast(1)

        return SesionPresentacion(
            clavePaso = "${estado.idSesion}-${estado.idPaso}",
            tituloEtapa = estado.tituloEtapa,
            posicionTexto = "Paso ${estado.posicionPaso} de $total",
            // Descubrir presenta la palabra; en práctica sólo se revela con el feedback.
            palabraEspanol = if (!esPractica || estado.feedbackVisible) {
                estado.palabraEspanol
            } else {
                null
            },
            // En Menos apoyo el turco permanece oculto hasta revelar la ayuda.
            apoyoTurco = if (esPractica && estado.apoyoTurcoOculto && !estado.ayudaRevelada) {
                null
            } else {
                estado.apoyoTurco
            },
            mostrarVerAyuda = esPractica && estado.apoyoTurcoOculto && !estado.ayudaRevelada,
            opciones = if (esPractica) {
                estado.opciones.map { OpcionRespuestaUi(id = it.id, texto = it.textoEspanol) }
            } else {
                emptyList()
            },
            eleccionHabilitada = esPractica && !estado.eleccionAdmitida,
            feedback = if (estado.feedbackVisible) {
                feedbackParaPresentacion(estado)
            } else {
                null
            },
            mostrarContinuar = estado.continuarVisible,
            continuarHabilitado = !estado.guardandoFinalizacion,
            textoContinuar = if (estado.guardandoFinalizacion) "Guardando…" else "Continuar",
            errorOperacion = estado.errorOperacion
        )
    }

    /** Resumen de recorrido con logro derivado y control del marco. */
    fun resumenParaPresentacion(
        nick: String,
        progreso: Progreso,
        guardandoMarco: Boolean,
        errorMarco: String?
    ): ResumenPresentacion {
        val completadas = progreso.etapasCompletadas.size
        val total = Etapa.entries.size

        return ResumenPresentacion(
            titulo = "Resumen del recorrido",
            mensaje = if (progreso.moduloCompletado) {
                "Completaste las tres etapas, $nick."
            } else {
                "Revisa tu avance, $nick."
            },
            progresoTexto = "$completadas de $total etapas completadas",
            textoAccionPrincipal = if (progreso.moduloCompletado) {
                "Repasar el recorrido"
            } else {
                "Continuar el recorrido"
            },
            logro = if (progreso.moduloCompletado) {
                LogroResumenUi(
                    titulo = "Logro del módulo",
                    descripcion = "Recorriste Descubrir, Con apoyo y Menos apoyo.",
                    marcoAplicado = progreso.marcoAplicado,
                    guardandoMarco = guardandoMarco,
                    errorMarco = errorMarco
                )
            } else {
                null
            }
        )
    }

    private fun feedbackParaPresentacion(estado: SesionUiState): FeedbackUi =
        if (estado.acierto == true) {
            FeedbackUi(
                titulo = "¡Correcto!",
                mensaje = "Esa es la respuesta de este paso."
            )
        } else {
            FeedbackUi(
                titulo = "Revisa la respuesta",
                mensaje = "Puedes reconocer la respuesta correcta en la tarjeta."
            )
        }

    private fun tituloDeEtapa(etapa: Etapa): String = when (etapa) {
        Etapa.DESCUBRIR -> "Descubrir"
        Etapa.CON_APOYO -> "Con apoyo"
        Etapa.MENOS_APOYO -> "Menos apoyo"
    }

    private fun descripcionDeEtapa(etapa: Etapa): String = when (etapa) {
        Etapa.DESCUBRIR -> "Conoce las palabras con su apoyo."
        Etapa.CON_APOYO -> "Practica con ayuda visible."
        Etapa.MENOS_APOYO -> "Consulta la ayuda cuando la necesites."
    }
}
