package com.example.espalearn

import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.espalearn.app.AppContainer
import com.example.espalearn.viewmodel.AppViewModel
import com.example.espalearn.viewmodel.ViewModelFactories
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith

/** Regresión Android: rotar no puede separar el repositorio del ViewModel retenido. */
@RunWith(AndroidJUnit4::class)
class AppContainerRecreacionTest {
    @Test
    fun recrearActivityConservaContenedorRepositorioYEstadoGlobal() {
        lateinit var inicial: AppContainer
        lateinit var modeloInicial: AppViewModel

        ActivityScenario.launch(MainActivity::class.java).use { escenario ->
            escenario.onActivity { activity ->
                inicial = (activity.application as EspaLearnApplication).appContainer
                modeloInicial = ViewModelProvider(
                    activity, ViewModelFactories.app(inicial.repositorio)
                )[AppViewModel::class.java]
                assertSame(inicial.repositorio.estadoAplicacion, modeloInicial.estadoAplicacion)
            }
            escenario.recreate()
            escenario.onActivity { activity ->
                val actual = (activity.application as EspaLearnApplication).appContainer
                val modeloActual = ViewModelProvider(
                    activity, ViewModelFactories.app(actual.repositorio)
                )[AppViewModel::class.java]
                assertSame(inicial, actual)
                assertSame(inicial.repositorio, actual.repositorio)
                assertSame(modeloInicial, modeloActual)
                assertSame(actual.repositorio.estadoAplicacion, modeloActual.estadoAplicacion)
            }
        }
    }
}
