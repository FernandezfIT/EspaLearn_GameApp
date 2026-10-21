package com.example.espalearn

import android.app.Application
import com.example.espalearn.app.AppContainer

/** Un único contenedor por proceso, también después de recrear MainActivity. */
class EspaLearnApplication : Application() {
    val appContainer: AppContainer by lazy { AppContainer(this) }
}
