package com.example.espalearn.viewmodel

import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import com.example.espalearn.repository.AppRepository
import com.example.espalearn.repository.RepositorioFalso
import com.example.espalearn.repository.Resultado
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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

/** Resultado del registro y selección; la pila Navigation requiere comprobación integrada. */
@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelEtapaTest {
    private val dispatcher = StandardTestDispatcher()
    private val perfil = Perfil("Deniz123", 8, AvatarRef.Predeterminado)

    @Before
    fun preparar() = Dispatchers.setMain(dispatcher)

    @After
    fun limpiar() = Dispatchers.resetMain()

    @Test
    fun `preparar etapa confirma el registro antes de exponer la nueva seleccion`() = runTest(dispatcher) {
        val repo = RepositorioFalso()
        val vm = AppViewModel(repo)
        advanceUntilIdle()
        repo.guardarPerfil(perfil)
        repo.finalizarEtapa(Etapa.DESCUBRIR)

        val resultado = vm.prepararEtapa(Etapa.CON_APOYO)

        assertTrue(resultado is Resultado.Exito)
        assertEquals(Etapa.CON_APOYO, vm.etapaActivaActual())
    }

    @Test
    fun `un fallo no sustituye la etapa activa ni devuelve exito`() = runTest(dispatcher) {
        val repo = RepositorioFalso()
        val vm = AppViewModel(repo)
        advanceUntilIdle()
        repo.guardarPerfil(perfil)
        repo.finalizarEtapa(Etapa.DESCUBRIR)
        repo.falloEscritura = true

        val resultado = vm.prepararEtapa(Etapa.DESCUBRIR)

        assertTrue(resultado is Resultado.Fallo)
        assertEquals(Etapa.CON_APOYO, vm.etapaActivaActual())
    }

    @Test
    fun `una escritura pendiente no abre anticipadamente la etapa solicitada`() = runTest(dispatcher) {
        val pendiente = CompletableDeferred<Resultado<Progreso>>()
        val repo = object : AppRepository by RepositorioFalso() {
            override suspend fun iniciarEtapa(etapa: Etapa): Resultado<Progreso> = pendiente.await()
        }
        val vm = AppViewModel(repo)
        advanceUntilIdle()
        val tarea = launch { vm.prepararEtapa(Etapa.CON_APOYO) }
        runCurrent()

        assertFalse(tarea.isCompleted)
        assertEquals(Etapa.DESCUBRIR, vm.etapaActivaActual())

        pendiente.complete(Resultado.Exito(Progreso(etapaActiva = Etapa.CON_APOYO)))
        advanceUntilIdle()
        assertTrue(tarea.isCompleted)
        assertEquals(Etapa.CON_APOYO, vm.etapaActivaActual())
    }

    @Test
    fun `preparar repaso conserva las etapas completas y el marco`() = runTest(dispatcher) {
        val repo = RepositorioFalso()
        val vm = AppViewModel(repo)
        advanceUntilIdle()
        repo.guardarPerfil(perfil)
        Etapa.entries.forEach { repo.finalizarEtapa(it) }
        repo.cambiarMarcoAplicado(true)

        val resultado = vm.prepararEtapa(Etapa.DESCUBRIR)

        assertTrue(resultado is Resultado.Exito)
        assertEquals(Etapa.DESCUBRIR, vm.etapaActivaActual())
        assertEquals(Etapa.entries.toSet(), vm.progresoActual()?.etapasCompletadas)
        assertEquals(true, vm.progresoActual()?.marcoAplicado)
    }
}
