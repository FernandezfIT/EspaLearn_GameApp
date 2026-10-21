package com.example.espalearn.ui.screens.sesion


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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.espalearn.ui.components.PanelFeedback
import com.example.espalearn.ui.components.TarjetaVocabulario
import com.example.espalearn.ui.components.TarjetaDeslizable

data class OpcionRespuestaUi(
    val id: String,
    val texto: String
)

data class FeedbackUi(
    val titulo: String,
    val mensaje: String
)

data class SesionPresentacion(
    val clavePaso: String,
    val tituloEtapa: String,
    val posicionTexto: String,
    val palabraEspanol: String? = null,
    val apoyoTurco: String? = null,
    val mostrarVerAyuda: Boolean = false,
    val opciones: List<OpcionRespuestaUi> = emptyList(),
    val eleccionHabilitada: Boolean = false,
    val feedback: FeedbackUi? = null,
    val mostrarContinuar: Boolean = false,
    val continuarHabilitado: Boolean = true,
    val textoContinuar: String = "Continuar",
    val errorOperacion: String? = null
)

@Composable
fun SesionScreen(
    presentacion: SesionPresentacion,
    onVerAyuda: () -> Unit,
    onElegir: (String) -> Unit,
    onContinuar: () -> Unit,
    ilustracion: @Composable () -> Unit,
    modifier: Modifier = Modifier
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
                text = presentacion.tituloEtapa,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(text = presentacion.posicionTexto)

            val tieneDosOpciones = presentacion.opciones.size == 2

            if (tieneDosOpciones && presentacion.eleccionHabilitada) {
                Text(
                    text = "Desliza la tarjeta o usa los botones.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            TarjetaDeslizable(
                clavePaso = presentacion.clavePaso,
                habilitado = presentacion.eleccionHabilitada && tieneDosOpciones,
                onIzquierda = {
                    presentacion.opciones.getOrNull(0)?.let { opcion ->
                        onElegir(opcion.id)
                    }
                },
                onDerecha = {
                    presentacion.opciones.getOrNull(1)?.let { opcion ->
                        onElegir(opcion.id)
                    }
                }
            ) {
                TarjetaVocabulario(
                    ilustracion = ilustracion,
                    palabraEspanol = presentacion.palabraEspanol,
                    apoyoTurco = presentacion.apoyoTurco,
                    mostrarVerAyuda = presentacion.mostrarVerAyuda,
                    onVerAyuda = onVerAyuda
                )
            }

            presentacion.opciones.forEachIndexed { indice, opcion ->
                val textoBoton = when {
                    tieneDosOpciones && indice == 0 -> "← ${opcion.texto}"
                    tieneDosOpciones && indice == 1 -> "${opcion.texto} →"
                    else -> opcion.texto
                }

                OutlinedButton(
                    onClick = { onElegir(opcion.id) },
                    enabled = presentacion.eleccionHabilitada,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = textoBoton)
                }
            }
            val feedback = presentacion.feedback

            if (feedback != null) {
                PanelFeedback(
                    titulo = feedback.titulo,
                    mensaje = feedback.mensaje
                )
            }

            val error = presentacion.errorOperacion

            if (error != null) {
                Text(
                    text = "No se pudo completar la operación: $error",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (presentacion.mostrarContinuar) {
                Button(
                    onClick = onContinuar,
                    enabled = presentacion.continuarHabilitado,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = presentacion.textoContinuar)
                }
            }
        }
    }
}