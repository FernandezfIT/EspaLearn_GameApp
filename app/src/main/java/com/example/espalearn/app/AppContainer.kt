package com.example.espalearn.app

import android.content.Context
import com.example.espalearn.repository.AppPreferencesStore
import com.example.espalearn.repository.AppRepository
import com.example.espalearn.repository.LocalAppRepository
import com.example.espalearn.repository.dataStorePreferencias

/**
 * Contenedor de dependencias de la aplicación, sin inyección de frameworks.
 *
 * Crea y reutiliza una única instancia del repositorio y del DataStore,
 * evitando abrir el mismo archivo desde varias pantallas. Las factories de
 * ViewModels reciben el repositorio desde aquí.
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
