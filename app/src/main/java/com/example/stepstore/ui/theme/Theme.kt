package com.example.stepstore.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = Primario,
    onPrimary = Color.White,
    secondary = Acento,
    onSecondary = Color.White,
    background = FondoTarjetas,
    onBackground = Primario,
    surface = Color.White,
    onSurface = Primario,
    surfaceVariant = FondoTarjetas,
    onSurfaceVariant = TextoSecundario,
    error = Alerta,
    onError = Color.White
)

@Composable
fun StepStoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaClaro,
        typography = Typography,
        content = content
    )
}