package com.example.espalearn.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp



data class EtapaMapaUi( // Datos para dibujar el mapa
    val id: String,
    val titulo: String,
    val descripcion: String,
    val estado: String,
    val accion: String,
    val habilitada: Boolean
)

@Composable
fun MapaEtapas(  // Representación de la lista
    etapas: List<EtapaMapaUi>,
    onEtapaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        etapas.forEach { etapa ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = etapa.titulo,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(text = etapa.descripcion)

                    Text(
                        text = "Estado: ${etapa.estado}",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Button(
                        onClick = { onEtapaClick(etapa.id) },
                        enabled = etapa.habilitada,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = etapa.accion)
                    }
                }
            }
        }
    }
}