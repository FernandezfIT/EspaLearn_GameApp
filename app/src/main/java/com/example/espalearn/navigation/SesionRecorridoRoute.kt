package com.example.espalearn.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.espalearn.app.AppContainer
import com.example.espalearn.model.Etapa
import com.example.espalearn.platform.AnswerHaptics
import com.example.espalearn.repository.SesionPreparada
import com.example.espalearn.ui.components.IlustracionVocabulario
import com.example.espalearn.ui.mapping.MapeoPresentacion
import com.example.espalearn.ui.screens.sesion.SesionScreen
import com.example.espalearn.viewmodel.*

/** Función: conectar la sesión existente al lote remoto sin rehacer gestos, feedback o háptica. */
@Composable
fun SesionRecorridoRoute(container: AppContainer, modelo: RecorridoViewModel,
                         preparada: SesionPreparada, onInicio: () -> Unit, onResumen: () -> Unit) {
    // Identidad: una sesión de otro recorrido/etapa nunca reutiliza este ViewModel.
    val sesion: SesionViewModel = viewModel(key = "ev3_${preparada.recorrido.recorridoId}_${preparada.etapa.id}",
        factory = ViewModelFactories.sesionEv3(container.repositorio, preparada) { modelo.confirmar(preparada, it) })
    val estado by sesion.estado.collectAsStateWithLifecycle()
    val finalizada by sesion.etapaFinalizada.collectAsStateWithLifecycle()
    val vista = LocalView.current
    val haptica = remember(vista, sesion) { AnswerHaptics(vista) }

    // Navegación: sólo tras cierre remoto confirmado; Atrás conserva activo y permite retomar.
    LaunchedEffect(finalizada) {
        if (finalizada) {
            sesion.finalizacionConsumida()
            if (preparada.etapa == Etapa.MENOS_APOYO) onResumen() else onInicio()
        }
    }
    BackHandler { if (!estado.guardandoFinalizacion) onInicio() }

    // Presentación: botones/swipe comparten elección admitida; la red nunca se llama por gesto.
    SesionScreen(MapeoPresentacion.sesionParaPresentacion(estado), sesion::verAyuda,
        onElegir = { opcion ->
            val antes = sesion.eleccionesAdmitidas.value
            sesion.elegir(opcion)
            val despues = sesion.eleccionesAdmitidas.value
            if (despues > antes) haptica.notificarEleccionAdmitida(despues)
        }, onContinuar = sesion::continuar,
        ilustracion = { IlustracionVocabulario(estado.recursoIlustracion) })
}
