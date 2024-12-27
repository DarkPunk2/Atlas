package com.project.atlas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AtlasGreen,
    secondary = AtlasDarker,
    tertiary = AtlasGold,
    background = BackgroundBlack,
    surface = AtlasDarkGrey,
    onPrimary = SubtittleGrey,
    onSecondary = SubtittleGrey,
    onTertiary = Color.Black,
    onBackground = TextLightGrey,
    onSurface = TextLightGrey,
    secondaryContainer = darkItemBackground
)

private val LightColorScheme = lightColorScheme(
    primary = AtlasGreen,
    secondary = AtlasDarker,
    tertiary = SnowWhite,
    background = Color(0xFFFFFBFE), // Color claro para fondo
    surface = Color(0xFFFFFBFE),
    onPrimary = Black,
    onSecondary = Black,
    onTertiary = Color.Black,
    onBackground = Black, // Color oscuro para texto
    onSurface = Black,
    secondaryContainer = SnowWhite
)

@Composable
fun AtlasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Detecta tema oscuro del sistema
    dynamicColor: Boolean = true,               // Habilita colores dinámicos
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de personalizar `Typography`
        content = content
    )
}
