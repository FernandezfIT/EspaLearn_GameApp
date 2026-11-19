package com.example.espalearn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.espalearn.domain.ev3.*
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Progreso
import com.example.espalearn.repository.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** Función: representar carga, preparación y avance EV3 sin guardar progreso en el perfil EV2. */
data class RecorridoUiState(
    val cargando: Boolean = true,
    val operando: Boolean = false,
    val error: String? = null,
    val info: PreferenciasUsuarioEv3? = null,
    val remoto: EstadoVertical? = null,
    val preparada: SesionPreparada? = null
) {
    // Proyección: marco legado y preferencia son locales; el logro nuevo requiere respuesta remota.
    val marcoDisponible: Boolean get() = info?.marcoLegado == true || remoto?.progreso?.primerRecorridoCompletado == true
    val marcoAplicado: Boolean get() = info?.marcoAplicado == true
}

/** Función: orquestar acciones MVVM y navegar sólo después de preparación/cierre confirmados. */
class RecorridoViewModel(private val repositorio: VerticalRepository) : ViewModel() {
    private val _estado = MutableStateFlow(RecorridoUiState())
    val estado = _estado.asStateFlow()

    // Arranque/reintento: leer preferencia antes de red para conservar el marco incluso sin conexión.
    fun recargar() {
        if (_estado.value.operando) return
        _estado.update { it.copy(cargando = true, operando = true, error = null) }
        viewModelScope.launch {
            try {
                val info = repositorio.preferencias()
                _estado.update { it.copy(info = info) }
                val remoto = repositorio.cargar()
                _estado.update { it.copy(remoto = remoto, cargando = false) }
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { _estado.update { it.copy(error = mensaje(error), cargando = false) } }
            finally { _estado.update { it.copy(operando = false) } }
        }
    }

    // Inicio: sin lista de tres cartas válidas no se abre la pantalla de sesión.
    fun abrir(onLista: () -> Unit) {
        if (_estado.value.operando || _estado.value.cargando || _estado.value.error != null) return
        _estado.update { it.copy(operando = true, error = null) }
        viewModelScope.launch {
            try {
                val preparada = repositorio.preparar()
                _estado.update { it.copy(preparada = preparada, operando = false) }
                onLista()
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { _estado.update { it.copy(error = mensaje(error)) } }
            finally { _estado.update { it.copy(operando = false) } }
        }
    }

    // Cierre: adaptador para reutilizar SesionViewModel; jamás llama al guardado local de etapas.
    suspend fun confirmar(preparada: SesionPreparada, resultados: List<ResultadoPalabra>): Resultado<Progreso> {
        return try {
            val confirmado = repositorio.confirmar(preparada, resultados)
            _estado.update { actual -> actual.copy(remoto = actual.remoto?.copy(recorrido = confirmado)) }
            Resultado.Exito(Progreso(confirmado.etapasConfirmadas.toSet(),
                confirmado.etapaPendiente ?: Etapa.MENOS_APOYO, _estado.value.marcoAplicado))
        } catch (error: CancellationException) { throw error }
        catch (error: Exception) { Resultado.Fallo(mensaje(error)) }
    }

    // Preferencia: no se concede un logro al pulsar; sólo se aplica/quita uno ya habilitado.
    fun cambiarMarco() {
        val actual = _estado.value
        if (actual.operando || !actual.marcoDisponible) return
        _estado.update { it.copy(operando = true, error = null) }
        viewModelScope.launch {
            try {
                repositorio.aplicarMarco(!actual.marcoAplicado)
                val info = repositorio.preferencias()
                _estado.update { it.copy(info = info) }
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { _estado.update { it.copy(error = mensaje(error)) } }
            finally { _estado.update { it.copy(operando = false) } }
        }
    }

    // Errores públicos: conservar cancelación estructurada y no mostrar trazas ni detalles SQL.
    private fun mensaje(error: Exception): String =
        (error as? FalloOperacionEv3)?.mensajePublico ?: "No se pudo completar la operación. Reintenta."

}
