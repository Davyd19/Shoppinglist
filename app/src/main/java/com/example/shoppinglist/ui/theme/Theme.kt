package com.example.shoppinglist.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GreenToscaDark,
    onPrimary = GreenToscaOn,
    primaryContainer = GreenToscaDarkPrimaryContainer,
    onPrimaryContainer = GreenToscaDarkOnPrimaryContainer,
    secondary = GreenToscaSecondary,
    secondaryContainer = GreenToscaDarkSecondaryContainer,
    onSecondaryContainer = GreenToscaDarkOnSecondaryContainer,
    background = GreenToscaDarkBackground,
    onBackground = GreenToscaDarkOnBackground,
    surface = GreenToscaDarkSurface,
    onSurface = GreenToscaDarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = GreenToscaPrimary,
    onPrimary = GreenToscaOn,
    primaryContainer = GreenToscaPrimaryContainer,
    onPrimaryContainer = GreenToscaOnPrimaryContainer,
    secondary = GreenToscaSecondary,
    secondaryContainer = GreenToscaSecondaryContainer,
    onSecondaryContainer = GreenToscaOnSecondaryContainer,
    background = GreenToscaBackground,
    onBackground = GreenToscaOnBackground,
    surface = GreenToscaSurface,
    onSurface = GreenToscaOnSurface
)

@Composable
fun ShoppingListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}