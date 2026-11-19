package com.example.espalearn

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.espalearn.data.local.*
import kotlinx.coroutines.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.util.UUID

/** Función: verificar SQL Room real y persistencia al reabrir, sin tocar la caché de la app. */
@RunWith(AndroidJUnit4::class)
class ContenidoRoomTest {
    @Test fun guardarReabrirYSepararVersiones() = runBlocking {
        val contexto = ApplicationProvider.getApplicationContext<android.content.Context>()
        val nombre = "prueba_room_${UUID.randomUUID()}.db"
        fun abrir() = Room.databaseBuilder(contexto, ContenidoDatabase::class.java, nombre).build()
        try {
            // Escritura: misma clave reemplaza su contenido; otra versión no queda borrada.
            val db = abrir()
            withContext(Dispatchers.IO) {
                db.contenidoDao().guardar(listOf(ContenidoEntity("pan", "v1", "inicial"), ContenidoEntity("pan", "v2", "otra")))
                db.contenidoDao().guardar(listOf(ContenidoEntity("pan", "v1", "actualizada")))
            }
            db.close()
            // Reapertura: consulta nueva del archivo SQLite, no de un fake ni de memoria de ViewModel.
            val reabierta = abrir()
            withContext(Dispatchers.IO) {
                assertEquals("actualizada", reabierta.contenidoDao().leer("v1", listOf("pan")).single().contenidoJson)
                assertEquals("otra", reabierta.contenidoDao().leer("v2", listOf("pan")).single().contenidoJson)
                assertTrue(reabierta.contenidoDao().leer("v1", listOf("inexistente")).isEmpty())
            }
            reabierta.close()
        } finally { contexto.deleteDatabase(nombre) }
    }
}
