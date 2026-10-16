package com.example.espalearn.preview


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.espalearn.ui.screens.sesion.FeedbackUi
import com.example.espalearn.ui.screens.sesion.OpcionRespuestaUi
import com.example.espalearn.ui.screens.sesion.SesionPresentacion
import com.example.espalearn.ui.screens.sesion.SesionScreen
import com.example.espalearn.ui.theme.EspaLearn_turco_espaniolTheme

private val practicaEjemplo = SesionPresentacion(
    clavePaso = "demo_descubrir_1",
    tituloEtapa = "Descubrir",
    posicionTexto = "Paso de prueba 1 de 4",
    apoyoTurco = "[Apoyo turco de prueba]",
    opciones = listOf(
        OpcionRespuestaUi("demo_a", "Opción A"),
        OpcionRespuestaUi("demo_b", "Opción B")
    ),
    eleccionHabilitada = true
)

@Composable
private fun SesionEjemplo(presentacion: SesionPresentacion) {
    var ayudaVisible by remember(presentacion.clavePaso) {
        mutableStateOf(false)
    }
    var mensaje by remember {
        mutableStateOf("DEMO: sin evaluación ni avance.")
    }

    val presentacionVisible = presentacion.copy(
        apoyoTurco = if (ayudaVisible) {
            "[Apoyo turco de prueba]"
        } else {
            presentacion.apoyoTurco
        },
        mostrarVerAyuda = presentacion.mostrarVerAyuda && !ayudaVisible
    )

    EspaLearn_turco_espaniolTheme {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            ) {
                Text(
                    text = mensaje,
                    modifier = Modifier.padding(16.dp)
                )

                SesionScreen(
                    presentacion = presentacionVisible,
                    onVerAyuda = { ayudaVisible = true },
                    onElegir = { id ->
                        mensaje = "DEMO: elección solicitada $id."
                    },
                    onContinuar = {
                        mensaje = "DEMO: se solicitó continuar; no se avanzó."
                    },
                    ilustracion = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "[Ilustración de prueba]")
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(name = "Sesión — Descubrir", widthDp = 360, heightDp = 720)
@Composable
private fun SesionDescubrirPreview() {
    SesionEjemplo(
        SesionPresentacion(
            clavePaso = "demo_descubrir_1",
            tituloEtapa = "Descubrir",
            posicionTexto = "Paso de prueba 1 de 4",
            palabraEspanol = "Hola",
            apoyoTurco = "[Apoyo turco de prueba]",
            mostrarContinuar = true
        )
    )
}

@Preview(name = "Sesión — Con apoyo", widthDp = 360, heightDp = 720)
@Composable
private fun SesionConApoyoPreview() {
    SesionEjemplo(practicaEjemplo)
}

@Preview(name = "Sesión — Menos apoyo", widthDp = 360, heightDp = 720)
@Composable
private fun SesionMenosApoyoPreview() {
    SesionEjemplo(
        practicaEjemplo.copy(
            clavePaso = "demo_menos_apoyo_1",
            tituloEtapa = "Menos apoyo",
            apoyoTurco = null,
            mostrarVerAyuda = true
        )
    )
}

@Preview(name = "Sesión — Feedback", widthDp = 360, heightDp = 720)
@Composable
private fun SesionFeedbackPreview() {
    SesionEjemplo(
        practicaEjemplo.copy(
            eleccionHabilitada = false,
            feedback = FeedbackUi(
                titulo = "Feedback de prueba",
                mensaje = "Aquí se mostrará el resultado y la respuesta correcta."
            ),
            mostrarContinuar = true
        )
    )
}

@Preview(name = "Sesión — Guardando", widthDp = 360, heightDp = 720)
@Composable
private fun SesionGuardandoPreview() {
    SesionEjemplo(
        practicaEjemplo.copy(
            eleccionHabilitada = false,
            mostrarContinuar = true,
            continuarHabilitado = false,
            textoContinuar = "Guardando…"
        )
    )
}