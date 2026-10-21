package com.example.espalearn.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.espalearn.ui.mapping.IlustracionUnidadUi

/** Dibujos geométricos creados para EspaLearn con asistencia de IA, sin arte externo. */
@Composable
fun IlustracionVocabulario(recurso: String?, modifier: Modifier = Modifier) {
    val ilustracion = IlustracionUnidadUi.desdeRecurso(recurso)
    if (ilustracion == null) {
        IlustracionPlaceholder(descripcion = "recurso no disponible", modifier = modifier)
        return
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF2F7FC))
            .semantics { contentDescription = ilustracion.descripcion }
    ) {
        // Coordenadas lógicas 320 × 200, centradas sin deformar las figuras.
        val escala = minOf(size.width / 320f, size.height / 200f)
        withTransform({
            translate((size.width - 320f * escala) / 2f, (size.height - 200f * escala) / 2f)
            scale(escala, escala, pivot = Offset.Zero)
        }) {
            when (ilustracion) {
                IlustracionUnidadUi.LLEGADA -> dibujarEscena(llegando = true)
                IlustracionUnidadUi.DESPEDIDA -> dibujarEscena(llegando = false)
                IlustracionUnidadUi.PIEZA_HORNEADA -> dibujarPan()
                IlustracionUnidadUi.FRUTA_ROJA -> dibujarManzana()
            }
        }
    }
}

private fun DrawScope.dibujarEscena(llegando: Boolean) {
    drawCircle(Color(0xFFF4C95D), radius = 17f, center = Offset(286f, 30f))
    drawRoundRect(
        color = Color(0xFFD4E8D4), topLeft = Offset(12f, 163f),
        size = Size(296f, 16f), cornerRadius = CornerRadius(8f)
    )
    val casaX = if (llegando) 208f else 24f
    dibujarCasa(casaX)
    dibujarPersona(x = if (llegando) 75f else 232f)
    // La posición de la casa y el movimiento distinguen llegada y salida.
    val inicio = if (llegando) 118f else 128f
    val fin = 188f
    drawLine(Color(0xFF245C75), Offset(inicio, 139f), Offset(fin, 139f), 5f, StrokeCap.Round)
    drawLine(Color(0xFF245C75), Offset(fin - 12f, 130f), Offset(fin, 139f), 5f, StrokeCap.Round)
    drawLine(Color(0xFF245C75), Offset(fin - 12f, 148f), Offset(fin, 139f), 5f, StrokeCap.Round)
}

private fun DrawScope.dibujarCasa(x: Float) {
    drawRoundRect(
        Color(0xFFF0D7B5), Offset(x, 72f), Size(80f, 92f), CornerRadius(6f)
    )
    val tejado = Path().apply {
        moveTo(x - 8f, 77f)
        lineTo(x + 40f, 34f)
        lineTo(x + 88f, 77f)
        close()
    }
    drawPath(tejado, Color(0xFFAA6453))
    drawRoundRect(
        Color(0xFF446984), Offset(x + 25f, 107f), Size(31f, 57f), CornerRadius(4f)
    )
    drawCircle(Color(0xFFF4C95D), 3f, Offset(x + 49f, 138f))
}

private fun DrawScope.dibujarPersona(x: Float) {
    val contorno = Color(0xFF304A62)
    val piel = Color(0xFFE5B58B)
    drawLine(contorno, Offset(x - 8f, 136f), Offset(x - 17f, 164f), 9f, StrokeCap.Round)
    drawLine(contorno, Offset(x + 8f, 136f), Offset(x + 18f, 164f), 9f, StrokeCap.Round)
    drawLine(contorno, Offset(x - 10f, 105f), Offset(x - 28f, 128f), 8f, StrokeCap.Round)
    // Brazo elevado y trazos de movimiento, compartidos por ambos saludos.
    drawLine(contorno, Offset(x + 12f, 105f), Offset(x + 26f, 87f), 8f, StrokeCap.Round)
    drawLine(piel, Offset(x + 26f, 87f), Offset(x + 25f, 68f), 8f, StrokeCap.Round)
    drawLine(contorno, Offset(x + 37f, 65f), Offset(x + 42f, 58f), 3f, StrokeCap.Round)
    drawLine(contorno, Offset(x + 40f, 76f), Offset(x + 48f, 74f), 3f, StrokeCap.Round)
    drawRoundRect(Color(0xFF4C8E99), Offset(x - 17f, 96f), Size(34f, 43f), CornerRadius(10f))
    drawCircle(piel, 18f, Offset(x, 75f))
    drawArc(
        contorno, 190f, 160f, false, Offset(x - 18f, 55f), Size(36f, 29f),
        style = Stroke(7f, cap = StrokeCap.Round)
    )
    drawCircle(contorno, 2f, Offset(x - 5f, 74f))
    drawCircle(contorno, 2f, Offset(x + 5f, 74f))
    drawArc(
        contorno, 15f, 150f, false, Offset(x - 6f, 77f), Size(12f, 8f),
        style = Stroke(2f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.dibujarPan() {
    drawOval(Color(0xFFDCE3E9), Offset(61f, 157f), Size(203f, 19f))
    rotate(-12f, pivot = Offset(160f, 111f)) {
        drawRoundRect(
            Color(0xFFD29A4B), Offset(62f, 66f), Size(196f, 94f), CornerRadius(47f)
        )
        drawRoundRect(
            Color(0xFF94602D), Offset(62f, 66f), Size(196f, 94f), CornerRadius(47f),
            style = Stroke(3f)
        )
        listOf(112f, 153f, 194f).forEach { x ->
            drawLine(Color(0xFFFFE1A5), Offset(x + 13f, 80f), Offset(x - 5f, 110f), 9f, StrokeCap.Round)
        }
    }
}

private fun DrawScope.dibujarManzana() {
    drawOval(Color(0xFFDCE3E9), Offset(99f, 169f), Size(122f, 15f))
    drawLine(Color(0xFF795238), Offset(159f, 70f), Offset(166f, 36f), 8f, StrokeCap.Round)
    val hoja = Path().apply {
        moveTo(164f, 51f)
        cubicTo(173f, 25f, 201f, 29f, 207f, 29f)
        cubicTo(199f, 51f, 180f, 61f, 164f, 51f)
        close()
    }
    drawPath(hoja, Color(0xFF4C8E59))
    val fruta = Path().apply {
        moveTo(160f, 71f)
        cubicTo(117f, 43f, 87f, 76f, 100f, 119f)
        cubicTo(110f, 163f, 131f, 182f, 160f, 168f)
        cubicTo(189f, 182f, 210f, 163f, 220f, 119f)
        cubicTo(233f, 76f, 203f, 43f, 160f, 71f)
        close()
    }
    drawPath(fruta, Color(0xFFD95353))
    drawPath(fruta, Color(0xFF9F363E), style = Stroke(3f))
    drawArc(
        Color(0xFFFFC4B5), 165f, 82f, false, Offset(114f, 82f), Size(28f, 50f),
        style = Stroke(7f, cap = StrokeCap.Round)
    )
}
