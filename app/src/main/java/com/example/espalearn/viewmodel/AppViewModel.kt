package com.example.espalearn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import com.example.espalearn.model.state.AppUiState
import com.example.espalearn.repository.AppRepository
import com.example.espalearn.repository.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado global de arranque (K0) compartido a nivel raíz/Activity.
 *
 * Observa la lectura del repositorio y ofrece las acciones que la interfaz
 * necesita para reintentar tras un error y para cambiar la preferencia del
 * marco. No decide destinos: la navegación se acuerda fuera.
 */
class AppViewModel(
    private val repositorio: AppRepository
) : ViewModel() {

    val estadoAplicacion: StateFlow<AppUiState> = repositorio.estadoAplicacion

    private val _errorMarco = MutableStateFlow<String?>(null)
    val errorMarco: StateFlow<String?> = _errorMarco.asStateFlow()

    private val _guardandoMarco = MutableStateFlow(false)
    val guardandoMarco: StateFlow<Boolean> = _guardandoMarco.asStateFlow()

    /** Etapa que el usuario pidió abrir; se pierde al recrear el proceso. */
    private var etapaSolicitada: Etapa? = null

    init {
        recargar()
    }

    /** Solicita de nuevo la lectura tras un error de arranque (K0). */
    fun recargar() {
        viewModelScope.launch {
            repositorio.recargar()
        }
    }

    /** Aplica o quita el marco; sólo disponible con el módulo completado (K4). */
    fun cambiarMarcoAplicado(aplicado: Boolean) {
        if (_guardandoMarco.value) return
        _guardandoMarco.value = true
        _errorMarco.value = null
        viewModelScope.launch {
            when (val resultado = repositorio.cambiarMarcoAplicado(aplicado)) {
                is Resultado.Fallo -> _errorMarco.value = resultado.mensaje
                is Resultado.Exito -> _errorMarco.value = null
            }
            _guardandoMarco.value = false
        }
    }

    /** Progreso actual del perfil cargado, o null si aún no hay uno. */
    fun progresoActual(): Progreso? {
        val estado = estadoAplicacion.value
        return (estado as? AppUiState.ListoConPerfil)?.progreso
    }

    /** Perfil cargado, o null si aún no hay uno. */
    fun perfilActual(): Perfil? {
        val estado = estadoAplicacion.value
        return (estado as? AppUiState.ListoConPerfil)?.perfil
    }

    /** Etapa que la sesión debe abrir: la solicitada o la activa persistida. */
    fun etapaActivaActual(): Etapa =
        etapaSolicitada ?: progresoActual()?.etapaActiva ?: Etapa.DESCUBRIR

    /** Inicio y Resumen esperan este resultado; un fallo no cambia la etapa solicitada. */
    suspend fun prepararEtapa(etapa: Etapa): Resultado<Progreso> {
        val resultado = repositorio.iniciarEtapa(etapa)
        if (resultado is Resultado.Exito) etapaSolicitada = etapa
        return resultado
    }

    /** Etapas disponibles ahora mismo según el acceso secuencial (K4). */
    fun etapasDisponibles(): Set<Etapa> {
        val progreso = progresoActual() ?: return setOf(Etapa.DESCUBRIR)
        return Etapa.entries.filter { progreso.etapaDisponible(it) }.toSet()
    }

    /** Confirma el avatar capturado tras guardar un perfil válido (K5). */
    fun confirmarAvatar(avatar: AvatarRef) {
        viewModelScope.launch {
            repositorio.confirmarAvatar(avatar)
        }
    }
}
