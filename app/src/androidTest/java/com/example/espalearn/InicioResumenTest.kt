package com.example.espalearn

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Progreso
import com.example.espalearn.ui.mapping.MapeoPresentacion
import com.example.espalearn.ui.screens.inicio.InicioScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** UI aislada: no borra ni modifica el perfil/progreso del teléfono. */
@RunWith(AndroidJUnit4::class)
class InicioResumenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun resumenNoApareceAntesDeCompletarElModulo() {
        mostrar(Progreso(etapasCompletadas = setOf(Etapa.DESCUBRIR, Etapa.CON_APOYO)))

        compose.onNodeWithText("Ver resumen").assertDoesNotExist()
    }

    @Test
    fun resumenApareceDespuesDeLaTerceraEtapaYEmiteSuAccion() {
        var solicitudes = 0
        mostrar(Progreso(etapasCompletadas = Etapa.entries.toSet())) { solicitudes++ }

        val boton = compose.onNodeWithText("Ver resumen")
        boton.performScrollTo().assertIsDisplayed()
        val terceraEtapa = compose.onNodeWithText("3. Menos apoyo").fetchSemanticsNode().boundsInRoot
        assertTrue(boton.fetchSemanticsNode().boundsInRoot.top >= terceraEtapa.bottom)
        boton.performClick()
        compose.runOnIdle { assertEquals(1, solicitudes) }
    }

    private fun mostrar(progreso: Progreso, onVerResumen: () -> Unit = {}) {
        compose.setContent {
            MaterialTheme {
                InicioScreen(
                    nick = "Deniz123",
                    etapas = MapeoPresentacion.etapasParaMapa(progreso, progreso.etapaActiva),
                    onEditarPerfil = {},
                    onEtapaClick = {},
                    mostrarResumen = progreso.moduloCompletado,
                    onVerResumen = onVerResumen
                )
            }
        }
    }
}
