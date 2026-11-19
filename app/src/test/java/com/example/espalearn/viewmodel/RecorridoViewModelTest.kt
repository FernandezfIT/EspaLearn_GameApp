package com.example.espalearn.viewmodel

import com.example.espalearn.domain.ev3.*
import com.example.espalearn.model.*
import com.example.espalearn.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import java.io.IOException
import java.util.UUID

/** Función: verificar estados MVVM de carga/preparación/error sin fingir integración de red. */
@OptIn(ExperimentalCoroutinesApi::class)
class RecorridoViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun preparar() = Dispatchers.setMain(dispatcher)
    @After fun limpiar() = Dispatchers.resetMain()

    // Carga: conservar preferencias locales aunque el servidor no sea alcanzable.
    @Test fun `error de lectura remota no significa ausencia ni borra marco`() = runTest(dispatcher) {
        val repo = VerticalPrueba().apply { falloCarga = true }
        val vm = RecorridoViewModel(repo)
        vm.recargar(); advanceUntilIdle()
        assertNotNull(vm.estado.value.error)
        assertNull(vm.estado.value.remoto)
        assertTrue(vm.estado.value.marcoAplicado)
        assertFalse(vm.estado.value.cargando)
        assertFalse(vm.estado.value.operando)
    }
    @Test fun `preparacion fallida no solicita navegacion`() = runTest(dispatcher) {
        val repo = VerticalPrueba().apply { falloPreparacion = true }
        val vm = RecorridoViewModel(repo)
        vm.recargar(); advanceUntilIdle()
        var navegaciones = 0
        vm.abrir { navegaciones++ }; advanceUntilIdle()
        assertEquals(0, navegaciones)
        assertNull(vm.estado.value.preparada)
        assertNotNull(vm.estado.value.error)
        assertFalse(vm.estado.value.operando)
    }
    @Test fun `preparacion exitosa habilita una sesion del recorrido real`() = runTest(dispatcher) {
        val repo = VerticalPrueba()
        val vm = RecorridoViewModel(repo)
        vm.recargar(); advanceUntilIdle()
        var navegaciones = 0
        vm.abrir { navegaciones++ }; advanceUntilIdle()
        assertEquals(1, navegaciones)
        assertEquals(repo.recorrido.recorridoId, vm.estado.value.preparada?.recorrido?.recorridoId)
        assertEquals(3, vm.estado.value.preparada?.unidades?.size)
    }
    @Test fun `fallo al cerrar no cambia etapas confirmadas`() = runTest(dispatcher) {
        val repo = VerticalPrueba()
        val vm = RecorridoViewModel(repo)
        vm.recargar(); advanceUntilIdle()
        val preparada = repo.preparar()
        val resultado = vm.confirmar(preparada, preparada.unidades.map { ResultadoPalabra(it.id, null, false) })
        assertTrue(resultado is Resultado.Fallo)
        assertTrue(vm.estado.value.remoto?.recorrido?.etapasConfirmadas.orEmpty().isEmpty())
    }

    // Doble explícito: no implementa SQL, HTTP ni una caché real.
    private class VerticalPrueba : VerticalRepository {
        val recorrido = RecorridoEv3(UUID.randomUUID().toString(), "v1", TipoRecorrido.NORMAL,
            EstadoRecorrido.ACTIVO, listOf("a", "b", "c"), emptyList())
        var falloCarga = false; var falloPreparacion = false
        override suspend fun preferencias() = PreferenciasUsuarioEv3("identidad-prueba", null, true, true)
        override suspend fun cargar(): EstadoVertical {
            if (falloCarga) throw IOException("Fallo sintético")
            return EstadoVertical(recorrido, ProgresoEv3(0, 3, false))
        }
        override suspend fun preparar(): SesionPreparada {
            if (falloPreparacion) throw IOException("Fallo sintético")
            val unidades = recorrido.palabraIds.map { id -> Unidad(id, TemaUnidad.ALIMENTOS, id, "apoyo", "imagen",
                listOf(OpcionUnidad("a", "A"), OpcionUnidad("b", "B")), "a") }
            return SesionPreparada(recorrido, Etapa.DESCUBRIR, unidades)
        }
        override suspend fun confirmar(preparada: SesionPreparada, resultados: List<ResultadoPalabra>): RecorridoEv3 = throw IOException("Fallo sintético")
        override suspend fun aplicarMarco(aplicado: Boolean) { }
    }
}
