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
import com.example.espalearn.ui.screens.arranque.CargandoScreen
import com.example.espalearn.ui.screens.arranque.ErrorCargaScreen
import com.example.espalearn.ui.theme.EspaLearn_turco_espaniolTheme

@Composable
private fun ArranqueEjemplo(
    content: @Composable (Modifier) -> Unit
) {
    EspaLearn_turco_espaniolTheme {
        Scaffold { innerPadding ->
            content(
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}

@Preview(name = "Arranque — cargando", widthDp = 360, heightDp = 720)
@Composable
private fun ArranqueCargandoPreview() {
    ArranqueEjemplo { modifier ->
        CargandoScreen(modifier = modifier)
    }
}

@Preview(name = "Arranque — error", widthDp = 360, heightDp = 720)
@Composable
private fun ArranqueErrorPreview() {
    var aviso by remember {
        mutableStateOf("DEMO: fallo de lectura prefijado.")
    }

    ArranqueEjemplo { modifier ->
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = aviso,
                modifier = Modifier.padding(16.dp)
            )

            ErrorCargaScreen(
                mensaje = "No pudimos leer tu perfil. Intenta nuevamente.",
                onReintentar = {
                    aviso = "DEMO: se solicitó otra lectura; no se ejecutó."
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}