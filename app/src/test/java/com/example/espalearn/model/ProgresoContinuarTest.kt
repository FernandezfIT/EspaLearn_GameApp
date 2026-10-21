package com.example.espalearn.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgresoContinuarTest {
    @Test
    fun `sin avance continuar abre Descubrir`() {
        assertEquals(Etapa.DESCUBRIR, Progreso().etapaParaContinuar)
    }

    @Test
    fun `tras Descubrir continuar abre Con apoyo aunque se este repitiendo otra`() {
        val progreso = Progreso(
            etapasCompletadas = setOf(Etapa.DESCUBRIR),
            etapaActiva = Etapa.DESCUBRIR
        )
        assertEquals(Etapa.CON_APOYO, progreso.etapaParaContinuar)
    }

    @Test
    fun `tras Con apoyo continuar abre Menos apoyo`() {
        val progreso = Progreso(
            etapasCompletadas = setOf(Etapa.DESCUBRIR, Etapa.CON_APOYO)
        )
        assertEquals(Etapa.MENOS_APOYO, progreso.etapaParaContinuar)
    }

    @Test
    fun `con modulo completo repasar empieza en Descubrir sin borrar logros ni marco`() {
        val progreso = Progreso(
            etapasCompletadas = Etapa.entries.toSet(),
            etapaActiva = Etapa.MENOS_APOYO,
            marcoAplicado = true
        )
        assertEquals(Etapa.DESCUBRIR, progreso.etapaParaContinuar)
        assertTrue(progreso.moduloCompletado)
        assertTrue(progreso.marcoAplicado)
        assertEquals(Etapa.entries.toSet(), progreso.etapasCompletadas)
    }
}
