package com.cropdoc.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary              = Green700,
    onPrimary            = White,
    primaryContainer     = Green100,
    onPrimaryContainer   = Green900,
    secondary            = LightGreen400,
    onSecondary          = Gray900,
    secondaryContainer   = Green50,
    onSecondaryContainer = Green800,
    tertiary             = SoilBrown,
    onTertiary           = White,
    tertiaryContainer    = SoilBrownLight,
    onTertiaryContainer  = SoilBrown,
    background           = OffWhite,
    onBackground         = Gray900,
    surface              = White,
    onSurface            = Gray900,
    surfaceVariant       = Green50,
    onSurfaceVariant     = Gray600,
    outline              = Green300,
    error                = ErrorRed,
    onError              = White,
)

private val DarkColorScheme = darkColorScheme(
    primary              = Green400,
    onPrimary            = Green900,
    primaryContainer     = Green800,
    onPrimaryContainer   = Green100,
    secondary            = LightGreen400,
    onSecondary          = Gray900,
    secondaryContainer   = Green900,
    onSecondaryContainer = Green200,
    tertiary             = SoilBrownLight,
    onTertiary           = SoilBrown,
    background           = androidx.compose.ui.graphics.Color(0xFF101510.toInt()),
    onBackground         = Green100,
    surface              = androidx.compose.ui.graphics.Color(0xFF1A211A.toInt()),
    onSurface            = Green100,
    surfaceVariant       = androidx.compose.ui.graphics.Color(0xFF1F2A1F.toInt()),
    onSurfaceVariant     = Green200,
    outline              = Green700,
    error                = androidx.compose.ui.graphics.Color(0xFFEF9A9A.toInt()),
    onError              = ErrorRed,
)

@Composable
fun CropDocTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}