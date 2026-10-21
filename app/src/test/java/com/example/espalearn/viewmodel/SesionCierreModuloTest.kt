package com.example.espalearn.viewmodel

import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.ContenidoUnidades
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import com.example.espalearn.repository.AppRepository
import com.example.espalearn.repository.RepositorioFalso
import com.example.espalearn.repository.Resultado
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SesionCierreModuloTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun preparar() = Dispatchers.setMain(dispatcher)

    @After
    fun limpiar() = Dispatchers.resetMain()

    private suspend fun repositorio(completadas: List<Etapa>): RepositorioFalso {
        val repo = RepositorioFalso()
        repo.guardarPerfil(Perfil("Deniz123", 8, AvatarRef.Predeterminado))
        completadas.forEach { repo.finalizarEtapa(it) }
        return repo
    }

    private fun terminar(vm: SesionViewModel) {
        ContenidoUnidades.todas.forEach {
            vm.elegir(it.respuestaEsperadaId) // Descubrir ignora elecciones.
            vm.continuar()
        }
    }

    @Test
    fun `terminar etapas uno y dos no anuncia el cierre del modulo`() = runTest(dispatcher) {
        listOf(Etapa.DESCUBRIR, Etapa.CON_APOYO).forEach { etapa ->
            val repo = repositorio(Etapa.entries.take(etapa.ordinal))
            val vm = SesionViewModel(repo, etapa, ContenidoUnidades.todas)
            terminar(vm)
            advanceUntilIdle()

            assertTrue(vm.etapaFinalizada.value)
            assertFalse(vm.estado.value.moduloCompletadoPorPrimeraVez)
        }
    }

    @Test
    fun `primer cierre de etapa tres anuncia el modulo solo tras guardarlo`() = runTest(dispatcher) {
        val repo = repositorio(listOf(Etapa.DESCUBRIR, Etapa.CON_APOYO))
        val vm = SesionViewModel(repo, Etapa.MENOS_APOYO, ContenidoUnidades.todas)
        terminar(vm)
        assertFalse(vm.estado.value.moduloCompletadoPorPrimeraVez)
        advanceUntilIdle()

        assertTrue(vm.etapaFinalizada.value)
        assertTrue(vm.estado.value.moduloCompletadoPorPrimeraVez)
    }

    @Test
    fun `repetir cualquiera de las tres etapas no anuncia otro cierre ni borra marco`() = runTest(dispatcher) {
        Etapa.entries.forEach { etapa ->
            val repo = repositorio(Etapa.entries)
            repo.cambiarMarcoAplicado(true)
            val vm = SesionViewModel(repo, etapa, ContenidoUnidades.todas)
            terminar(vm)
            advanceUntilIdle()

            assertTrue(vm.etapaFinalizada.value)
            assertFalse(vm.estado.value.moduloCompletadoPorPrimeraVez)
            val estado = repo.estadoAplicacion.value as com.example.espalearn.model.state.AppUiState.ListoConPerfil
            assertEquals(Etapa.entries.toSet(), estado.progreso.etapasCompletadas)
            assertTrue(estado.progreso.marcoAplicado)
        }
    }

    @Test
    fun `fallo y reintento de etapa tres no celebran antes de confirmar escritura`() = runTest(dispatcher) {
        val repo = repositorio(listOf(Etapa.DESCUBRIR, Etapa.CON_APOYO))
        val vm = SesionViewModel(repo, Etapa.MENOS_APOYO, ContenidoUnidades.todas)
        repo.falloEscritura = true
        terminar(vm)
        advanceUntilIdle()
        assertFalse(vm.etapaFinalizada.value)
        assertFalse(vm.estado.value.moduloCompletadoPorPrimeraVez)

        repo.falloEscritura = false
        vm.continuar()
        advanceUntilIdle()
        assertTrue(vm.etapaFinalizada.value)
        assertTrue(vm.estado.value.moduloCompletadoPorPrimeraVez)
    }

    @Test
    fun `una escritura pendiente no anuncia finalizacion`() = runTest(dispatcher) {
        val base = repositorio(listOf(Etapa.DESCUBRIR, Etapa.CON_APOYO))
        val pendiente = CompletableDeferred<Resultado<Progreso>>()
        val repo = object : AppRepository by base {
            override suspend fun finalizarEtapa(etapa: Etapa): Resultado<Progreso> = pendiente.await()
        }
        val vm = SesionViewModel(repo, Etapa.MENOS_APOYO, ContenidoUnidades.todas)
        terminar(vm)
        runCurrent()
        assertTrue(vm.estado.value.guardandoFinalizacion)
        assertFalse(vm.etapaFinalizada.value)
        assertFalse(vm.estado.value.moduloCompletadoPorPrimeraVez)

        pendiente.complete(Resultado.Exito(Progreso(etapasCompletadas = Etapa.entries.toSet())))
        advanceUntilIdle()
        assertTrue(vm.estado.value.moduloCompletadoPorPrimeraVez)
    }

    @Test
    fun `continuar tras consumir el cierre no vuelve a emitir la finalizacion`() = runTest(dispatcher) {
        val repo = repositorio(listOf(Etapa.DESCUBRIR, Etapa.CON_APOYO))
        val vm = SesionViewModel(repo, Etapa.MENOS_APOYO, ContenidoUnidades.todas)
        terminar(vm)
        advanceUntilIdle()
        vm.finalizacionConsumida()

        vm.continuar()
        advanceUntilIdle()

        assertFalse(vm.etapaFinalizada.value)
        assertTrue(vm.estado.value.etapaCompletada)
    }
}
