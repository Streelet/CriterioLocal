package com.example.criteriolocal.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = IosBlue,
    onPrimary = NeutralWhite,
    primaryContainer = NeutralFog,
    onPrimaryContainer = IosBlue,
    secondary = NeutralGraphite,
    onSecondary = NeutralWhite,
    secondaryContainer = NeutralFog,
    onSecondaryContainer = NeutralBlack,
    tertiary = NeutralSlate,
    onTertiary = NeutralWhite,
    background = NeutralWhite,
    onBackground = NeutralBlack,
    surface = NeutralWhite,
    onSurface = NeutralBlack,
    surfaceVariant = NeutralFog,
    onSurfaceVariant = NeutralSlate,
    surfaceContainer = NeutralCanvas,
    surfaceContainerHigh = NeutralFog,
    surfaceContainerHighest = NeutralMist,
    outline = NeutralMist,
    outlineVariant = NeutralFog,
    error = DangerRed,
    onError = NeutralWhite,
)

private val DarkColorScheme = darkColorScheme(
    primary = IosBlueDark,
    onPrimary = NeutralWhite,
    primaryContainer = DarkSurfaceElevated,
    onPrimaryContainer = IosBlueDark,
    secondary = NeutralStone,
    onSecondary = NeutralBlack,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = NeutralWhite,
    tertiary = NeutralStone,
    onTertiary = NeutralBlack,
    background = DarkBackground,
    onBackground = NeutralWhite,
    surface = DarkSurface,
    onSurface = NeutralWhite,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = NeutralStone,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurfaceElevated,
    surfaceContainerHighest = DarkOutline,
    outline = DarkOutline,
    outlineVariant = DarkSurfaceElevated,
    error = DangerRedDark,
    onError = NeutralWhite,
)

@Composable
fun CriterioLocalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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
        typography = Typography,
        content = content,
    )
}
