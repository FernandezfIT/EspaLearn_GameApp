package com.example.espalearn.preview


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.example.espalearn.ui.components.AvatarImagen
import com.example.espalearn.ui.components.EtapaMapaUi
import com.example.espalearn.ui.components.MapaEtapas

@Composable
fun InicioScreen(
    nick: String,
    etapas: List<EtapaMapaUi>,
    onEditarPerfil: () -> Unit,
    onEtapaClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    avatarPainter: Painter? = null,
    marcoAplicado: Boolean = false
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val usarDosColumnas = maxWidth >= 840.dp

        if (usarDosColumnas) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                BloquePerfilInicio(
                    nick = nick,
                    avatarPainter = avatarPainter,
                    marcoAplicado = marcoAplicado,
                    onEditarPerfil = onEditarPerfil,
                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )

                BloqueRecorridoInicio(
                    etapas = etapas,
                    onEtapaClick = onEtapaClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                BloquePerfilInicio(
                    nick = nick,
                    avatarPainter = avatarPainter,
                    marcoAplicado = marcoAplicado,
                    onEditarPerfil = onEditarPerfil
                )

                BloqueRecorridoInicio(
                    etapas = etapas,
                    onEtapaClick = onEtapaClick
                )
            }
        }
    }
}

@Composable
private fun BloquePerfilInicio(
    nick: String,
    avatarPainter: Painter?,
    marcoAplicado: Boolean,
    onEditarPerfil: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hola, $nick",
            style = MaterialTheme.typography.headlineMedium
        )

        AvatarImagen(
            painter = avatarPainter,
            marcoAplicado = marcoAplicado,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        OutlinedButton(
            onClick = onEditarPerfil
        ) {
            Text(text = "Editar perfil")
        }
    }
}

@Composable
private fun BloqueRecorridoInicio(
    etapas: List<EtapaMapaUi>,
    onEtapaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Tu recorrido",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Saludos y alimentos",
            style = MaterialTheme.typography.bodyLarge
        )

        MapaEtapas(
            etapas = etapas,
            onEtapaClick = onEtapaClick
        )
    }
}