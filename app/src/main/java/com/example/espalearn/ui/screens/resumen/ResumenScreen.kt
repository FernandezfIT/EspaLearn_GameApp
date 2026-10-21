package com.example.espalearn.ui.screens.resumen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.painter.Painter
import com.example.espalearn.ui.components.AvatarImagen

data class LogroResumenUi(
    val titulo: String,
    val descripcion: String,
    val marcoAplicado: Boolean = false,
    val guardandoMarco: Boolean = false,
    val errorMarco: String? = null
)

data class ResumenPresentacion(
    val titulo: String,
    val mensaje: String,
    val progresoTexto: String,
    val textoAccionPrincipal: String,
    val logro: LogroResumenUi? = null
)

@Composable
fun ResumenScreen(
    presentacion: ResumenPresentacion,
    onAccionPrincipal: () -> Unit,
    onVolverInicio: () -> Unit,
    onCambiarMarco: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    avatarPainter: Painter? = null,
    abriendoEtapa: Boolean = false,
    errorApertura: String? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = presentacion.titulo,
                style = MaterialTheme.typography.headlineMedium
            )


            Text(text = presentacion.mensaje)

            Text(
                text = presentacion.progresoTexto,
                style = MaterialTheme.typography.titleMedium
            )

            val logro = presentacion.logro



            if (logro != null) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = logro.titulo,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(text = logro.descripcion)

                        AvatarImagen(
                            painter = avatarPainter,
                            marcoAplicado = logro.marcoAplicado,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Text(
                            text = if (logro.marcoAplicado) {
                                "Marco aplicado al avatar"
                            } else {
                                "Marco sin aplicar"
                            }
                        )

                        OutlinedButton(
                            onClick = {
                                onCambiarMarco(!logro.marcoAplicado)
                            },
                            enabled = !logro.guardandoMarco && !abriendoEtapa,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = when {
                                    logro.guardandoMarco -> "Guardando marco…"
                                    logro.marcoAplicado -> "Quitar marco"
                                    else -> "Aplicar marco"
                                }
                            )
                        }

                        val error = logro.errorMarco

                        if (error != null) {
                            Text(
                                text = "No se pudo cambiar el marco: $error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }


            if (errorApertura != null) {
                Text(text = errorApertura, color = MaterialTheme.colorScheme.error)
            }

            val accionesHabilitadas = !abriendoEtapa && presentacion.logro?.guardandoMarco != true
            Button(
                onClick = onAccionPrincipal,
                enabled = accionesHabilitadas,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (abriendoEtapa) "Abriendo etapa…" else presentacion.textoAccionPrincipal)
            }

            OutlinedButton(
                onClick = onVolverInicio,
                enabled = accionesHabilitadas,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver a Inicio")
            }
        }
    }
}