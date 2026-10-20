package com.example.espalearn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.espalearn.domain.PerfilValidator
import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.state.AvatarDraft
import com.example.espalearn.model.state.ModoFormularioPerfil
import com.example.espalearn.model.state.PerfilErrores
import com.example.espalearn.model.state.PerfilUiState
import com.example.espalearn.repository.AppRepository
import com.example.espalearn.repository.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado del formulario de perfil (K1), reutilizado para crear y editar.
 *
 * No muestra errores al abrir: el primer Guardar activa la validación y,
 * desde entonces, corregir un campo actualiza su error. Durante el guardado
 * se impide un segundo envío; el éxito sólo se comunica tras confirmar la
 * escritura y la interfaz decide entonces la salida.
 */
class PerfilViewModel(
    private val repositorio: AppRepository,
    private val perfilExistente: Perfil? = null
) : ViewModel() {

    private val _estado = MutableStateFlow(
        perfilExistente?.let { perfil ->
            PerfilUiState(
                modo = ModoFormularioPerfil.EDITAR,
                nick = perfil.nick,
                edadTexto = perfil.edad.toString(),
                avatarActual = cuandoAvatarActual(perfil.avatar)
            )
        } ?: PerfilUiState()
    )
    val estado: StateFlow<PerfilUiState> = _estado.asStateFlow()

    private val _guardadoExitoso = MutableStateFlow(false)
    val guardadoExitoso: StateFlow<Boolean> = _guardadoExitoso.asStateFlow()

    fun editarNick(nick: String) {
        _estado.update { actual ->
            val nuevo = actual.copy(nick = nick)
            if (actual.validacionActivada) nuevo.copy(errores = calcularErrores(nuevo)) else nuevo
        }
    }

    fun editarEdad(edadTexto: String) {
        _estado.update { actual ->
            val nuevo = actual.copy(edadTexto = edadTexto)
            if (actual.validacionActivada) nuevo.copy(errores = calcularErrores(nuevo)) else nuevo
        }
    }

    /** Registra el resultado de la captura: candidata, sin error (K5). */
    fun capturaCorrecta(nombreArchivo: String) {
        _estado.update {
            it.copy(
                avatarCandidato = AvatarDraft.ArchivoCandidato(nombreArchivo),
                errorCaptura = null
            )
        }
    }

    /** Fallo de captura: mensaje y reintento, conservando el avatar anterior. */
    fun capturaFallida(mensaje: String) {
        _estado.update { it.copy(errorCaptura = mensaje) }
    }

    /** Cancelar cámara no es error: conserva avatar y borrador. */
    fun capturaCancelada() {
        _estado.update { it.copy(errorCaptura = null) }
    }

    /** Valida y solicita el guardado; no asume éxito por el toque. */
    fun guardar() {
        val actual = _estado.value
        if (actual.guardando) return

        val errores = calcularErrores(actual)
        if (errores.hayErrores) {
            _estado.update { it.copy(validacionActivada = true, errores = errores) }
            return
        }

        val edad = PerfilValidator.edadConfirmada(actual.edadTexto) ?: return
        val avatar = cuandoAvatarConfirmado(actual)

        _estado.update {
            it.copy(
                validacionActivada = true,
                errores = PerfilErrores(),
                guardando = true,
                errorGuardado = null
            )
        }

        viewModelScope.launch {
            val perfil = Perfil(
                nick = PerfilValidator.normalizarNick(actual.nick),
                edad = edad,
                avatar = avatar
            )
            when (val resultado = repositorio.guardarPerfil(perfil)) {
                is Resultado.Exito -> {
                    _estado.update { it.copy(guardando = false) }
                    _guardadoExitoso.value = true
                }
                is Resultado.Fallo -> {
                    _estado.update {
                        it.copy(guardando = false, errorGuardado = resultado.mensaje)
                    }
                }
            }
        }
    }

    /** Consumido por la interfaz al salir como guardado exitoso. */
    fun guardadoConsumido() {
        _guardadoExitoso.value = false
    }

    private fun calcularErrores(estado: PerfilUiState): PerfilErrores =
        PerfilValidator.validar(estado.nick, estado.edadTexto)

    private fun cuandoAvatarConfirmado(estado: PerfilUiState): AvatarRef =
        when (val candidato = estado.avatarCandidato) {
            is AvatarDraft.ArchivoCandidato -> AvatarRef.ArchivoPrivado(candidato.nombreArchivo)
            else -> cuandoAvatarActualRef(estado.avatarActual)
        }

    private fun cuandoAvatarActualRef(avatar: AvatarDraft): AvatarRef =
        when (avatar) {
            is AvatarDraft.ArchivoCandidato -> AvatarRef.ArchivoPrivado(avatar.nombreArchivo)
            AvatarDraft.Predeterminado -> AvatarRef.Predeterminado
        }

    private fun cuandoAvatarActual(avatar: AvatarRef): AvatarDraft =
        when (avatar) {
            is AvatarRef.ArchivoPrivado -> AvatarDraft.ArchivoCandidato(avatar.nombreArchivo)
            AvatarRef.Predeterminado -> AvatarDraft.Predeterminado
        }
}
