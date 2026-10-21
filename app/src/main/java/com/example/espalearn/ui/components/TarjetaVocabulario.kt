package com.example.espalearn.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TarjetaVocabulario(
    ilustracion: @Composable () -> Unit,
    palabraEspanol: String?,
    apoyoTurco: String?,
    mostrarVerAyuda: Boolean,
    onVerAyuda: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp),
                contentAlignment = Alignment.Center
            ) {
                ilustracion()
            }

            if (palabraEspanol != null) {
                Text(
                    text = palabraEspanol,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            if (apoyoTurco != null) {
                Text(
                    text = apoyoTurco,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (mostrarVerAyuda) {
                TextButton(
                    onClick = onVerAyuda
                ) {
                    Text(text = "Ver ayuda")
                }
            }
        }
    }
}