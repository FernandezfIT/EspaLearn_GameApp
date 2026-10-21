package com.example.espalearn.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Lectura y escritura del perfil y el progreso en Preferences DataStore.
 *
 * Una única instancia de DataStore por archivo, creada desde el contenedor
 * y reutilizada por toda la aplicación. Un error de lectura se propaga al
 * llamante para que no se convierta en un perfil vacío. Los valores
 * desconocidos o incompletos se descartan de forma segura.
 */
class AppPreferencesStore(
    private val dataStore: DataStore<Preferences>
) : PerfilProgresoStore {

    /** Instantánea persistida: [PerfilProgresoStore.Instantanea.perfil] es null si no hay perfil. */
    override suspend fun leer(): PerfilProgresoStore.Instantanea = observar().first()

    /** Persiste el perfil y el progreso en una actualización coherente. */
    override suspend fun escribir(perfil: Perfil, progreso: Progreso) {
        dataStore.edit { preferencias ->
            preferencias[CLAVE_VERSION_ESQUEMA] = VERSION_ESQUEMA
            preferencias[CLAVE_NICK] = perfil.nick
            preferencias[CLAVE_EDAD] = perfil.edad
            preferencias[CLAVE_AVATAR_TIPO] = perfil.avatar.tipoPersistido()

            val archivo = perfil.avatar.nombreArchivoPersistido()
            if (archivo != null) {
                preferencias[CLAVE_AVATAR_ARCHIVO] = archivo
            } else {
                preferencias.remove(CLAVE_AVATAR_ARCHIVO)
            }

            preferencias[CLAVE_ETAPAS_COMPLETADAS] =
                progreso.etapasCompletadas.joinToString(separator = ",") { it.id }
            preferencias[CLAVE_ETAPA_ACTIVA] = progreso.etapaActiva.id
            preferencias[CLAVE_MARCO] = progreso.marcoAplicado
        }
    }

    /** Observa los cambios persistidos; útil para la recarga reactiva. */
    override fun observar(): Flow<PerfilProgresoStore.Instantanea> = dataStore.data
        .catch { error -> throw error }
        .map { it.aInstantanea() }

    private fun Preferences.aInstantanea(): PerfilProgresoStore.Instantanea {
        val version = this[CLAVE_VERSION_ESQUEMA] ?: VERSION_ESQUEMA
        val nick = this[CLAVE_NICK]
        val edad = this[CLAVE_EDAD]
        val tipoAvatar = this[CLAVE_AVATAR_TIPO]
        val archivoAvatar = this[CLAVE_AVATAR_ARCHIVO]

        val perfil = if (nick != null && edad != null && version <= VERSION_ESQUEMA) {
            Perfil(
                nick = nick,
                edad = edad,
                avatar = reconstruirAvatar(tipoAvatar, archivoAvatar)
            )
        } else {
            null
        }

        val completadas = this[CLAVE_ETAPAS_COMPLETADAS]
            .orEmpty()
            .split(',')
            .mapNotNull { Etapa.desdeId(it.trim()) }
            .toSet()

        val etapaActiva = this[CLAVE_ETAPA_ACTIVA]
            ?.let { Etapa.desdeId(it) }
            ?: Etapa.DESCUBRIR

        val progreso = Progreso(
            etapasCompletadas = completadas,
            etapaActiva = etapaActiva,
            marcoAplicado = this[CLAVE_MARCO] ?: false
        )

        return PerfilProgresoStore.Instantanea(perfil, progreso)
    }

    private fun reconstruirAvatar(tipo: String?, archivo: String?): AvatarRef =
        if (tipo == TIPO_AVATAR_ARCHIVO && !archivo.isNullOrBlank()) {
            AvatarRef.ArchivoPrivado(archivo)
        } else {
            AvatarRef.Predeterminado
        }

    /** Fotografía de lo persistido sin exponer claves concretas. */
    companion object {
        const val VERSION_ESQUEMA = 1

        private val CLAVE_VERSION_ESQUEMA = intPreferencesKey("version_esquema")
        private val CLAVE_NICK = stringPreferencesKey("perfil_nick")
        private val CLAVE_EDAD = intPreferencesKey("perfil_edad")
        private val CLAVE_AVATAR_TIPO = stringPreferencesKey("avatar_tipo")
        private val CLAVE_AVATAR_ARCHIVO = stringPreferencesKey("avatar_archivo")
        private val CLAVE_ETAPAS_COMPLETADAS = stringPreferencesKey("etapas_completadas")
        private val CLAVE_ETAPA_ACTIVA = stringPreferencesKey("etapa_activa")
        private val CLAVE_MARCO = booleanPreferencesKey("marco_aplicado")
    }
}

private const val TIPO_AVATAR_PREDETERMINADO = "predeterminado"
private const val TIPO_AVATAR_ARCHIVO = "archivo"

private fun AvatarRef.tipoPersistido(): String = when (this) {
    is AvatarRef.ArchivoPrivado -> TIPO_AVATAR_ARCHIVO
    AvatarRef.Predeterminado -> TIPO_AVATAR_PREDETERMINADO
}

private fun AvatarRef.nombreArchivoPersistido(): String? =
    (this as? AvatarRef.ArchivoPrivado)?.nombreArchivo

/** Contexto de creación del DataStore único de la aplicación. */
val Context.dataStorePreferencias: DataStore<Preferences> by preferencesDataStore(
    name = "espalearn_preferencias"
)
