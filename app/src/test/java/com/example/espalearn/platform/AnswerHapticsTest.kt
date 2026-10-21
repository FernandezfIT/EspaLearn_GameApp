package com.example.espalearn.platform

import com.example.espalearn.model.ContenidoUnidades
import com.example.espalearn.model.Etapa
import com.example.espalearn.repository.RepositorioFalso
import com.example.espalearn.viewmodel.SesionViewModel
import org.junit.Assert.assertEquals
import org.junit.Test

/** Comprueba señales lógicas; no acredita vibración física ni ajustes de Android. */
class AnswerHapticsTest {

    @Test
    fun `el contador inicial no genera pulso`() {
        var pulsos = 0
        val haptica = AnswerHaptics { pulsos++ }

        haptica.notificarEleccionAdmitida(0)

        assertEquals(0, pulsos)
    }

    @Test
    fun `cada contador nuevo genera un pulso sin duplicados ni retrocesos`() {
        var pulsos = 0
        val haptica = AnswerHaptics { pulsos++ }

        listOf(1, 1, 2, 1, 0, 2, 3).forEach(haptica::notificarEleccionAdmitida)

        assertEquals(3, pulsos)
    }

    @Test
    fun `acierto y error generan el mismo pulso y continuar no genera otro`() {
        listOf(true, false).forEach { acertar ->
            val vm = SesionViewModel(
                RepositorioFalso(), Etapa.CON_APOYO, ContenidoUnidades.todas
            )
            var pulsos = 0
            val haptica = AnswerHaptics { pulsos++ }
            val unidad = ContenidoUnidades.todas.first()
            val opcion = unidad.opciones.first {
                (it.id == unidad.respuestaEsperadaId) == acertar
            }

            elegir(vm, haptica, opcion.id)
            elegir(vm, haptica, opcion.id)
            elegir(vm, haptica, "inexistente")
            vm.continuar()

            assertEquals(1, pulsos)
            assertEquals(1, vm.eleccionesAdmitidas.value)
        }
    }

    @Test
    fun `recrear el adaptador no reproduce una eleccion anterior`() {
        val vm = SesionViewModel(
            RepositorioFalso(), Etapa.CON_APOYO, ContenidoUnidades.todas
        )
        var pulsos = 0
        val primeraHaptica = AnswerHaptics { pulsos++ }
        elegir(vm, primeraHaptica, ContenidoUnidades.todas.first().respuestaEsperadaId)

        val hapticaRecreada = AnswerHaptics { pulsos++ }
        assertEquals(1, pulsos)
        elegir(vm, hapticaRecreada, ContenidoUnidades.todas.first().respuestaEsperadaId)
        assertEquals(1, pulsos)

        vm.continuar()
        elegir(vm, hapticaRecreada, ContenidoUnidades.todas[1].respuestaEsperadaId)
        assertEquals(2, pulsos)
    }

    @Test
    fun `descubrir no admite elecciones ni genera pulsos`() {
        val vm = SesionViewModel(
            RepositorioFalso(), Etapa.DESCUBRIR, ContenidoUnidades.todas
        )
        var pulsos = 0
        val haptica = AnswerHaptics { pulsos++ }

        elegir(vm, haptica, ContenidoUnidades.todas.first().respuestaEsperadaId)
        vm.continuar()

        assertEquals(0, pulsos)
    }

    private fun elegir(vm: SesionViewModel, haptica: AnswerHaptics, opcionId: String) {
        val anterior = vm.eleccionesAdmitidas.value
        vm.elegir(opcionId)
        val actual = vm.eleccionesAdmitidas.value
        if (actual > anterior) haptica.notificarEleccionAdmitida(actual)
    }
}
