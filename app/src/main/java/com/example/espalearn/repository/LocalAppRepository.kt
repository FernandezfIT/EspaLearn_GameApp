package com.example.espalearn.repository

import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import com.example.espalearn.model.state.AppUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.IOException

/**
 * Implementación duradera sobre Preferences DataStore.
 *
 * El éxito sólo se comunica tras confirmar la escritura. Un error de lectura
 * se expone como [AppUiState.ErrorLectura] sin convertir los datos en un
 * perfil vacío. Finalizar una etapa es idempotente y no duplica progreso.
 * El avatar capturado se confirma como archivo privado; el archivo físico se
 * gestiona en el bloque de cámara y aquí sólo se persiste su referencia.
 */
class LocalAppRepository(
    private val store: PerfilProgresoStore
) : AppRepository {

    private val _estadoAplicacion = MutableStateFlow<AppUiState>(AppUiState.Cargando)
    override val estadoAplicacion: StateFlow<AppUiState> = _estadoAplicacion.asStateFlow()

    /** Serializa las escrituras para no perder actualizaciones concurrentes. */
    private val mutex = Mutex()

    private var perfil: Perfil? = null
    private var progreso = Progreso()

    override suspend fun recargar() {
        _estadoAplicacion.value = AppUiState.Cargando
        try {
            val instantanea = store.leer()
            perfil = instantanea.perfil
            progreso = instantanea.progreso
            _estadoAplicacion.value = instantanea.perfil
                ?.let { AppUiState.ListoConPerfil(it, instantanea.progreso) }
                ?: AppUiState.ListoSinPerfil
        } catch (error: IOException) {
            // No se transforma un error de lectura en ausencia de perfil.
            _estadoAplicacion.value = AppUiState.ErrorLectura(
                "No se pudo leer el perfil guardado."
            )
        }
    }

    override suspend fun guardarPerfil(perfil: Perfil): Resultado<Perfil> = mutex.withLock {
        val anterior = this.perfil
        val nuevo = perfil
        return try {
            store.escribir(nuevo, progreso)
            this.perfil = nuevo
            publicar()
            Resultado.Exito(nuevo)
        } catch (error: IOException) {
            // Editar el perfil no debe borrar el progreso previo.
            this.perfil = anterior
            Resultado.Fallo("No se pudo guardar el perfil.")
        }
    }

    override suspend fun confirmarAvatar(avatar: AvatarRef): Resultado<AvatarRef> =
        mutex.withLock {
            val actual = perfil
                ?: return Resultado.Fallo("No hay un perfil guardado que actualizar.")
            val nuevo = actual.copy(avatar = avatar)
            return try {
                store.escribir(nuevo, progreso)
                perfil = nuevo
                publicar()
                Resultado.Exito(avatar)
            } catch (error: IOException) {
                Resultado.Fallo("No se pudo confirmar la fotografía.")
            }
        }

    override suspend fun iniciarEtapa(etapa: Etapa): Resultado<Progreso> = mutex.withLock {
        if (!progreso.etapaDisponible(etapa)) {
            return Resultado.Fallo("Completa la etapa anterior para abrir esta.")
        }
        val actualPerfil = perfil
            ?: return Resultado.Fallo("Crea un perfil antes de empezar el recorrido.")

        val nuevo = progreso.copy(etapaActiva = etapa)
        return try {
            store.escribir(actualPerfil, nuevo)
            progreso = nuevo
            publicar()
            Resultado.Exito(nuevo)
        } catch (error: IOException) {
            Resultado.Fallo("No se pudo registrar la etapa activa.")
        }
    }

    override suspend fun finalizarEtapa(etapa: Etapa): Resultado<Progreso> = mutex.withLock {
        val actualPerfil = perfil
            ?: return Resultado.Fallo("Crea un perfil antes de completar etapas.")

        // Idempotente: repetir no duplica ni borra progreso.
        val nuevo = progreso.copy(
            etapasCompletadas = progreso.etapasCompletadas + etapa,
            etapaActiva = etapa.siguiente ?: progreso.etapaActiva
        )
        return try {
            store.escribir(actualPerfil, nuevo)
            progreso = nuevo
            publicar()
            Resultado.Exito(nuevo)
        } catch (error: IOException) {
            Resultado.Fallo("No se pudo guardar la etapa completada.")
        }
    }

    override suspend fun cambiarMarcoAplicado(aplicado: Boolean): Resultado<Progreso> =
        mutex.withLock {
            if (!progreso.moduloCompletado) {
                return Resultado.Fallo("El marco se habilita al completar las tres etapas.")
            }
            val actualPerfil = perfil
                ?: return Resultado.Fallo("Crea un perfil antes de cambiar el marco.")

            val nuevo = progreso.copy(marcoAplicado = aplicado)
            return try {
                store.escribir(actualPerfil, nuevo)
                progreso = nuevo
                publicar()
                Resultado.Exito(nuevo)
            } catch (error: IOException) {
                Resultado.Fallo("No se pudo guardar la preferencia del marco.")
            }
        }

    private fun publicar() {
        val actual = perfil ?: return
        _estadoAplicacion.value = AppUiState.ListoConPerfil(actual, progreso)
    }
}
