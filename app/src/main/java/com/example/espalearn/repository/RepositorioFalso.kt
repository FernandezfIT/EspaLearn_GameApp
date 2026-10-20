package com.example.espalearn.repository

import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import com.example.espalearn.model.state.AppUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repositorio de datos de prueba, explícito y en memoria.
 *
 * Permite probar la lógica y la interfaz sin persistencia real: los datos
 * se pierden al cerrar el proceso y no sustituyen a la implementación con
 * DataStore. Los estados de error pueden forzarse mediante los indicadores
 * de constructor para simular fallos de lectura y escritura.
 */
class RepositorioFalso(
    estadoInicial: AppUiState = AppUiState.ListoSinPerfil,
    var falloLectura: Boolean = false,
    var falloEscritura: Boolean = false
) : AppRepository {

    private val _estadoAplicacion = MutableStateFlow(estadoInicial)
    override val estadoAplicacion: StateFlow<AppUiState> = _estadoAplicacion.asStateFlow()

    private var perfil: Perfil? = null
    private var progreso = Progreso()

    override suspend fun recargar() {
        _estadoAplicacion.value = AppUiState.Cargando
        _estadoAplicacion.value = if (falloLectura) {
            AppUiState.ErrorLectura("No se pudo leer el perfil guardado.")
        } else {
            perfil?.let { AppUiState.ListoConPerfil(it, progreso) } ?: AppUiState.ListoSinPerfil
        }
    }

    override suspend fun guardarPerfil(perfil: Perfil): Resultado<Perfil> {
        if (falloEscritura) {
            return Resultado.Fallo("No se pudo guardar el perfil.")
        }
        this.perfil = perfil
        _estadoAplicacion.value = AppUiState.ListoConPerfil(perfil, progreso)
        return Resultado.Exito(perfil)
    }

    override suspend fun confirmarAvatar(avatar: AvatarRef): Resultado<AvatarRef> {
        if (falloEscritura) {
            return Resultado.Fallo("No se pudo confirmar la fotografía.")
        }
        val actual = perfil ?: return Resultado.Fallo("No hay un perfil guardado que actualizar.")
        val actualizado = actual.copy(avatar = avatar)
        perfil = actualizado
        _estadoAplicacion.value = AppUiState.ListoConPerfil(actualizado, progreso)
        return Resultado.Exito(avatar)
    }

    override suspend fun iniciarEtapa(etapa: Etapa): Resultado<Progreso> {
        if (!progreso.etapaDisponible(etapa)) {
            return Resultado.Fallo("Completa la etapa anterior para abrir esta.")
        }
        if (falloEscritura) {
            return Resultado.Fallo("No se pudo registrar la etapa activa.")
        }
        progreso = progreso.copy(etapaActiva = etapa)
        perfil?.let { _estadoAplicacion.value = AppUiState.ListoConPerfil(it, progreso) }
        return Resultado.Exito(progreso)
    }

    override suspend fun finalizarEtapa(etapa: Etapa): Resultado<Progreso> {
        if (falloEscritura) {
            return Resultado.Fallo("No se pudo guardar la etapa completada.")
        }
        progreso = progreso.copy(
            etapasCompletadas = progreso.etapasCompletadas + etapa,
            etapaActiva = etapa.siguiente ?: progreso.etapaActiva
        )
        perfil?.let { _estadoAplicacion.value = AppUiState.ListoConPerfil(it, progreso) }
        return Resultado.Exito(progreso)
    }

    override suspend fun cambiarMarcoAplicado(aplicado: Boolean): Resultado<Progreso> {
        if (!progreso.moduloCompletado) {
            return Resultado.Fallo("El marco se habilita al completar las tres etapas.")
        }
        if (falloEscritura) {
            return Resultado.Fallo("No se pudo guardar la preferencia del marco.")
        }
        progreso = progreso.copy(marcoAplicado = aplicado)
        perfil?.let { _estadoAplicacion.value = AppUiState.ListoConPerfil(it, progreso) }
        return Resultado.Exito(progreso)
    }
}
