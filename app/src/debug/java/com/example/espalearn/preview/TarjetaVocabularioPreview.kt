package com.example.espalearn.preview


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
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
import com.example.espalearn.ui.components.TarjetaVocabulario
import com.example.espalearn.ui.theme.EspaLearn_turco_espaniolTheme

@Composable
private fun TarjetaEjemplo(
    titulo: String,
    palabraEspanol: String?,
    apoyoVisible: Boolean,
    ayudaDisponible: Boolean
) {
    var ayudaVisible by remember { mutableStateOf(false) }

    EspaLearn_turco_espaniolTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "DEMO: $titulo",
                style = MaterialTheme.typography.titleLarge
            )

            TarjetaVocabulario(
                palabraEspanol = palabraEspanol,
                apoyoTurco = if (apoyoVisible || ayudaVisible) {
                    "[Apoyo turco de prueba]"
                } else {
                    null
                },
                mostrarVerAyuda = ayudaDisponible && !ayudaVisible,
                onVerAyuda = { ayudaVisible = true },
                ilustracion = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "[Ilustración de prueba]")
                    }
                }
            )
        }
    }
}

@Preview(name = "Tarjeta — Descubrir", showBackground = true)
@Composable
private fun TarjetaDescubrirPreview() {
    TarjetaEjemplo(
        titulo = "Descubrir",
        palabraEspanol = "Hola",
        apoyoVisible = true,
        ayudaDisponible = false
    )
}

@Preview(name = "Tarjeta — Con apoyo", showBackground = true)
@Composable
private fun TarjetaConApoyoPreview() {
    TarjetaEjemplo(
        titulo = "Con apoyo",
        palabraEspanol = null,
        apoyoVisible = true,
        ayudaDisponible = false
    )
}

@Preview(name = "Tarjeta — Menos apoyo", showBackground = true)
@Composable
private fun TarjetaMenosApoyoPreview() {
    TarjetaEjemplo(
        titulo = "Menos apoyo",
        palabraEspanol = null,
        apoyoVisible = false,
        ayudaDisponible = true
    )
}