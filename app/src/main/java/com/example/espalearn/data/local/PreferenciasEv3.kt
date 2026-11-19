package com.example.espalearn.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.espalearn.model.Etapa
import com.example.espalearn.domain.ev3.PreferenciasUsuarioEv3
import kotlinx.coroutines.flow.first
import java.util.UUID

/** Función: persistir identidad demo y preferencias EV3 sin reemplazar perfil/avatar/progreso EV2. */
class PreferenciasEv3(private val store: DataStore<Preferences>) {
    // Migración atómica/versionada: un marco desbloqueado pero desactivado también se conserva.
    suspend fun leer(): PreferenciasUsuarioEv3 {
        store.edit { p ->
            if (p[IDENTIDAD] == null) p[IDENTIDAD] = UUID.randomUUID().toString()
            if (p[MIGRACION] == null) {
                val etapas = p[stringPreferencesKey("etapas_completadas")].orEmpty().split(',').map { it.trim() }.toSet()
                p[LEGADO] = Etapa.entries.all { it.id in etapas }
                p[MARCO] = p[LEGADO] == true && p[booleanPreferencesKey("marco_aplicado")] == true
                p[MIGRACION] = 1
            }
        }
        val p = store.data.first()
        return PreferenciasUsuarioEv3(requireNotNull(p[IDENTIDAD]), p[ULTIMO], p[LEGADO] == true, p[MARCO] == true)
    }

    // Inicio: conservar la misma petición mientras no se conoce su resultado; no es cola offline.
    suspend fun solicitudInicio(): String {
        leer() // Inicializar identidad/legado antes de cualquier escritura de preferencias nuevas.
        store.edit { if (it[SOLICITUD] == null) it[SOLICITUD] = UUID.randomUUID().toString() }
        return requireNotNull(store.data.first()[SOLICITUD])
    }
    suspend fun recordarRecorrido(id: String) {
        leer()
        store.edit { it[ULTIMO] = id; it.remove(SOLICITUD) }
    }
    suspend fun aplicarMarco(aplicado: Boolean) {
        leer()
        store.edit { it[MARCO] = aplicado }
    }

    // Claves nuevas: nunca sobrescribir las claves de perfil ni inferir palabras completadas del legado.
    private companion object {
        val IDENTIDAD = stringPreferencesKey("ev3_aprendiz_id")
        val ULTIMO = stringPreferencesKey("ev3_ultimo_recorrido")
        val SOLICITUD = stringPreferencesKey("ev3_inicio_pendiente")
        val MIGRACION = intPreferencesKey("ev3_migracion_version")
        val LEGADO = booleanPreferencesKey("ev3_marco_legado")
        val MARCO = booleanPreferencesKey("ev3_marco_aplicado")
    }
}
