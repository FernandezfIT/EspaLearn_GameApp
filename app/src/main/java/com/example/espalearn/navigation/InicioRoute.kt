package com.example.espalearn.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.example.espalearn.model.Etapa
import com.example.espalearn.model.Progreso
import com.example.espalearn.model.state.AppUiState
import com.example.espalearn.repository.Resultado
import com.example.espalearn.ui.components.rememberAvatarPainter
import com.example.espalearn.ui.mapping.MapeoPresentacion
import com.example.espalearn.ui.screens.inicio.InicioScreen
import kotlinx.coroutines.launch

/** No abre sesión hasta confirmar el registro; las pruebas pueden sustituir sólo esa operación. */
@Composable
fun InicioRoute(
    estado: AppUiState.ListoConPerfil,
    prepararEtapa: suspend (Etapa) -> Resultado<Progreso>,
    onEtapaPreparada: () -> Unit,
    onEditarPerfil: () -> Unit,
    onVerResumen: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var abriendoEtapa by remember { mutableStateOf(false) }
    var errorApertura by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = abriendoEtapa) { /* Esperar resultado de escritura. */ }

    InicioScreen(
        nick = estado.perfil.nick,
        etapas = MapeoPresentacion.etapasParaMapa(estado.progreso, estado.progreso.etapaActiva)
            .map { it.copy(habilitada = it.habilitada && !abriendoEtapa) },
        onEditarPerfil = { if (!abriendoEtapa) onEditarPerfil() },
        onEtapaClick = { id ->
            val etapa = Etapa.desdeId(id)
            if (etapa != null && estado.progreso.etapaDisponible(etapa) && !abriendoEtapa) {
                abriendoEtapa = true
                errorApertura = null
                scope.launch {
                    try {
                        when (val resultado = prepararEtapa(etapa)) {
                            is Resultado.Exito -> onEtapaPreparada()
                            is Resultado.Fallo -> errorApertura = resultado.mensaje
                        }
                    } finally {
                        abriendoEtapa = false
                    }
                }
            }
        },
        avatarPainter = rememberAvatarPainter(estado.perfil.avatar),
        marcoAplicado = estado.progreso.marcoAplicado,
        mostrarResumen = estado.progreso.moduloCompletado,
        onVerResumen = {
            if (!abriendoEtapa && estado.progreso.moduloCompletado) onVerResumen()
        },
        abriendoEtapa = abriendoEtapa,
        errorApertura = errorApertura
    )
}
