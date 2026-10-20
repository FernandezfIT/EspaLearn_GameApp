package com.example.espalearn.viewmodel

import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Perfil
import com.example.espalearn.repository.RepositorioFalso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val perfilExistente = Perfil(
        nick = "Deniz123",
        edad = 8,
        avatar = AvatarRef.Predeterminado
    )

    @Before
    fun preparar() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun `no muestra errores al abrir`() {
        val vm = PerfilViewModel(RepositorioFalso())

        assertFalse(vm.estado.value.validacionActivada)
        assertNull(vm.estado.value.errores.nick)
        assertNull(vm.estado.value.errores.edad)
    }

    @Test
    fun `guardar invalido activa los errores por campo`() {
        val vm = PerfilViewModel(RepositorioFalso())
        vm.editarNick("ab")
        vm.editarEdad("12")

        vm.guardar()

        assertNotNull(vm.estado.value.errores.nick)
        assertNotNull(vm.estado.value.errores.edad)
        assertFalse(vm.estado.value.guardando)
    }

    @Test
    fun `corregir despues de guardar actualiza los errores`() {
        val vm = PerfilViewModel(RepositorioFalso())
        vm.editarNick("ab")
        vm.guardar()
        assertNotNull(vm.estado.value.errores.nick)

        vm.editarNick("Deniz")

        assertNull(vm.estado.value.errores.nick)
    }

    @Test
    fun `guardar valido confirma y no bloquea la salida`() = runTest(dispatcher) {
        val vm = PerfilViewModel(RepositorioFalso())
        vm.editarNick("Deniz123")
        vm.editarEdad("8")

        vm.guardar()
        assertTrue(vm.estado.value.guardando)

        advanceUntilIdle()

        assertFalse(vm.estado.value.guardando)
        assertTrue(vm.guardadoExitoso.value)
        assertNull(vm.estado.value.errorGuardado)
    }

    @Test
    fun `fallo de guardado conserva el borrador y permite reintentar`() = runTest(dispatcher) {
        val repositorio = RepositorioFalso()
        repositorio.falloEscritura = true
        val vm = PerfilViewModel(repositorio)
        vm.editarNick("Deniz123")
        vm.editarEdad("8")

        vm.guardar()
        advanceUntilIdle()

        assertNotNull(vm.estado.value.errorGuardado)
        assertFalse(vm.guardadoExitoso.value)
        // El borrador permanece para corregir o reintentar.
        assertTrue(vm.estado.value.nick == "Deniz123")
    }

    @Test
    fun `modo editar precarga el perfil existente`() {
        val vm = PerfilViewModel(RepositorioFalso(), perfilExistente)

        assertTrue(vm.estado.value.nick == "Deniz123")
        assertTrue(vm.estado.value.edadTexto == "8")
    }

    @Test
    fun `captura cancelada no es error`() {
        val vm = PerfilViewModel(RepositorioFalso())

        vm.capturaFallida("No se pudo abrir la cámara")
        assertNotNull(vm.estado.value.errorCaptura)

        vm.capturaCancelada()
        assertNull(vm.estado.value.errorCaptura)
    }
}
