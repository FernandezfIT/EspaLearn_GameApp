package com.example.espalearn.ui.screens.arranque


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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CargandoScreen(
    modifier: Modifier = Modifier
) {
    EstadoArranqueLayout(
        titulo = "Abriendo EspaLearn",
        mensaje = "Cargando tu perfil y recorrido.",
        modifier = modifier
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorCargaScreen(
    mensaje: String,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    EstadoArranqueLayout(
        titulo = "No pudimos abrir tu recorrido",
        mensaje = mensaje,
        modifier = modifier
    ) {
        Button(onClick = onReintentar) {
            Text(text = "Reintentar")
        }
    }
}

@Composable
private fun EstadoArranqueLayout(
    titulo: String,
    mensaje: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Text(
                text = mensaje,
                textAlign = TextAlign.Center
            )

            content()
        }
    }
}