package com.example.espalearn.preview


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Compacta",
    widthDp = 360,
    heightDp = 720
)
@Composable
private fun CompactaPreview() {
    NavegacionDemo()
}

@Preview(
    name = "Amplia",
    widthDp = 1000,
    heightDp = 700
)
@Composable
private fun AmpliaPreview() {
    NavegacionDemo()
}

@Preview(
    name = "Compacta — fuente grande",
    widthDp = 360,
    heightDp = 720,
    fontScale = 2f
)
@Composable
private fun CompactaFuenteGrandePreview() {
    NavegacionDemo()
}

@Preview(
    name = "Amplia — fuente grande",
    widthDp = 1000,
    heightDp = 700,
    fontScale = 2f
)
@Composable
private fun AmpliaFuenteGrandePreview() {
    NavegacionDemo()
}

@Preview(
    name = "Horizontal estrecha",
    widthDp = 720,
    heightDp = 360
)
@Composable
private fun HorizontalEstrechaPreview() {
    NavegacionDemo()
}