package com.example.espalearn.viewmodel

import com.example.espalearn.domain.ev3.ResultadoPalabra
import com.example.espalearn.model.*
import com.example.espalearn.repository.*
import com.example.espalearn.ui.mapping.MapeoPresentacion
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

/** Función: verificar que integrar F2 no adelanta navegación ni escribe progreso en el perfil EV2. */
@OptIn(ExperimentalCoroutinesApi::class)
class SesionRemotaTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun preparar() = Dispatchers.setMain(dispatcher)
    @After fun limpiar() = Dispatchers.resetMain()
    private val unidades = listOf("pan", "manzana", "pera").map { id ->
        Unidad(id, TemaUnidad.ALIMENTOS, id, "apoyo", "ilustracion_$id",
            listOf(OpcionUnidad("${id}_a", id), OpcionUnidad("${id}_b", "Otra")), "${id}_a", "Explicación de $id")
    }
    private suspend fun perfil() = RepositorioFalso().also { it.guardarPerfil(Perfil("Deniz123", 8, AvatarRef.Predeterminado)) }

    // Guardado pendiente: pulsar otra vez no envía una segunda confirmación.
    @Test fun `espera confirmacion remota sin guardar etapas localmente`() = runTest(dispatcher) {
        val local = perfil()
        val pendiente = CompletableDeferred<Resultado<Progreso>>()
        var llamadas = 0
        val vm = SesionViewModel(local, Etapa.DESCUBRIR, unidades, "recorrido/descubrir") {
            llamadas++; pendiente.await()
        }
        repeat(3) { vm.continuar() }
        runCurrent()
        assertTrue(vm.estado.value.guardandoFinalizacion)
        assertFalse(vm.etapaFinalizada.value)
        vm.continuar(); runCurrent()
        assertEquals(1, llamadas)
        pendiente.complete(Resultado.Exito(Progreso(setOf(Etapa.DESCUBRIR))))
        advanceUntilIdle()
        assertTrue(vm.etapaFinalizada.value)
        val perfil = local.estadoAplicacion.value as com.example.espalearn.model.state.AppUiState.ListoConPerfil
        assertTrue(perfil.progreso.etapasCompletadas.isEmpty())
    }

    // Reintento: conservar resultados/ayuda y no anunciar éxito ante fallo del servidor.
    @Test fun `reintento envia los mismos resultados tras un fallo`() = runTest(dispatcher) {
        val cuerpos = mutableListOf<List<ResultadoPalabra>>()
        val vm = SesionViewModel(perfil(), Etapa.MENOS_APOYO, unidades) { resultados ->
            cuerpos.add(resultados)
            if (cuerpos.size == 1) Resultado.Fallo("Fallo de prueba") else Resultado.Exito(Progreso(Etapa.entries.toSet()))
        }
        unidades.forEach { vm.verAyuda(); vm.elegir(it.respuestaEsperadaId); vm.continuar() }
        advanceUntilIdle()
        assertFalse(vm.etapaFinalizada.value)
        assertNotNull(vm.estado.value.errorOperacion)
        vm.continuar(); advanceUntilIdle()
        assertEquals(cuerpos.first(), cuerpos.last())
        assertTrue(cuerpos.last().all { it.ayudaUsada })
        assertTrue(vm.etapaFinalizada.value)
    }
    @Test fun `explicacion editorial no revela respuesta antes de elegir`() = runTest(dispatcher) {
        val vm = SesionViewModel(perfil(), Etapa.CON_APOYO, unidades)
        assertNull(MapeoPresentacion.sesionParaPresentacion(vm.estado.value).explicacion)
        vm.elegir(unidades.first().respuestaEsperadaId)
        assertEquals(unidades.first().explicacion, MapeoPresentacion.sesionParaPresentacion(vm.estado.value).explicacion)
    }
}
