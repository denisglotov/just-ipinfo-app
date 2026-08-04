package org.dymka.justipinfo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme =
    darkColorScheme(
        primary = Blue80,
        onPrimary = PureBlack,
        primaryContainer = Blue40,
        onPrimaryContainer = PureWhite,
        secondary = Gray100,
        onSecondary = PureBlack,
        error = Red80,
        onError = PureBlack,
        background = PureBlack,
        onBackground = PureWhite,
        surface = PureBlack,
        onSurface = PureWhite,
        surfaceVariant = Gray900,
        onSurfaceVariant = Gray100,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Blue40,
        onPrimary = PureWhite,
        primaryContainer = Blue80,
        onPrimaryContainer = PureBlack,
        secondary = Gray900,
        onSecondary = PureWhite,
        error = Red40,
        onError = PureWhite,
        background = PureWhite,
        onBackground = PureBlack,
        surface = PureWhite,
        onSurface = PureBlack,
        surfaceVariant = Gray100,
        onSurfaceVariant = Gray900,
    )

@Composable
fun JustIpInfoTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
