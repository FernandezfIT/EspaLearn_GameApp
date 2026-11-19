package com.example.espalearn.ui.screens.recorrido

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.espalearn.domain.ev3.EstadoRecorrido
import com.example.espalearn.model.*
import com.example.espalearn.ui.components.AvatarImagen
import com.example.espalearn.ui.components.rememberAvatarPainter
import com.example.espalearn.viewmodel.RecorridoUiState

/** Función: representar Inicio/Resumen EV3 y sus estados sin ejecutar red ni modificar datos. */
@Composable
fun RecorridoScreen(perfil: Perfil, estado: RecorridoUiState, resumen: Boolean,
                    puedeConectar: Boolean, marcoLegadoAplicado: Boolean,
                    onPermiso: () -> Unit, onAbrir: () -> Unit, onActualizar: () -> Unit,
                    onPerfil: () -> Unit, onResumen: () -> Unit, onVolver: () -> Unit, onMarco: () -> Unit) {
    // Cabecera: reutilizar avatar/perfil y distinguir recompensa de cobertura, no de dominio.
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val recorrido = estado.remoto?.recorrido
        val completo = recorrido?.estado == EstadoRecorrido.COMPLETADO
        Text(if (resumen && completo) "Recorrido completado" else if (resumen) "Resumen del recorrido" else "EspaLearn",
            style = MaterialTheme.typography.headlineMedium)
        Text("Hola, ${perfil.nick}")
        AvatarImagen(painter = rememberAvatarPainter(perfil.avatar),
            marcoAplicado = estado.info?.marcoAplicado ?: marcoLegadoAplicado)
        OutlinedButton(onClick = onPerfil, enabled = !estado.operando) { Text("Editar perfil") }

        // Conectividad: denegación de permiso no se interpreta como ausencia de recorrido.
        if (!puedeConectar) {
            Text("Autoriza la conexión con el servidor local del proyecto para cargar el recorrido.")
            Button(onClick = onPermiso) { Text("Autorizar conexión local") }
        } else {
            if (estado.cargando || estado.operando) {
                CircularProgressIndicator()
                Text("Preparando datos…")
            }
            estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            val remoto = estado.remoto
            if (remoto != null && !estado.cargando) {
                Text("Palabras recorridas: ${remoto.progreso.completadasUnicas} de ${remoto.progreso.totalPalabras}",
                    modifier = Modifier.testTag("ev3_cobertura"))
                Text("Catálogo de prueba F2 · tres palabras. No representa dominio del vocabulario.", style = MaterialTheme.typography.bodySmall)
                recorrido?.let {
                    Etapa.entries.forEach { etapa ->
                        Text("${etapa.ordinal + 1}. ${etapa.id.replace('_', ' ')}: " +
                            if (etapa in it.etapasConfirmadas) "confirmada" else "pendiente")
                    }
                }
                if (estado.marcoDisponible) {
                    Text(if (estado.info?.marcoLegado == true) "Marco heredado conservado" else "Primer recorrido confirmado: marco disponible")
                    OutlinedButton(onClick = onMarco, enabled = !estado.operando) {
                        Text(if (estado.marcoAplicado) "Quitar marco" else "Aplicar marco")
                    }
                }
                val puedeAbrir = recorrido?.estado == EstadoRecorrido.ACTIVO ||
                    remoto.progreso.completadasUnicas < remoto.progreso.totalPalabras
                if (puedeAbrir) {
                    Button(onClick = onAbrir, enabled = !estado.operando && estado.error == null,
                        modifier = Modifier.fillMaxWidth().testTag("ev3_abrir")) {
                        Text(if (recorrido?.estado == EstadoRecorrido.ACTIVO) "Retomar etapa pendiente" else "Comenzar recorrido")
                    }
                } else {
                    Text("Terminaste el catálogo de prueba. El repaso se integrará en la fase correspondiente.")
                }
                if (!resumen && recorrido != null) OutlinedButton(onClick = onResumen, enabled = !estado.operando) { Text("Ver resumen") }
            }
            OutlinedButton(onClick = onActualizar, enabled = !estado.operando) { Text("Actualizar desde servidor") }
        }
        // Salida: nunca elimina un recorrido ni limpia el perfil por volver a Inicio.
        if (resumen) OutlinedButton(onClick = onVolver, enabled = !estado.operando,
            modifier = Modifier.testTag("ev3_volver")) { Text("Volver a Inicio") }
    }
}
