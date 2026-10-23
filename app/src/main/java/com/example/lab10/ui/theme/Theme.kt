package com.example.lab10.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColors = darkColorScheme(
    primary = CinemaCoral, onPrimary = CinemaCoralInk,
    primaryContainer = Color(0xFF54241F), onPrimaryContainer = Color(0xFFFFDAD3),
    secondary = CinemaGold, onSecondary = Color(0xFF392D13),
    secondaryContainer = Color(0xFF413A2A), onSecondaryContainer = Color(0xFFF7E5C2),
    background = CinemaInk, onBackground = CinemaPaper,
    surface = CinemaInk, onSurface = CinemaPaper,
    surfaceVariant = CinemaSurface, onSurfaceVariant = CinemaMuted,
    surfaceContainer = CinemaSurface, surfaceContainerHigh = CinemaElevated,
    surfaceContainerLow = Color(0xFF18191E),
    outline = CinemaOutline, outlineVariant = Color(0xFF414148),
    error = Color(0xFFFFB4AB), onError = Color(0xFF690005),
    errorContainer = Color(0xFF52201F), onErrorContainer = Color(0xFFFFDAD6),
)
private val LightColors = lightColorScheme(
    primary = CinemaRed, onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD3), onPrimaryContainer = CinemaCoralInk,
    secondary = Color(0xFF725A25), onSecondary = Color.White,
    secondaryContainer = Color(0xFFF4E4BE), onSecondaryContainer = Color(0xFF30250F),
    background = CinemaPaper, onBackground = CinemaInk,
    surface = CinemaPaper, onSurface = CinemaInk,
    surfaceVariant = Color(0xFFECE7E1), onSurfaceVariant = Color(0xFF615C62),
    surfaceContainer = Color(0xFFFFFFFF), surfaceContainerHigh = Color(0xFFECE7E1),
    surfaceContainerLow = Color(0xFFF3EFEA),
    outline = Color(0xFF7A7378), outlineVariant = Color(0xFFD8D0CA),
    error = Color(0xFFAA251E), onError = Color.White,
    errorContainer = Color(0xFFFFDAD6), onErrorContainer = Color(0xFF410002),
)

@Composable
fun Lab10Theme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = !darkTheme
                    isAppearanceLightNavigationBars = !darkTheme
                }
            }
        }
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = Shapes(
            extraSmall = RoundedCornerShape(8.dp), small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp), large = RoundedCornerShape(24.dp),
            extraLarge = RoundedCornerShape(28.dp),
        ),
        content = content,
    )
}
