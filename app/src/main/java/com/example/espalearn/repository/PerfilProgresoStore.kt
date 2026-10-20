package com.example.espalearn.repository

import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import kotlinx.coroutines.flow.Flow

/**
 * Almacén de perfil y progreso. Abstrae DataStore para poder probar la
 * lógica de datos con una implementación en memoria, sin device ni archivo.
 */
interface PerfilProgresoStore {

    /** Instantánea persistida: [Instantanea.perfil] es null si no hay perfil. */
    suspend fun leer(): Instantanea

    /** Persiste el perfil y el progreso en una actualización coherente. */
    suspend fun escribir(perfil: Perfil, progreso: Progreso)

    /** Observa los cambios persistidos; útil para la recarga reactiva. */
    fun observar(): Flow<Instantanea>

    data class Instantanea(
        val perfil: Perfil?,
        val progreso: Progreso
    )
}
