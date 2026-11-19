package com.example.espalearn

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.espalearn.app.AppContainer
import com.example.espalearn.data.local.ContenidoDatabase
import com.example.espalearn.domain.ev3.EstadoRecorrido
import com.example.espalearn.model.*
import com.example.espalearn.navigation.AppNavHost
import com.example.espalearn.platform.PermisoRedLocal
import com.example.espalearn.repository.Resultado
import com.example.espalearn.ui.theme.EspaLearn_turco_espaniolTheme
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/** Función: recorrer la UI real con Retrofit, Room y Spring/MySQL; opt-in requiere servidor de demo. */
@RunWith(AndroidJUnit4::class)
class VerticalRealTest {
    @get:Rule val ui = createAndroidComposeRule<ComponentActivity>()
    private var db: ContenidoDatabase? = null
    private var scope: CoroutineScope? = null
    private var nombre: String? = null
    private var archivo: File? = null

    // Aislamiento: nombre/identidad/almacenes propios; nunca limpiar datos del perfil del usuario.
    @After fun limpiar() {
        ui.activityRule.scenario.close()
        db?.close(); scope?.cancel()
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        nombre?.let { contexto.deleteDatabase(it) }
        archivo?.delete()
    }
    private fun esperarTag(tag: String) {
        ui.waitUntil(30_000) { ui.onAllNodesWithTag(tag).fetchSemanticsNodes().size == 1 }
        ui.onNodeWithTag(tag).performScrollTo()
    }

    @Test fun tresEtapasDesdePantallasConfirmanTresPalabrasEnServidor() {
        Assume.assumeTrue("Requiere backend real y argumento runBackendTests=true",
            InstrumentationRegistry.getArguments().getString("runBackendTests") == "true")
        val instrumentacion = InstrumentationRegistry.getInstrumentation()
        val contexto = instrumentacion.targetContext
        if (PermisoRedLocal.requerido(contexto)) instrumentacion.uiAutomation.grantRuntimePermission(contexto.packageName, PermisoRedLocal.NOMBRE)
        val sufijo = UUID.randomUUID().toString()
        nombre = "vertical_real_$sufijo.db"
        db = Room.databaseBuilder(contexto, ContenidoDatabase::class.java, requireNotNull(nombre)).build()
        archivo = File(contexto.cacheDir, "vertical_$sufijo.preferences_pb")
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val store = PreferenceDataStoreFactory.create(scope = requireNotNull(scope)) { requireNotNull(archivo) }
        val container = AppContainer(contexto, store, db)
        runBlocking {
            container.repositorio.recargar()
            assertTrue(container.repositorio.guardarPerfil(Perfil("PruebaF2", 8, AvatarRef.Predeterminado)) is Resultado.Exito)
        }
        // Flujo auténtico: mismos NavHost/ViewModels/repositorios que producción, sin API simulada.
        ui.setContent { EspaLearn_turco_espaniolTheme { AppNavHost(container, rememberNavController()) } }
        for (etapa in 0..2) {
            esperarTag("ev3_abrir")
            ui.onNodeWithTag("ev3_abrir").assertIsEnabled().performClick()
            for (paso in 1..3) {
                ui.waitUntil(30_000) { ui.onAllNodesWithText("Paso $paso de 3").fetchSemanticsNodes().isNotEmpty() }
                if (etapa > 0) {
                    esperarTag("opcion_0")
                    ui.onNodeWithTag("opcion_0").performClick() // Puede ser incorrecta: no hay nota mínima.
                }
                esperarTag("sesion_continuar")
                ui.onNodeWithTag("sesion_continuar").performClick()
            }
        }
        // Comprobación independiente de UI: consultar el resultado remoto y la caché SQL usada.
        ui.waitUntil(30_000) { ui.onAllNodesWithText("Recorrido completado").fetchSemanticsNodes().isNotEmpty() }
        ui.waitUntil(30_000) { ui.onAllNodesWithText("Palabras recorridas: 3 de 3").fetchSemanticsNodes().isNotEmpty() }
        runBlocking {
            val remoto = container.vertical.cargar()
            assertEquals(EstadoRecorrido.COMPLETADO, remoto.recorrido?.estado)
            assertEquals(3, remoto.progreso.completadasUnicas)
            assertTrue(remoto.progreso.primerRecorridoCompletado)
            val ids = requireNotNull(remoto.recorrido).palabraIds
            withContext(Dispatchers.IO) { assertEquals(3, requireNotNull(db).contenidoDao().leer("prueba-3-v1", ids).size) }
            val perfil = container.repositorio.estadoAplicacion.value as com.example.espalearn.model.state.AppUiState.ListoConPerfil
            assertTrue(perfil.progreso.etapasCompletadas.isEmpty())
        }
    }
}
