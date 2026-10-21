package com.example.espalearn.preview

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.espalearn.ui.screens.perfil.PerfilScreen
import com.example.espalearn.ui.theme.EspaLearn_turco_espaniolTheme

@Composable
private fun PerfilEjemplo( // Mantiene texto local y registra que se pulsaron botones. No valida. No navega y no simula un guardado exisotso
    guardando: Boolean = false,
    errorNick: String? = null,
    errorEdad: String? = null,
    errorGuardado: String? = null
) {
    var nick by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var mensaje by remember {
        mutableStateOf("DEMO: sin validación, cámara ni guardado.")
    }

    EspaLearn_turco_espaniolTheme {
        // Contenedor que aplica el innerPadding para quede bien centradito
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

                PerfilScreen(
                    nick = nick,
                    edad = edad,
                    onNickChange = { nick = it },
                    onEdadChange = { edad = it },
                    onTomarFoto = {
                        mensaje = "DEMO: se solicitó la cámara; no se abrió."
                    },
                    onGuardar = {
                        mensaje = "DEMO: se solicitó guardar; no se guardó."
                    },
                    guardando = guardando,
                    errorNick = errorNick,
                    errorEdad = errorEdad,
                    errorGuardado = errorGuardado,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(name = "Perfil — inicial", showBackground = true)
@Composable
private fun PerfilInicialPreview() {
    PerfilEjemplo()
}

@Preview(name = "Perfil — errores sintéticos", showBackground = true)
@Composable
private fun PerfilErroresPreview() {
    PerfilEjemplo(
        errorNick = "Introduce un nick ficticio.",
        errorEdad = "Introduce una edad simulada.",
        errorGuardado = "Fallo de escritura de ejemplo."
    )
}

@Preview(name = "Perfil — guardando", showBackground = true)
@Composable
private fun PerfilGuardandoPreview() {
    PerfilEjemplo(guardando = true)
}