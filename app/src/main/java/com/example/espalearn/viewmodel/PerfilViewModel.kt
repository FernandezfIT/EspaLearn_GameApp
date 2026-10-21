package com.example.espalearn.viewmodel

import androidx.lifecycle.SavedStateHandle
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

/** Borrador restaurable; sólo se anuncia éxito después de confirmar la escritura. */
class PerfilViewModel(
    private val repositorio: AppRepository,
    private val perfilExistente: Perfil? = null,
    private val savedState: SavedStateHandle = SavedStateHandle()
) : ViewModel() {

    private val _estado = MutableStateFlow(restaurarBorrador())
    val estado: StateFlow<PerfilUiState> = _estado.asStateFlow()

    private val _guardadoExitoso = MutableStateFlow(false)
    val guardadoExitoso: StateFlow<Boolean> = _guardadoExitoso.asStateFlow()

    init {
        persistirBorrador()
    }

    fun editarNick(nick: String) {
        if (_estado.value.guardando) return
        actualizar { actual ->
            val nuevo = actual.copy(nick = nick)
            if (actual.validacionActivada) nuevo.copy(errores = calcularErrores(nuevo)) else nuevo
        }
    }

    fun editarEdad(edadTexto: String) {
        if (_estado.value.guardando) return
        actualizar { actual ->
            val nuevo = actual.copy(edadTexto = edadTexto)
            if (actual.validacionActivada) nuevo.copy(errores = calcularErrores(nuevo)) else nuevo
        }
    }

    fun capturaCorrecta(nombreArchivo: String) {
        actualizar {
            it.copy(avatarCandidato = AvatarDraft.ArchivoCandidato(nombreArchivo), errorCaptura = null)
        }
    }

    fun capturaFallida(mensaje: String) {
        actualizar { it.copy(errorCaptura = mensaje) }
    }

    /** Cancelar otro intento conserva tanto la candidata previa como el avatar persistido. */
    fun capturaCancelada() {
        actualizar { it.copy(errorCaptura = null) }
    }

    fun guardar() {
        val actual = _estado.value
        if (actual.guardando) return

        val errores = calcularErrores(actual)
        if (errores.hayErrores) {
            actualizar { it.copy(validacionActivada = true, errores = errores) }
            return
        }

        val edad = PerfilValidator.edadConfirmada(actual.edadTexto) ?: return
        val avatar = cuandoAvatarConfirmado(actual)
        actualizar {
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
                    actualizar { it.copy(guardando = false) }
                    _guardadoExitoso.value = true
                }
                is Resultado.Fallo -> actualizar {
                    it.copy(guardando = false, errorGuardado = resultado.mensaje)
                }
            }
        }
    }

    fun guardadoConsumido() {
        _guardadoExitoso.value = false
    }

    private fun actualizar(transformar: (PerfilUiState) -> PerfilUiState) {
        _estado.update(transformar)
        persistirBorrador()
    }

    /** Sólo primitivas y nombres de archivo; nunca Context, Bitmap ni URI/archivo completo. */
    private fun persistirBorrador() {
        val actual = _estado.value
        savedState["perfil.iniciado"] = true
        savedState["perfil.modo"] = actual.modo.name
        savedState["perfil.nick"] = actual.nick
        savedState["perfil.edad"] = actual.edadTexto
        savedState["perfil.avatarActual"] = (actual.avatarActual as? AvatarDraft.ArchivoCandidato)?.nombreArchivo
        savedState["perfil.candidata"] = (actual.avatarCandidato as? AvatarDraft.ArchivoCandidato)?.nombreArchivo
        savedState["perfil.validacion"] = actual.validacionActivada
        savedState["perfil.guardando"] = actual.guardando
        savedState["perfil.errorCaptura"] = actual.errorCaptura
        savedState["perfil.errorGuardado"] = actual.errorGuardado
    }

    private fun restaurarBorrador(): PerfilUiState {
        if (savedState.get<Boolean>("perfil.iniciado") != true) {
            return perfilExistente?.let { perfil ->
                PerfilUiState(
                    modo = ModoFormularioPerfil.EDITAR,
                    nick = perfil.nick,
                    edadTexto = perfil.edad.toString(),
                    avatarActual = cuandoAvatarActual(perfil.avatar)
                )
            } ?: PerfilUiState()
        }
        val restaurado = PerfilUiState(
            modo = if (savedState.get<String>("perfil.modo") == ModoFormularioPerfil.EDITAR.name) {
                ModoFormularioPerfil.EDITAR
            } else {
                ModoFormularioPerfil.CREAR
            },
            nick = savedState.get<String>("perfil.nick").orEmpty(),
            edadTexto = savedState.get<String>("perfil.edad").orEmpty(),
            avatarActual = savedState.get<String>("perfil.avatarActual")
                ?.let { AvatarDraft.ArchivoCandidato(it) } ?: AvatarDraft.Predeterminado,
            avatarCandidato = savedState.get<String>("perfil.candidata")
                ?.let { AvatarDraft.ArchivoCandidato(it) },
            validacionActivada = savedState.get<Boolean>("perfil.validacion") == true,
            // Una escritura interrumpida no se reanuda como si siguiera ejecutándose.
            guardando = false,
            errorCaptura = savedState["perfil.errorCaptura"],
            errorGuardado = if (savedState.get<Boolean>("perfil.guardando") == true) {
                "El guardado se interrumpió. Revisa los datos y vuelve a guardar."
            } else {
                savedState["perfil.errorGuardado"]
            }
        )
        return if (restaurado.validacionActivada) {
            restaurado.copy(errores = calcularErrores(restaurado))
        } else {
            restaurado
        }
    }

    private fun calcularErrores(estado: PerfilUiState): PerfilErrores =
        PerfilValidator.validar(estado.nick, estado.edadTexto)

    private fun cuandoAvatarConfirmado(estado: PerfilUiState): AvatarRef =
        cuandoAvatarActualRef(estado.avatarCandidato ?: estado.avatarActual)

    private fun cuandoAvatarActualRef(avatar: AvatarDraft): AvatarRef = when (avatar) {
        is AvatarDraft.ArchivoCandidato -> AvatarRef.ArchivoPrivado(avatar.nombreArchivo)
        AvatarDraft.Predeterminado -> AvatarRef.Predeterminado
    }

    private fun cuandoAvatarActual(avatar: AvatarRef): AvatarDraft = when (avatar) {
        is AvatarRef.ArchivoPrivado -> AvatarDraft.ArchivoCandidato(avatar.nombreArchivo)
        AvatarRef.Predeterminado -> AvatarDraft.Predeterminado
    }
}
