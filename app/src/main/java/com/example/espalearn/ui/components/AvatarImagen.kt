package com.example.espalearn.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

@Composable
fun AvatarImagen(
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    marcoAplicado: Boolean = false,
    descripcion: String = "Avatar ficticio"
) {
    val fondo = MaterialTheme.colorScheme.secondaryContainer
    val rasgos = MaterialTheme.colorScheme.onSecondaryContainer
    val colorMarco = MaterialTheme.colorScheme.primary

    val borde = if (marcoAplicado) {
        Modifier.border(
            width = 4.dp,
            color = colorMarco,
            shape = CircleShape
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(112.dp)
            .semantics {
                contentDescription = descripcion
                stateDescription = if (marcoAplicado) {
                    "Marco aplicado"
                } else {
                    "Sin marco"
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(borde)
                .padding(6.dp)
                .clip(CircleShape)
                .background(fondo)
        ) {
            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val radioOjo = size.minDimension * 0.035f

                    drawCircle(
                        color = rasgos,
                        radius = radioOjo,
                        center = Offset(
                            x = size.width * 0.40f,
                            y = size.height * 0.38f
                        )
                    )

                    drawCircle(
                        color = rasgos,
                        radius = radioOjo,
                        center = Offset(
                            x = size.width * 0.60f,
                            y = size.height * 0.38f
                        )
                    )

                    drawArc(
                        color = rasgos,
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(
                            x = size.width * 0.30f,
                            y = size.height * 0.45f
                        ),
                        size = Size(
                            width = size.width * 0.40f,
                            height = size.height * 0.20f
                        ),
                        style = Stroke(
                            width = size.minDimension * 0.03f,
                            cap = StrokeCap.Round
                        )
                    )
                }
            }
        }
    }
}