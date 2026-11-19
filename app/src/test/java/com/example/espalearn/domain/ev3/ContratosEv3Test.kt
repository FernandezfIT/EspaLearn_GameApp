package com.example.espalearn.domain.ev3

import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Progreso
import org.junit.Assert.*
import org.junit.Test

/** Fixtures sintéticos: no certifican traducciones, assets ni integración real. */
class ContratosEv3Test {
    private val ids = listOf("pan", "manzana", "pera")
    private val palabras = ids.map { id ->
        PalabraCatalogo(id, "prueba-3-v1", id, "apoyo-sintetico", "Texto de prueba", "fixture_$id",
            listOf(OpcionCatalogo("${id}_a", id), OpcionCatalogo("${id}_b", "Otra")), "${id}_a")
    }
    private val recorrido = RecorridoEv3("recorrido-prueba", "prueba-3-v1", TipoRecorrido.NORMAL,
        EstadoRecorrido.ACTIVO, ids, emptyList())

    private fun cierre(etapa: Etapa) = CierreEtapaEv3("solicitud-prueba", "prueba-3-v1", etapa,
        ids.map { ResultadoPalabra(it, if (etapa == Etapa.DESCUBRIR) null else "${it}_a", false) })

    @Test fun `descubrir admite tres presentaciones sin aciertos`() {
        cierre(Etapa.DESCUBRIR).validarContra(recorrido, palabras)
        assertEquals(Etapa.DESCUBRIR, recorrido.etapaPendiente)
    }

    @Test fun `practica admite una opcion por palabra en la etapa pendiente`() {
        cierre(Etapa.CON_APOYO).validarContra(
            recorrido.copy(etapasConfirmadas = listOf(Etapa.DESCUBRIR)), palabras)
    }

    @Test fun `rechaza palabras repetidas y lote incompleto`() {
        assertThrows(IllegalArgumentException::class.java) { recorrido.copy(palabraIds = listOf("pan", "pan", "pera")) }
        assertThrows(IllegalArgumentException::class.java) { recorrido.copy(palabraIds = ids.take(2)) }
    }

    @Test fun `rechaza etapas desordenadas o repetidas`() {
        assertThrows(IllegalArgumentException::class.java) { recorrido.copy(etapasConfirmadas = listOf(Etapa.CON_APOYO)) }
        assertThrows(IllegalArgumentException::class.java) {
            recorrido.copy(etapasConfirmadas = listOf(Etapa.DESCUBRIR, Etapa.DESCUBRIR))
        }
    }

    @Test fun `completado exige tres etapas y no tiene pendiente`() {
        assertThrows(IllegalArgumentException::class.java) { recorrido.copy(estado = EstadoRecorrido.COMPLETADO) }
        val completo = recorrido.copy(estado = EstadoRecorrido.COMPLETADO, etapasConfirmadas = Etapa.entries)
        assertNull(completo.etapaPendiente)
        assertThrows(IllegalArgumentException::class.java) { completo.copy(estado = EstadoRecorrido.ACTIVO) }
    }

    @Test fun `descubrir rechaza respuestas y ayuda ficticias`() {
        assertThrows(IllegalArgumentException::class.java) {
            cierre(Etapa.DESCUBRIR).copy(resultados = ids.map { ResultadoPalabra(it, "${it}_a", false) })
        }
        assertThrows(IllegalArgumentException::class.java) {
            cierre(Etapa.DESCUBRIR).copy(resultados = ids.map { ResultadoPalabra(it, null, true) })
        }
    }

    @Test fun `practica rechaza respuestas ausentes`() {
        assertThrows(IllegalArgumentException::class.java) {
            cierre(Etapa.CON_APOYO).copy(resultados = cierre(Etapa.DESCUBRIR).resultados)
        }
    }

    @Test fun `rechaza cierre de otro lote o version`() {
        assertThrows(IllegalArgumentException::class.java) {
            cierre(Etapa.DESCUBRIR).copy(resultados = cierre(Etapa.DESCUBRIR).resultados.reversed())
                .validarContra(recorrido, palabras)
        }
        assertThrows(IllegalArgumentException::class.java) {
            cierre(Etapa.DESCUBRIR).copy(versionCatalogo = "otra").validarContra(recorrido, palabras)
        }
    }

    @Test fun `rechaza saltar etapa u opcion desconocida`() {
        assertThrows(IllegalArgumentException::class.java) { cierre(Etapa.CON_APOYO).validarContra(recorrido, palabras) }
        assertThrows(IllegalArgumentException::class.java) {
            cierre(Etapa.CON_APOYO).copy(resultados = ids.map { ResultadoPalabra(it, "inexistente", false) })
                .validarContra(recorrido.copy(etapasConfirmadas = listOf(Etapa.DESCUBRIR)), palabras)
        }
    }

    @Test fun `catalogo exige dos opciones distintas y respuesta existente`() {
        assertThrows(IllegalArgumentException::class.java) { palabras.first().copy(respuestaEsperadaId = "no") }
        assertThrows(IllegalArgumentException::class.java) { palabras.first().copy(opciones = listOf(palabras.first().opciones.first())) }
        assertThrows(IllegalArgumentException::class.java) { palabras.first().copy(opciones = List(2) { OpcionCatalogo("a", "A") }) }
    }

    @Test fun `repaso solo disponible con cobertura total`() {
        assertFalse(ProgresoEv3(3, 150, true).repasoDisponible)
        assertTrue(ProgresoEv3(150, 150, true).repasoDisponible)
        assertThrows(IllegalArgumentException::class.java) { ProgresoEv3(153, 150, true) }
        assertThrows(IllegalArgumentException::class.java) { ProgresoEv3(1, 150, false) }
    }

    @Test fun `feedback distingue generado de alternativa y pendiente`() {
        FeedbackEtapaEv3(EstadoFeedback.DISPONIBLE, OrigenFeedback.GROQ, "Frase de prueba", "v1")
        FeedbackEtapaEv3(EstadoFeedback.ALTERNATIVO, OrigenFeedback.LOCAL, "Texto revisado", "v1")
        FeedbackEtapaEv3(EstadoFeedback.GENERANDO, null, null, "v1")
        assertThrows(IllegalArgumentException::class.java) {
            FeedbackEtapaEv3(EstadoFeedback.ALTERNATIVO, OrigenFeedback.GROQ, "No es IA", "v1")
        }
        assertThrows(IllegalArgumentException::class.java) {
            FeedbackEtapaEv3(EstadoFeedback.PENDIENTE, null, "No terminado", "v1")
        }
        assertThrows(IllegalArgumentException::class.java) {
            FeedbackEtapaEv3(EstadoFeedback.DISPONIBLE, OrigenFeedback.GROQ, "x".repeat(241), "v1")
        }
    }

    @Test fun `conserva marco legado sin inventar avance EV3`() {
        val legado = RecompensaLegada.desdeEv2(Progreso(Etapa.entries.toSet(), Etapa.MENOS_APOYO, true))
        val nuevo = ProgresoEv3(0, 150, false)
        assertTrue(legado.desbloqueada)
        assertTrue(legado.aplicada)
        assertTrue(legado.puedeUsarMarco(nuevo))
        assertEquals(0, nuevo.completadasUnicas)
        assertFalse(nuevo.primerRecorridoCompletado)
    }

    @Test fun `recompensa obtenida pero no aplicada sigue disponible`() {
        val legado = RecompensaLegada.desdeEv2(Progreso(etapasCompletadas = Etapa.entries.toSet()))
        assertTrue(legado.desbloqueada)
        assertFalse(legado.aplicada)
    }

    @Test fun `sin recompensa previa espera primer recorrido EV3`() {
        val legado = RecompensaLegada.desdeEv2(Progreso())
        assertFalse(legado.puedeUsarMarco(ProgresoEv3(0, 150, false)))
        assertTrue(legado.puedeUsarMarco(ProgresoEv3(3, 150, true)))
    }
}
