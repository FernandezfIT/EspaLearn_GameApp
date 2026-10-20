package com.example.espalearn.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Marcador de posición para las ilustraciones del vocabulario.
 *
 * SUSTITUIBLE: sustituir este bloque por la imagen autorizada cuando se
 * incorpore el material definitivo. Mientras no exista, la interfaz muestra
 * un recuadro neutro con el nombre del recurso pendiente, sin presentar el
 * contenido como final. Ver `docs/contenido-y-licencias.md`.
 */
@Composable
fun IlustracionPlaceholder(
    descripcion: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ilustración pendiente: $descripcion",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
