package com.example.espalearn.viewmodel

import com.example.espalearn.model.ContenidoUnidades
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.OpcionUnidad
import com.example.espalearn.model.TemaUnidad
import com.example.espalearn.model.Unidad
import com.example.espalearn.model.state.FaseSesion
import com.example.espalearn.repository.RepositorioFalso
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SesionViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private val unidades = listOf(
        unidad("hola", "Hola", "Merhaba", "hola", "adios"),
        unidad("pan", "Pan", "Ekmek", "pan", "manzana")
    )

    @Before
    fun preparar() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun unidad(
        id: String,
        espanol: String,
        turco: String,
        respuesta: String,
        alternativa: String
    ) = Unidad(
        id = id,
        tema = TemaUnidad.SALUDOS,
        textoEspanol = espanol,
        apoyoTurco = turco,
        recursoIlustracion = "ilustracion_$id",
        opciones = listOf(
            OpcionUnidad(respuesta, espanol),
            OpcionUnidad(alternativa, alternativa)
        ),
        respuestaEsperadaId = respuesta
    )

    @Test
    fun `descubrir presenta sin exigir respuesta`() = runTest(dispatcher) {
        val vm = SesionViewModel(RepositorioFalso(), Etapa.DESCUBRIR, unidades)
        val estado = vm.estado.value

        assertEquals(FaseSesion.PRESENTACION, estado.fase)
        assertTrue(estado.opciones.isEmpty())
        assertTrue(estado.continuarVisible)
        assertFalse(estado.eleccionAdmitida)
    }

    @Test
    fun `practica admite una sola eleccion por paso`() = runTest(dispatcher) {
        val vm = SesionViewModel(RepositorioFalso(), Etapa.CON_APOYO, unidades)

        vm.elegir("hola")
        assertTrue(vm.estado.value.eleccionAdmitida)

        // Una segunda elección en el mismo paso se ignora.
        vm.elegir("adios")
        assertEquals("hola", vm.estado.value.opcionElegidaId)
        assertEquals(1, vm.eleccionesAdmitidas.value)
    }

    @Test
    fun `eleccion desconocida no registra respuesta`() = runTest(dispatcher) {
        val vm = SesionViewModel(RepositorioFalso(), Etapa.CON_APOYO, unidades)

        vm.elegir("opcion_inexistente")

        assertFalse(vm.estado.value.eleccionAdmitida)
        assertEquals(0, vm.eleccionesAdmitidas.value)
    }

    @Test
    fun `acierto y error usan el mismo flujo de feedback`() = runTest(dispatcher) {
        val vm = SesionViewModel(RepositorioFalso(), Etapa.CON_APOYO, unidades)

        vm.elegir("adios")

        assertTrue(vm.estado.value.feedbackVisible)
        assertEquals(false, vm.estado.value.acierto)
        assertEquals(1, vm.eleccionesAdmitidas.value)
    }

    @Test
    fun `el feedback permanece hasta pulsar Continuar`() = runTest(dispatcher) {
        val vm = SesionViewModel(RepositorioFalso(), Etapa.CON_APOYO, unidades)

        vm.elegir("hola")
        assertEquals("con_apoyo-hola", vm.estado.value.idPaso)

        vm.continuar()

        assertEquals("con_apoyo-pan", vm.estado.value.idPaso)
        assertFalse(vm.estado.value.feedbackVisible)
        assertFalse(vm.estado.value.eleccionAdmitida)
    }

    @Test
    fun `practica no avanza sin feedback`() = runTest(dispatcher) {
        val vm = SesionViewModel(RepositorioFalso(), Etapa.CON_APOYO, unidades)

        vm.continuar()

        assertEquals("con_apoyo-hola", vm.estado.value.idPaso)
    }

    @Test
    fun `menos apoyo oculta el turco y permite revelarlo`() = runTest(dispatcher) {
        val vm = SesionViewModel(RepositorioFalso(), Etapa.MENOS_APOYO, unidades)

        assertTrue(vm.estado.value.apoyoTurcoOculto)
        assertFalse(vm.estado.value.ayudaRevelada)

        vm.verAyuda()

        assertTrue(vm.estado.value.ayudaRevelada)
    }

    @Test
    fun `la etapa se anuncia completada solo tras confirmar el guardado`() = runTest(dispatcher) {
        val repositorio = RepositorioFalso()
        val vm = SesionViewModel(repositorio, Etapa.DESCUBRIR, unidades)

        // Recorre todos los pasos de presentación de Descubrir.
        repeat(unidades.size) { vm.continuar() }
        advanceUntilIdle()

        assertTrue(vm.estado.value.etapaCompletada)
        assertTrue(vm.etapaFinalizada.value)
        assertFalse(vm.estado.value.guardandoFinalizacion)
    }

    @Test
    fun `los doce pasos conservan la ilustracion de la unidad correspondiente`() = runTest(dispatcher) {
        Etapa.entries.forEach { etapa ->
            val vm = SesionViewModel(RepositorioFalso(), etapa, ContenidoUnidades.todas)

            ContenidoUnidades.todas.forEachIndexed { indice, unidad ->
                assertEquals(indice + 1, vm.estado.value.posicionPaso)
                assertEquals(unidad.recursoIlustracion, vm.estado.value.recursoIlustracion)
                if (etapa != Etapa.DESCUBRIR) vm.elegir(unidad.respuestaEsperadaId)
                if (indice < ContenidoUnidades.todas.lastIndex) vm.continuar()
            }
        }
    }

    @Test
    fun `elegir y pedir ayuda no cambian la ilustracion actual`() = runTest(dispatcher) {
        val unidad = ContenidoUnidades.todas.first()
        val vm = SesionViewModel(RepositorioFalso(), Etapa.MENOS_APOYO, ContenidoUnidades.todas)

        vm.verAyuda()
        assertEquals(unidad.recursoIlustracion, vm.estado.value.recursoIlustracion)
        vm.elegir(unidad.respuestaEsperadaId)
        assertEquals(unidad.recursoIlustracion, vm.estado.value.recursoIlustracion)
    }

    @Test
    fun `un fallo de guardado permite reintentar sin exito ficticio`() = runTest(dispatcher) {
        val repositorio = RepositorioFalso()
        val vm = SesionViewModel(repositorio, Etapa.DESCUBRIR, unidades)
        repositorio.falloEscritura = true

        repeat(unidades.size) { vm.continuar() }
        advanceUntilIdle()

        assertFalse(vm.estado.value.etapaCompletada)
        assertFalse(vm.etapaFinalizada.value)
        assertTrue(vm.estado.value.errorOperacion != null)

        repositorio.falloEscritura = false
        vm.continuar()
        advanceUntilIdle()

        assertTrue(vm.estado.value.etapaCompletada)
    }
}
