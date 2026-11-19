package com.example.espalearn.app

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.example.espalearn.BuildConfig
import com.example.espalearn.data.local.ContenidoDatabase
import com.example.espalearn.data.local.PreferenciasEv3
import com.example.espalearn.data.remote.RecorridosApi
import com.example.espalearn.repository.ContenidoRoom
import com.example.espalearn.repository.VerticalRepository
import com.example.espalearn.repository.VerticalRepositoryReal
import com.example.espalearn.ui.mapping.IlustracionUnidadUi
import com.example.espalearn.repository.AppPreferencesStore
import com.example.espalearn.repository.AppRepository
import com.example.espalearn.repository.LocalAppRepository
import com.example.espalearn.repository.dataStorePreferencias

/**
 * Contenedor de dependencias de la aplicación, sin inyección de frameworks.
 *
 * EspaLearnApplication conserva este contenedor durante toda la vida del
 * proceso, no de la Activity. Todas las factories reciben el mismo repositorio
 * incluso después de una rotación. DataStore también se comparte por archivo.
 */
class AppContainer(
    context: Context,
    // Inyección de almacenes aislados para probar el flujo real sin tocar el perfil del dispositivo.
    private val storeOverride: DataStore<Preferences>? = null,
    private val databaseOverride: ContenidoDatabase? = null
) {

    private val applicationContext = context.applicationContext

    private val dataStore by lazy { storeOverride ?: applicationContext.dataStorePreferencias }
    private val preferencesStore: AppPreferencesStore by lazy { AppPreferencesStore(dataStore) }

    // EV3: compartir identidad/Room/cliente por proceso, nunca crear clientes en una recomposición.
    private val contenidoDatabase by lazy {
        databaseOverride ?: Room.databaseBuilder(applicationContext, ContenidoDatabase::class.java, "espalearn_contenido.db").build()
    }
    val vertical: VerticalRepository by lazy {
        VerticalRepositoryReal(RecorridosApi.crear(BuildConfig.API_BASE_URL),
            ContenidoRoom(contenidoDatabase.contenidoDao()), PreferenciasEv3(dataStore),
            IlustracionUnidadUi.entries.map { it.recurso }.toSet())
    }

    val repositorio: AppRepository by lazy {
        LocalAppRepository(preferencesStore)
    }
}
