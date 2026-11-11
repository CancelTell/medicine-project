package com.example.medicineproject.ui.theme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val PrimaryBlue = Color(0xFF2D5482)
private val SecondaryLightBlue = Color(0xFFD7DEEB)
private val DarkGray = Color(0xFF1F2937)
private val White = Color.White
private val LightGray = Color.Gray
private val VeryLightBlue = Color(0xFFD8DEEE)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = White,
    secondary = SecondaryLightBlue,
    onSecondary = PrimaryBlue,
    onSecondaryContainer = PrimaryBlue,
    surface = White,
    onSurface = DarkGray,
    outline = LightGray,
    background = White,
    onBackground = DarkGray
)

@Composable
fun MedicineAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}