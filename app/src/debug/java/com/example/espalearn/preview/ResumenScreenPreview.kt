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
import com.example.espalearn.ui.screens.resumen.LogroResumenUi
import com.example.espalearn.ui.screens.resumen.ResumenPresentacion
import com.example.espalearn.ui.screens.resumen.ResumenScreen
import com.example.espalearn.ui.theme.EspaLearn_turco_espaniolTheme

private val logroEjemplo = LogroResumenUi(
    titulo = "Logro: módulo completado",
    descripcion = "Recorriste Descubrir, Con apoyo y Menos apoyo."
)

private val moduloEjemplo = ResumenPresentacion(
    titulo = "Recorrido completado",
    mensaje = "Recorriste las tres etapas del módulo.",
    progresoTexto = "3 de 3 etapas completadas",
    textoAccionPrincipal = "Repetir Menos apoyo",
    logro = logroEjemplo
)

@Composable
private fun ResumenEjemplo(presentacion: ResumenPresentacion) {
    var mensaje by remember {
        mutableStateOf("DEMO: progreso prefijado; sin guardado.")
    }

    EspaLearn_turco_espaniolTheme {
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

                ResumenScreen(
                    presentacion = presentacion,
                    onAccionPrincipal = {
                        mensaje = "DEMO: se solicitó la acción principal."
                    },
                    onVolverInicio = {
                        mensaje = "DEMO: se solicitó volver a Inicio."
                    },
                    onCambiarMarco = { aplicar ->
                        mensaje = "DEMO: cambio solicitado, aplicar=$aplicar."
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(name = "Resumen — etapa", widthDp = 360, heightDp = 720)
@Composable
private fun ResumenEtapaPreview() {
    ResumenEjemplo(
        ResumenPresentacion(
            titulo = "Etapa completada",
            mensaje = "Recorriste todos los pasos de Descubrir.",
            progresoTexto = "1 de 3 etapas completadas",
            textoAccionPrincipal = "Siguiente etapa"
        )
    )
}

@Preview(name = "Resumen — módulo", widthDp = 360, heightDp = 720)
@Composable
private fun ResumenModuloPreview() {
    ResumenEjemplo(moduloEjemplo)
}

@Preview(name = "Resumen — marco aplicado", widthDp = 360, heightDp = 720)
@Composable
private fun ResumenMarcoAplicadoPreview() {
    ResumenEjemplo(
        moduloEjemplo.copy(
            logro = logroEjemplo.copy(marcoAplicado = true)
        )
    )
}

@Preview(name = "Resumen — guardando marco", widthDp = 360, heightDp = 720)
@Composable
private fun ResumenGuardandoMarcoPreview() {
    ResumenEjemplo(
        moduloEjemplo.copy(
            logro = logroEjemplo.copy(guardandoMarco = true)
        )
    )
}

@Preview(name = "Resumen — fallo de marco", widthDp = 360, heightDp = 720)
@Composable
private fun ResumenErrorMarcoPreview() {
    ResumenEjemplo(
        moduloEjemplo.copy(
            logro = logroEjemplo.copy(
                errorMarco = "Fallo de escritura de ejemplo."
            )
        )
    )
}
