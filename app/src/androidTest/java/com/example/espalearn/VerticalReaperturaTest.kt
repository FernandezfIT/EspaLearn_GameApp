package com.example.espalearn

import androidx.datastore.preferences.core.*
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.espalearn.app.AppContainer
import com.example.espalearn.data.local.ContenidoDatabase
import com.example.espalearn.domain.ev3.ResultadoPalabra
import com.example.espalearn.model.*
import com.example.espalearn.platform.PermisoRedLocal
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.io.File
import java.util.UUID

/** Función: reabrir DataStore/Room reales y recuperar desde Spring el lote y la etapa pendiente. */
@RunWith(AndroidJUnit4::class)
class VerticalReaperturaTest {
    @Test fun conservaIdentidadContenidoLegadoYEtapaAlReabrirAlmacenes() = runBlocking {
        Assume.assumeTrue("Requiere backend real", InstrumentationRegistry.getArguments().getString("runBackendTests") == "true")
        val instrumentacion = InstrumentationRegistry.getInstrumentation()
        val contexto = instrumentacion.targetContext
        if (PermisoRedLocal.requerido(contexto)) instrumentacion.uiAutomation.grantRuntimePermission(contexto.packageName, PermisoRedLocal.NOMBRE)
        val sufijo = UUID.randomUUID().toString()
        val nombreDb = "reapertura_$sufijo.db"
        val archivo = File(contexto.cacheDir, "reapertura_$sufijo.preferences_pb")
        var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        var db = Room.databaseBuilder(contexto, ContenidoDatabase::class.java, nombreDb).build()
        try {
            // Primer proceso lógico: perfil ficticio con recompensa EV2 ya obtenida.
            val store = PreferenceDataStoreFactory.create(scope = scope) { archivo }
            val original = AppContainer(contexto, store, db)
            original.repositorio.recargar()
            original.repositorio.guardarPerfil(Perfil("Reapertura", 8, AvatarRef.Predeterminado))
            store.edit {
                it[stringPreferencesKey("etapas_completadas")] = "descubrir,con_apoyo,menos_apoyo"
                it[booleanPreferencesKey("marco_aplicado")] = true
            }
            val preparada = original.vertical.preparar()
            val info = original.vertical.preferencias()
            original.vertical.confirmar(preparada, preparada.unidades.map { ResultadoPalabra(it.id, null, false) })

            // Reapertura real de los archivos: no reutilizar la memoria del DAO ni de DataStore.
            db.close(); scope.cancel(); scope.coroutineContext[Job]?.join()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            val storeNuevo = PreferenceDataStoreFactory.create(scope = scope) { archivo }
            db = Room.databaseBuilder(contexto, ContenidoDatabase::class.java, nombreDb).build()
            val restaurado = AppContainer(contexto, storeNuevo, db)
            val remoto = restaurado.vertical.cargar()
            assertEquals(preparada.recorrido.recorridoId, remoto.recorrido?.recorridoId)
            assertEquals(preparada.recorrido.palabraIds, remoto.recorrido?.palabraIds)
            assertEquals(Etapa.CON_APOYO, remoto.recorrido?.etapaPendiente)
            assertEquals(0, remoto.progreso.completadasUnicas)
            assertFalse(remoto.progreso.primerRecorridoCompletado)
            assertEquals(info.aprendizId, restaurado.vertical.preferencias().aprendizId)
            assertTrue(restaurado.vertical.preferencias().marcoLegado)
            assertTrue(restaurado.vertical.preferencias().marcoAplicado)
            assertEquals(Etapa.CON_APOYO, restaurado.vertical.preparar().etapa)
            withContext(Dispatchers.IO) {
                assertEquals(3, db.contenidoDao().leer("prueba-3-v1", preparada.recorrido.palabraIds).size)
            }
            restaurado.repositorio.recargar()
            val perfil = restaurado.repositorio.estadoAplicacion.value as com.example.espalearn.model.state.AppUiState.ListoConPerfil
            assertEquals("Reapertura", perfil.perfil.nick)
            assertEquals(AvatarRef.Predeterminado, perfil.perfil.avatar)
        } finally {
            db.close(); scope.cancel(); scope.coroutineContext[Job]?.join()
            contexto.deleteDatabase(nombreDb); archivo.delete()
        }
    }
}
