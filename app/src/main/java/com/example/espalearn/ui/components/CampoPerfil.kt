package com.example.espalearn.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.espalearn.R


@Composable
fun CampoPerfil(
    value: String,                      // texto actual
    onValueChange: (String) -> Unit,    // función que avisa cuando cambia
    label: String,                      // nombre visible del campo
    modifier: Modifier = Modifier,      // permite que la pantalla controle su distribución.
    errorMessage: String? = null,       // mensaje recibido; null significa que no se muestra error.
    enabled: Boolean = true,            // permite bloquear la edición durante guardado.
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default  // configura el teclado apropiado sin validar el contenido.
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        singleLine = true,
        enabled = enabled,
        isError = errorMessage != null,
        trailingIcon = if (errorMessage != null) {
            {
                Icon(
                    painter = painterResource(R.drawable.ic_error_campo),
                    contentDescription = "Error en $label"
                )
            }
        } else {
            null
        },
        supportingText = if (errorMessage != null) {
            { Text(text = "Error: $errorMessage") }
        } else {
            null
        },
        keyboardOptions = keyboardOptions
    )
}
