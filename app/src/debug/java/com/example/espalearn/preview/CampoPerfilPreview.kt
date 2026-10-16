package com.example.espalearn.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.espalearn.ui.components.CampoPerfil
import com.example.espalearn.ui.theme.EspaLearn_turco_espaniolTheme

@Preview(
    name = "CampoPerfil — estados de prueba",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun CampoPerfilPreview() {
    var nick by remember { mutableStateOf("") }

    EspaLearn_turco_espaniolTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Demostración visual; sin validación ni guardado")

            CampoPerfil(
                value = nick,
                onValueChange = { nick = it },
                label = "Nick ficticio"
            )

            CampoPerfil(
                value = "10",
                onValueChange = {},
                label = "Edad simulada",
                errorMessage = "Edad fuera del rango de prueba.",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            CampoPerfil(
                value = "Explorador",
                onValueChange = {},
                label = "Nick ficticio — guardando",
                enabled = false
            )
        }
    }
}