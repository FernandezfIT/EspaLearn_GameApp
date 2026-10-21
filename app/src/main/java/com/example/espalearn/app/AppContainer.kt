package com.example.espalearn.app

import android.content.Context
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
class AppContainer(context: Context) {

    private val applicationContext = context.applicationContext

    private val preferencesStore: AppPreferencesStore by lazy {
        AppPreferencesStore(applicationContext.dataStorePreferencias)
    }

    val repositorio: AppRepository by lazy {
        LocalAppRepository(preferencesStore)
    }
}
