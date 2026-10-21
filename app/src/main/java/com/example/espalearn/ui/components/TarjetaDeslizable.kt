package com.example.espalearn.ui.components


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun TarjetaDeslizable(
    clavePaso: String,
    habilitado: Boolean,
    onIzquierda: () -> Unit,
    onDerecha: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    key(clavePaso) {
        val izquierdaActual by rememberUpdatedState(onIzquierda)
        val derechaActual by rememberUpdatedState(onDerecha)
        val habilitadoActual by rememberUpdatedState(habilitado)

        val ancho = remember { mutableIntStateOf(0) }
        val anchoPx = ancho.intValue

        val desplazamiento = remember(habilitado, anchoPx) {
            mutableFloatStateOf(0f)
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .onSizeChanged { ancho.intValue = it.width }
                .pointerInput(habilitado, anchoPx) {
                    if (!habilitado || anchoPx <= 0) {
                        return@pointerInput
                    }

                    coroutineScope {
                        var retorno: Job? = null
                        var distanciaGesto = 0f

                        fun volverAlCentro() {
                            retorno?.cancel()

                            val inicio = desplazamiento.floatValue

                            retorno = launch {
                                Animatable(inicio).animateTo(
                                    targetValue = 0f,
                                    animationSpec = spring()
                                ) {
                                    desplazamiento.floatValue = value
                                }

                                desplazamiento.floatValue = 0f
                            }
                        }

                        try {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    retorno?.cancel()
                                    distanciaGesto = 0f
                                },
                                onHorizontalDrag = { _, delta ->
                                    distanciaGesto += delta
                                    desplazamiento.floatValue += delta
                                },
                                onDragCancel = {
                                    distanciaGesto = 0f
                                    volverAlCentro()
                                },
                                onDragEnd = {
                                    val distancia = distanciaGesto
                                    distanciaGesto = 0f

                                    volverAlCentro()

                                    val superaUmbral =
                                        abs(distancia) >= anchoPx * 0.25f

                                    if (habilitadoActual && superaUmbral) {
                                        if (distancia < 0f) {
                                            izquierdaActual()
                                        } else {
                                            derechaActual()
                                        }
                                    }
                                }
                            )
                        } finally {
                            retorno?.cancel()
                            desplazamiento.floatValue = 0f
                        }
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val x = desplazamiento.floatValue

                        translationX = x

                        rotationZ = if (anchoPx > 0) {
                            (x / anchoPx * 6f).coerceIn(-6f, 6f)
                        } else {
                            0f
                        }
                    }
            ) {
                content()
            }
        }
    }
}