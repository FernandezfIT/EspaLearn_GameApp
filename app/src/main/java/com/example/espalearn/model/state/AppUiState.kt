package com.example.espalearn.model.state

import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso

/**
 * Estados del arranque (K0). La interfaz distingue sin perfil de
 * error/cargando: un fallo de lectura nunca se interpreta como ausencia
 * de perfil ni borra los datos existentes.
 */
sealed interface AppUiState {

    data object Cargando : AppUiState

    data object ListoSinPerfil : AppUiState

    data class ListoConPerfil(
        val perfil: Perfil,
        val progreso: Progreso
    ) : AppUiState

    data class ErrorLectura(val mensaje: String) : AppUiState
}
