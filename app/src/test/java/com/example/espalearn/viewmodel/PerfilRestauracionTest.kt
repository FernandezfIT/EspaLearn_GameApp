package com.example.espalearn.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.state.AvatarDraft
import com.example.espalearn.model.state.ModoFormularioPerfil
import com.example.espalearn.repository.RepositorioFalso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Reconstrucción con una copia del SavedStateHandle, no muerte real de proceso Android. */
@OptIn(ExperimentalCoroutinesApi::class)
class PerfilRestauracionTest {
    private val dispatcher = StandardTestDispatcher()
    private val anterior = Perfil("Deniz123", 8, AvatarRef.ArchivoPrivado("avatar_anterior.jpg"))

    @Before
    fun preparar() = Dispatchers.setMain(dispatcher)

    @After
    fun limpiar() = Dispatchers.resetMain()

    private fun copiar(handle: SavedStateHandle) = SavedStateHandle(
        handle.keys().associateWith { handle.get<Any?>(it) }
    )

    @Test
    fun `recrear creacion conserva texto y candidata sin convertirlo en edicion`() {
        val handle = SavedStateHandle()
        val vm = PerfilViewModel(RepositorioFalso(), savedState = handle)
        vm.editarNick("Borrador123")
        vm.editarEdad("9")
        vm.capturaCorrecta("candidato_nuevo.jpg")

        val restaurado = PerfilViewModel(RepositorioFalso(), anterior, copiar(handle))

        assertEquals(ModoFormularioPerfil.CREAR, restaurado.estado.value.modo)
        assertEquals("Borrador123", restaurado.estado.value.nick)
        assertEquals("9", restaurado.estado.value.edadTexto)
        assertEquals(AvatarDraft.ArchivoCandidato("candidato_nuevo.jpg"), restaurado.estado.value.avatarCandidato)
        assertFalse(restaurado.guardadoExitoso.value)
    }

    @Test
    fun `recrear edicion conserva avatar anterior y candidata por separado`() {
        val handle = SavedStateHandle()
        val vm = PerfilViewModel(RepositorioFalso(), anterior, handle)
        vm.editarNick("Editado123")
        vm.capturaCorrecta("candidato_nuevo.jpg")

        val restaurado = PerfilViewModel(RepositorioFalso(), savedState = copiar(handle))

        assertEquals(ModoFormularioPerfil.EDITAR, restaurado.estado.value.modo)
        assertEquals("Editado123", restaurado.estado.value.nick)
        assertEquals(AvatarDraft.ArchivoCandidato("avatar_anterior.jpg"), restaurado.estado.value.avatarActual)
        assertEquals(AvatarDraft.ArchivoCandidato("candidato_nuevo.jpg"), restaurado.estado.value.avatarCandidato)
    }

    @Test
    fun `restaurar conserva validacion activada y permite corregir errores`() {
        val handle = SavedStateHandle()
        val vm = PerfilViewModel(RepositorioFalso(), savedState = handle)
        vm.editarNick("ab")
        vm.editarEdad("12")
        vm.guardar()

        val restaurado = PerfilViewModel(RepositorioFalso(), savedState = copiar(handle))
        assertNotNull(restaurado.estado.value.errores.nick)
        assertNotNull(restaurado.estado.value.errores.edad)

        restaurado.editarNick("Deniz123")
        restaurado.editarEdad("8")
        assertNull(restaurado.estado.value.errores.nick)
        assertNull(restaurado.estado.value.errores.edad)
    }

    @Test
    fun `restaurar sin haber guardado no muestra errores de campo`() {
        val handle = SavedStateHandle()
        PerfilViewModel(RepositorioFalso(), savedState = handle).editarNick("ab")

        val restaurado = PerfilViewModel(RepositorioFalso(), savedState = copiar(handle))

        assertFalse(restaurado.estado.value.validacionActivada)
        assertNull(restaurado.estado.value.errores.nick)
        assertNull(restaurado.estado.value.errores.edad)
    }

    @Test
    fun `una escritura interrumpida no deja el formulario bloqueado ni anuncia exito`() = runTest(dispatcher) {
        val handle = SavedStateHandle()
        val vm = PerfilViewModel(RepositorioFalso(), anterior, handle)
        vm.capturaCorrecta("candidato_nuevo.jpg")
        vm.guardar()
        assertTrue(vm.estado.value.guardando)

        val restaurado = PerfilViewModel(RepositorioFalso(), anterior, copiar(handle))

        assertFalse(restaurado.estado.value.guardando)
        assertFalse(restaurado.guardadoExitoso.value)
        assertNotNull(restaurado.estado.value.errorGuardado)
        assertEquals(AvatarDraft.ArchivoCandidato("candidato_nuevo.jpg"), restaurado.estado.value.avatarCandidato)
        advanceUntilIdle()
    }

    @Test
    fun `un formulario nuevo no hereda el borrador descartado`() {
        val vm = PerfilViewModel(RepositorioFalso(), anterior, SavedStateHandle())
        vm.editarNick("Descartado123")
        vm.capturaCorrecta("candidato_descartado.jpg")

        val nuevo = PerfilViewModel(RepositorioFalso(), anterior, SavedStateHandle())

        assertEquals(anterior.nick, nuevo.estado.value.nick)
        assertNull(nuevo.estado.value.avatarCandidato)
        assertFalse(nuevo.estado.value.validacionActivada)
    }
}
