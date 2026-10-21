package com.example.espalearn.ui.screens.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.espalearn.ui.components.CampoPerfil
import androidx.compose.ui.graphics.painter.Painter
import com.example.espalearn.ui.components.AvatarImagen


/*
*  Recibe borrador, errores, estado de guardado y funciones. Los errores de campo, captura y guardado
*  están separados porque representan problemas distintos
*
* */

@Composable
fun PerfilScreen(
    nick: String,
    edad: String,
    onNickChange: (String) -> Unit,
    onEdadChange: (String) -> Unit,
    onTomarFoto: () -> Unit,
    onGuardar: () -> Unit,
    modifier: Modifier = Modifier,
    editando: Boolean = false,
    guardando: Boolean = false,
    errorNick: String? = null,
    errorEdad: String? = null,
    errorCaptura: String? = null,
    errorGuardado: String? = null,
    avatarPainter: Painter? = null,
    marcoAplicado: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (editando) "Editar perfil" else "Crear perfil",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Usa un nick inventado y una edad simulada.",
                style = MaterialTheme.typography.bodyMedium
            )

            AvatarImagen(
                painter = avatarPainter,
                marcoAplicado = marcoAplicado,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            OutlinedButton(
                onClick = onTomarFoto,
                enabled = !guardando,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Tomar foto")
            }

            Text(
                text = "Fotografía únicamente una ilustración autorizada.",
                style = MaterialTheme.typography.bodySmall
            )

            if (errorCaptura != null) {
                Text(
                    text = "No se pudo tomar la foto: $errorCaptura",
                    color = MaterialTheme.colorScheme.error
                )
            }

            CampoPerfil(
                value = nick,
                onValueChange = onNickChange,
                label = "Nick ficticio",
                errorMessage = errorNick,
                enabled = !guardando
            )

            CampoPerfil(
                value = edad,
                onValueChange = onEdadChange,
                label = "Edad simulada",
                errorMessage = errorEdad,
                enabled = !guardando,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            if (errorGuardado != null) {
                Text(
                    text = "No se pudo guardar: $errorGuardado",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onGuardar,
                enabled = !guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (guardando) "Guardando…" else "Guardar perfil"
                )
            }
        }
    }
}