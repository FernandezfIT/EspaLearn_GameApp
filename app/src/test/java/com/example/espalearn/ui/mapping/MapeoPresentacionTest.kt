package com.example.espalearn.ui.mapping

import com.example.espalearn.model.Etapa
import com.example.espalearn.model.OpcionUnidad
import com.example.espalearn.model.Progreso
import com.example.espalearn.model.state.FaseSesion
import com.example.espalearn.model.state.SesionUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas del mapeo de estados de B a los tipos de presentación de A.
 * Comprueban que la interfaz recibe la información acordada en K2–K4 sin
 * depender de los tipos internos.
 */
class MapeoPresentacionTest {

    @Test
    fun `el mapa expone las tres etapas en orden`() {
        val etapas = MapeoPresentacion.etapasParaMapa(
            progreso = Progreso(),
            etapaActiva = Etapa.DESCUBRIR
        )

        assertEquals(3, etapas.size)
        assertEquals(listOf("descubrir", "con_apoyo", "menos_apoyo"), etapas.map { it.id })
    }

    @Test
    fun `el mapa bloquea las etapas posteriores sin completar la previa`() {
        val etapas = MapeoPresentacion.etapasParaMapa(
            progreso = Progreso(),
            etapaActiva = Etapa.DESCUBRIR
        )

        assertTrue(etapas[0].habilitada)
        assertFalse(etapas[1].habilitada)
        assertFalse(etapas[2].habilitada)
    }

    @Test
    fun `una etapa completada se marca como repetible`() {
        val etapas = MapeoPresentacion.etapasParaMapa(
            progreso = Progreso(etapasCompletadas = setOf(Etapa.DESCUBRIR)),
            etapaActiva = Etapa.CON_APOYO
        )

        assertEquals("Completada", etapas[0].estado)
        assertEquals("Repetir", etapas[0].accion)
        assertTrue(etapas[1].habilitada)
    }

    @Test
    fun `descubrir no ofrece opciones ni feedback`() {
        val presentacion = MapeoPresentacion.sesionParaPresentacion(
            SesionUiState(
                idSesion = "s",
                etapa = Etapa.DESCUBRIR,
                fase = FaseSesion.PRESENTACION,
                idPaso = "p",
                posicionPaso = 1,
                totalPasos = 4,
                continuarVisible = true
            )
        )

        assertTrue(presentacion.opciones.isEmpty())
        assertNull(presentacion.feedback)
        assertFalse(presentacion.eleccionHabilitada)
        assertTrue(presentacion.mostrarContinuar)
    }

    @Test
    fun `la practica ofrece dos opciones y admite eleccion`() {
        val presentacion = MapeoPresentacion.sesionParaPresentacion(
            SesionUiState(
                idSesion = "s",
                etapa = Etapa.CON_APOYO,
                fase = FaseSesion.PRACTICA,
                idPaso = "p",
                posicionPaso = 1,
                totalPasos = 4,
                opciones = listOf(
                    OpcionUnidad("hola", "Hola"),
                    OpcionUnidad("adios", "Adiós")
                )
            )
        )

        assertEquals(2, presentacion.opciones.size)
        assertTrue(presentacion.eleccionHabilitada)
    }

    @Test
    fun `menos apoyo oculta el turco hasta revelar la ayuda`() {
        val estado = SesionUiState(
            idSesion = "s",
            etapa = Etapa.MENOS_APOYO,
            fase = FaseSesion.PRACTICA,
            idPaso = "p",
            posicionPaso = 1,
            totalPasos = 4,
            apoyoTurco = "Merhaba",
            apoyoTurcoOculto = true,
            opciones = listOf(OpcionUnidad("hola", "Hola"), OpcionUnidad("adios", "Adiós"))
        )

        val oculto = MapeoPresentacion.sesionParaPresentacion(estado)
        assertNull(oculto.apoyoTurco)
        assertTrue(oculto.mostrarVerAyuda)

        val revelado = MapeoPresentacion.sesionParaPresentacion(estado.copy(ayudaRevelada = true))
        assertEquals("Merhaba", revelado.apoyoTurco)
    }

    @Test
    fun `la eleccion admitida bloquea nuevas respuestas`() {
        val presentacion = MapeoPresentacion.sesionParaPresentacion(
            SesionUiState(
                idSesion = "s",
                etapa = Etapa.CON_APOYO,
                fase = FaseSesion.PRACTICA,
                idPaso = "p",
                posicionPaso = 1,
                totalPasos = 4,
                opciones = listOf(OpcionUnidad("hola", "Hola"), OpcionUnidad("adios", "Adiós")),
                eleccionAdmitida = true,
                feedbackVisible = true,
                continuarVisible = true
            )
        )

        assertFalse(presentacion.eleccionHabilitada)
        assertTrue(presentacion.feedback != null)
    }

    @Test
    fun `descubrir muestra la palabra y su apoyo sin elegir`() {
        val presentacion = MapeoPresentacion.sesionParaPresentacion(
            SesionUiState(
                idSesion = "s",
                etapa = Etapa.DESCUBRIR,
                fase = FaseSesion.PRESENTACION,
                palabraEspanol = "Hola",
                apoyoTurco = "Merhaba"
            )
        )

        assertEquals("Hola", presentacion.palabraEspanol)
        assertEquals("Merhaba", presentacion.apoyoTurco)
    }

    @Test
    fun `ambas practicas ocultan la respuesta incluso al pedir ayuda`() {
        listOf(Etapa.CON_APOYO, Etapa.MENOS_APOYO).forEach { etapa ->
            listOf(false, true).forEach { ayudaRevelada ->
                val presentacion = MapeoPresentacion.sesionParaPresentacion(
                    SesionUiState(
                        idSesion = "s",
                        etapa = etapa,
                        fase = FaseSesion.PRACTICA,
                        palabraEspanol = "Hola",
                        apoyoTurco = "Merhaba",
                        apoyoTurcoOculto = etapa == Etapa.MENOS_APOYO,
                        ayudaRevelada = ayudaRevelada
                    )
                )

                assertNull(presentacion.palabraEspanol)
            }
        }
    }

    @Test
    fun `ambas practicas revelan la respuesta tras acierto o error`() {
        listOf(Etapa.CON_APOYO, Etapa.MENOS_APOYO).forEach { etapa ->
            listOf(false, true).forEach { acierto ->
                val presentacion = MapeoPresentacion.sesionParaPresentacion(
                    SesionUiState(
                        idSesion = "s",
                        etapa = etapa,
                        fase = FaseSesion.PRACTICA,
                        palabraEspanol = "Hola",
                        eleccionAdmitida = true,
                        feedbackVisible = true,
                        acierto = acierto,
                        continuarVisible = true
                    )
                )

                assertEquals("Hola", presentacion.palabraEspanol)
                assertTrue(presentacion.feedback != null)
                assertFalse(presentacion.eleccionHabilitada)
                assertTrue(presentacion.mostrarContinuar)
            }
        }
    }

    @Test
    fun `el resumen muestra el logro solo con el modulo completo`() {
        val parcial = MapeoPresentacion.resumenParaPresentacion(
            nick = "Deniz",
            progreso = Progreso(etapasCompletadas = setOf(Etapa.DESCUBRIR)),
            guardandoMarco = false,
            errorMarco = null
        )
        assertNull(parcial.logro)

        val completo = MapeoPresentacion.resumenParaPresentacion(
            nick = "Deniz",
            progreso = Progreso(etapasCompletadas = Etapa.entries.toSet(), marcoAplicado = true),
            guardandoMarco = false,
            errorMarco = null
        )
        assertTrue(completo.logro != null)
        assertEquals(true, completo.logro?.marcoAplicado)
    }
}
