package com.example.espalearn.repository

import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import com.example.espalearn.model.state.AppUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

/**
 * Pruebas del repositorio duradero sobre un almacén en memoria que sustituye
 * al DataStore real. Comprueban éxito confirmado, fallo de lectura, fallo de
 * escritura, sin duplicados y preservación de progreso.
 */
class LocalAppRepositoryTest {

    private val perfilEjemplo = Perfil(
        nick = "Deniz123",
        edad = 8,
        avatar = AvatarRef.Predeterminado
    )

    @Test
    fun `lectura sin perfil deja el estado listo sin perfil`() = runTest {
        val repositorio = LocalAppRepository(AlmacenMemoria())

        repositorio.recargar()

        assertEquals(AppUiState.ListoSinPerfil, repositorio.estadoAplicacion.value)
    }

    @Test
    fun `fallo de lectura no se convierte en perfil vacio`() = runTest {
        val almacen = AlmacenMemoria().apply { falloLectura = true }
        val repositorio = LocalAppRepository(almacen)

        repositorio.recargar()

        assertTrue(repositorio.estadoAplicacion.value is AppUiState.ErrorLectura)
    }

    @Test
    fun `guardar perfil confirma la escritura`() = runTest {
        val almacen = AlmacenMemoria()
        val repositorio = LocalAppRepository(almacen)

        val resultado = repositorio.guardarPerfil(perfilEjemplo)

        assertTrue(resultado is Resultado.Exito)
        assertEquals(perfilEjemplo, almacen.perfilGuardado)
    }

    @Test
    fun `fallo de escritura no confirma el guardado`() = runTest {
        val almacen = AlmacenMemoria().apply { falloEscritura = true }
        val repositorio = LocalAppRepository(almacen)

        val resultado = repositorio.guardarPerfil(perfilEjemplo)

        assertTrue(resultado is Resultado.Fallo)
    }

    @Test
    fun `finalizar etapa es idempotente`() = runTest {
        val almacen = AlmacenMemoria()
        val repositorio = LocalAppRepository(almacen)
        repositorio.guardarPerfil(perfilEjemplo)

        repositorio.finalizarEtapa(Etapa.DESCUBRIR)
        repositorio.finalizarEtapa(Etapa.DESCUBRIR)

        assertEquals(setOf(Etapa.DESCUBRIR), almacen.progresoGuardado.etapasCompletadas)
    }

    @Test
    fun `editar perfil conserva el progreso previo`() = runTest {
        val almacen = AlmacenMemoria()
        val repositorio = LocalAppRepository(almacen)
        repositorio.guardarPerfil(perfilEjemplo)
        repositorio.finalizarEtapa(Etapa.DESCUBRIR)

        repositorio.guardarPerfil(perfilEjemplo.copy(nick = "NuevoNick"))

        assertEquals(setOf(Etapa.DESCUBRIR), almacen.progresoGuardado.etapasCompletadas)
    }

    @Test
    fun `acceso secuencial bloquea etapas posteriores`() = runTest {
        val almacen = AlmacenMemoria()
        val repositorio = LocalAppRepository(almacen)
        repositorio.guardarPerfil(perfilEjemplo)

        val resultado = repositorio.iniciarEtapa(Etapa.MENOS_APOYO)

        assertTrue(resultado is Resultado.Fallo)
    }

    @Test
    fun `el marco solo se habilita con el modulo completo`() = runTest {
        val almacen = AlmacenMemoria()
        val repositorio = LocalAppRepository(almacen)
        repositorio.guardarPerfil(perfilEjemplo)

        assertTrue(repositorio.cambiarMarcoAplicado(true) is Resultado.Fallo)

        Etapa.entries.forEach { repositorio.finalizarEtapa(it) }

        assertTrue(repositorio.cambiarMarcoAplicado(true) is Resultado.Exito)
        assertTrue(almacen.progresoGuardado.marcoAplicado)
    }

    @Test
    fun `la reapertura conserva las etapas completadas`() = runTest {
        val almacen = AlmacenMemoria()
        val repositorio = LocalAppRepository(almacen)
        repositorio.guardarPerfil(perfilEjemplo)
        repositorio.finalizarEtapa(Etapa.DESCUBRIR)

        // Nueva instancia sobre el mismo almacén: simula reabrir la app.
        val repositorioReabierto = LocalAppRepository(almacen)
        repositorioReabierto.recargar()

        val estado = repositorioReabierto.estadoAplicacion.value
        assertTrue(estado is AppUiState.ListoConPerfil)
        assertTrue((estado as AppUiState.ListoConPerfil).progreso.etapaCompletada(Etapa.DESCUBRIR))
        assertFalse(estado.progreso.etapaCompletada(Etapa.CON_APOYO))
    }

    /** Almacén en memoria que imita el contrato del almacén de preferencias. */
    private class AlmacenMemoria : PerfilProgresoStore {

        var perfilGuardado: Perfil? = null
        var progresoGuardado = Progreso()
        var falloLectura = false
        var falloEscritura = false

        override suspend fun leer(): PerfilProgresoStore.Instantanea {
            if (falloLectura) throw IOException("fallo simulado")
            return PerfilProgresoStore.Instantanea(perfilGuardado, progresoGuardado)
        }

        override suspend fun escribir(perfil: Perfil, progreso: Progreso) {
            if (falloEscritura) throw IOException("fallo simulado")
            perfilGuardado = perfil
            progresoGuardado = progreso
        }

        override fun observar(): Flow<PerfilProgresoStore.Instantanea> {
            val flujo = MutableStateFlow(
                PerfilProgresoStore.Instantanea(perfilGuardado, progresoGuardado)
            )
            return flujo.asStateFlow()
        }
    }
}
