package com.example.espalearn

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.espalearn.model.AvatarRef
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Perfil
import com.example.espalearn.model.Progreso
import com.example.espalearn.model.state.AppUiState
import com.example.espalearn.navigation.InicioRoute
import com.example.espalearn.repository.Resultado
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Usa la Route real con escritura sustituida; no modifica datos del teléfono. */
@RunWith(AndroidJUnit4::class)
class InicioAperturaTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun falloDeRegistroMantieneInicioYPermiteReintentarSinNavegar() {
        var fallar = true
        var solicitudes = 0
        var navegaciones = 0
        mostrar(
            progreso = Progreso(),
            prepararEtapa = { etapa ->
                solicitudes++
                if (fallar) Resultado.Fallo("No se pudo registrar la etapa activa.")
                else Resultado.Exito(Progreso(etapaActiva = etapa))
            },
            onEtapaPreparada = { navegaciones++ }
        )

        compose.onNodeWithText("Comenzar").performScrollTo().performClick()
        compose.onNodeWithText("No se pudo registrar la etapa activa.")
            .performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Comenzar").assertIsEnabled()
        compose.runOnIdle {
            assertEquals(1, solicitudes)
            assertEquals(0, navegaciones)
            fallar = false
        }

        compose.onNodeWithText("Comenzar").performScrollTo().performClick()
        compose.waitForIdle()
        compose.onNodeWithText("No se pudo registrar la etapa activa.").assertDoesNotExist()
        compose.runOnIdle {
            assertEquals(2, solicitudes)
            assertEquals(1, navegaciones)
        }
    }

    @Test
    fun escrituraPendienteBloqueaAccionesYSoloNavegaAlConfirmar() {
        val pendiente = CompletableDeferred<Resultado<Progreso>>()
        var solicitudes = 0
        var navegaciones = 0
        mostrar(
            progreso = Progreso(etapasCompletadas = Etapa.entries.toSet()),
            prepararEtapa = {
                solicitudes++
                pendiente.await()
            },
            onEtapaPreparada = { navegaciones++ }
        )

        compose.onAllNodesWithText("Repetir")[0].performScrollTo().performClick()
        compose.onNodeWithText("Abriendo etapa…").performScrollTo().assertIsDisplayed()
        compose.onAllNodesWithText("Repetir")[0].assertIsNotEnabled()
        compose.onNodeWithText("Editar perfil").assertIsNotEnabled()
        compose.onNodeWithText("Ver resumen").performScrollTo().assertIsNotEnabled()
        compose.runOnIdle {
            assertEquals(1, solicitudes)
            assertEquals(0, navegaciones)
            pendiente.complete(Resultado.Exito(Progreso(etapaActiva = Etapa.DESCUBRIR)))
        }
        compose.waitForIdle()
        compose.runOnIdle { assertEquals(1, navegaciones) }
        compose.onNodeWithText("Abriendo etapa…").assertDoesNotExist()
    }

    private fun mostrar(
        progreso: Progreso,
        prepararEtapa: suspend (Etapa) -> Resultado<Progreso>,
        onEtapaPreparada: () -> Unit
    ) {
        compose.setContent {
            MaterialTheme {
                InicioRoute(
                    estado = AppUiState.ListoConPerfil(
                        Perfil("Deniz123", 8, AvatarRef.Predeterminado), progreso
                    ),
                    prepararEtapa = prepararEtapa,
                    onEtapaPreparada = onEtapaPreparada,
                    onEditarPerfil = {},
                    onVerResumen = {}
                )
            }
        }
    }
}
