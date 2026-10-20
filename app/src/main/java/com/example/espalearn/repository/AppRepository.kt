package com.example.espalearn.repository

import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import com.example.espalearn.model.state.AppUiState
import kotlinx.coroutines.flow.StateFlow

/**
 * Entrada única de datos de la aplicación (K0–K5).
 *
 * El éxito de un guardado significa escritura confirmada, no intención.
 * Finalizar una etapa es idempotente: repetirlo no duplica progreso.
 * Un error de lectura se comunica como [AppUiState.ErrorLectura] y nunca
 * se convierte en un perfil vacío.
 *
 * La implementación duradera (DataStore y archivos privados) llega en el
 * bloque de datos; mientras tanto se usa [RepositorioFalso], marcado como
 * datos de prueba.
 */
interface AppRepository {

    /** Estado de arranque observado por la interfaz: carga, perfil y progreso. */
    val estadoAplicacion: StateFlow<AppUiState>

    /** Reintenta la lectura tras un error de arranque (K0). */
    suspend fun recargar()

    /** Guarda el perfil confirmado, incluida la imagen candidata validada (K1/K5). */
    suspend fun guardarPerfil(perfil: Perfil): Resultado<Perfil>

    /** Sustituye el avatar por una captura confirmada; el predeterminado se conserva si falla. */
    suspend fun confirmarAvatar(avatar: AvatarRef): Resultado<AvatarRef>

    /** Valida el acceso secuencial y marca la etapa como activa (K4). */
    suspend fun iniciarEtapa(etapa: Etapa): Resultado<Progreso>

    /** Marca la etapa como completada tras recorrerla; idempotente (K4). */
    suspend fun finalizarEtapa(etapa: Etapa): Resultado<Progreso>

    /** Aplica o quita el marco del avatar; sólo disponible con el módulo completado (K4). */
    suspend fun cambiarMarcoAplicado(aplicado: Boolean): Resultado<Progreso>
}
