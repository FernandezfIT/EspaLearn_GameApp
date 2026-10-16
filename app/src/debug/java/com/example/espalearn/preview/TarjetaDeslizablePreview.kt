package com.example.espalearn.preview


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.espalearn.ui.components.TarjetaDeslizable
import com.example.espalearn.ui.components.TarjetaVocabulario
import com.example.espalearn.ui.theme.EspaLearn_turco_espaniolTheme

@Preview(
    name = "Swipe — demostración",
    widthDp = 360,
    heightDp = 720
)
@Composable
private fun TarjetaDeslizablePreview() {
    var numeroPaso by remember { mutableIntStateOf(1) }
    var habilitado by remember { mutableStateOf(true) }
    var ayudaVisible by remember { mutableStateOf(false) }
    var mensaje by remember {
        mutableStateOf("DEMO: sin evaluación ni avance.")
    }

    val elegirIzquierda: () -> Unit = {
        mensaje = "DEMO: se solicitó Opción A."
    }

    val elegirDerecha: () -> Unit = {
        mensaje = "DEMO: se solicitó Opción B."
    }

    EspaLearn_turco_espaniolTheme {
        Scaffold { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = mensaje)

                Text(text = "← Opción A · Opción B →")

                TarjetaDeslizable(
                    clavePaso = "demo_$numeroPaso",
                    habilitado = habilitado,
                    onIzquierda = elegirIzquierda,
                    onDerecha = elegirDerecha
                ) {
                    TarjetaVocabulario(
                        palabraEspanol = null,
                        apoyoTurco = if (ayudaVisible) {
                            "[Apoyo turco de prueba]"
                        } else {
                            null
                        },
                        mostrarVerAyuda = !ayudaVisible,
                        onVerAyuda = { ayudaVisible = true },
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
                        }
                    )
                }

                Button(
                    onClick = elegirIzquierda,
                    enabled = habilitado,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "← Opción A")
                }

                Button(
                    onClick = elegirDerecha,
                    enabled = habilitado,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Opción B →")
                }

                OutlinedButton(
                    onClick = { habilitado = !habilitado }
                ) {
                    Text(
                        text = if (habilitado) {
                            "Deshabilitar elección de prueba"
                        } else {
                            "Habilitar elección de prueba"
                        }
                    )
                }

                OutlinedButton(
                    onClick = {
                        numeroPaso += 1
                        ayudaVisible = false
                        mensaje = "DEMO: nueva clave $numeroPaso."
                    }
                ) {
                    Text(text = "Cambiar clave de prueba")
                }
            }
        }
    }
}