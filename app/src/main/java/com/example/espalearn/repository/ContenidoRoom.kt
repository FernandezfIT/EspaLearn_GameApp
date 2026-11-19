package com.example.espalearn.repository

import com.example.espalearn.data.local.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Función: aislar Room del repositorio de recorrido y permitir dobles de caché en unitarias. */
interface CacheContenido {
    suspend fun leer(version: String, ids: List<String>): List<ContenidoEntity>
    suspend fun guardar(cartas: List<ContenidoEntity>)
}

/** Función: ejecutar SQL local fuera del hilo de UI; Room no se convierte en fuente de avance. */
class ContenidoRoom(private val dao: ContenidoDao) : CacheContenido {
    // IO: las operaciones Java del DAO son síncronas, por lo que se despachan explícitamente.
    override suspend fun leer(version: String, ids: List<String>) = withContext(Dispatchers.IO) { dao.leer(version, ids) }
    override suspend fun guardar(cartas: List<ContenidoEntity>) = withContext(Dispatchers.IO) { dao.guardar(cartas) }
}
