package com.nextlayer3d.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = NlGreen,
    onPrimary = Color(0xFF04140C),
    primaryContainer = Color(0xFF15351F),
    onPrimaryContainer = NlGreen,
    secondary = NlSky,
    onSecondary = Color(0xFF042033),
    secondaryContainer = Color(0xFF123247),
    onSecondaryContainer = NlSky,
    tertiary = NlPurple,
    onTertiary = Color(0xFF260A3B),
    tertiaryContainer = Color(0xFF3A1A52),
    onTertiaryContainer = NlPurple,
    background = NlNearBlack,
    onBackground = Color(0xFFE7F0EA),
    surface = NlSurfaceDark,
    onSurface = Color(0xFFE7F0EA),
    surfaceVariant = NlSurfaceDarkElevated,
    onSurfaceVariant = Color(0xFFB7C4BC),
    outline = Color(0xFF4A5950),
    error = Color(0xFFF87171),
    onError = Color(0xFF3B0A0A),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF167C3C), // darker than NlGreen for AA contrast on white
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBFF2CF),
    onPrimaryContainer = Color(0xFF07340F),
    secondary = Color(0xFF0B74B0), // darker than NlSky for the same reason
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCEBFB),
    onSecondaryContainer = Color(0xFF02314B),
    tertiary = NlPurple,
    onTertiary = Color.White,
    // A real, clearly-purple container instead of M3's usual pale pastel
    // tint — the washed-out lilac read as "not actually one of our brand
    // colors" even though it was mathematically derived from NlPurple.
    tertiaryContainer = Color(0xFFDDA6F7),
    onTertiaryContainer = Color(0xFF34104A),
    background = NlOffWhite,
    onBackground = Color(0xFF14201A),
    surface = NlSurfaceLight,
    onSurface = Color(0xFF14201A),
    surfaceVariant = NlSurfaceLightDim,
    onSurfaceVariant = Color(0xFF3F4C44),
    outline = Color(0xFF7A8B81),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
)

/** Replaces the bare `MaterialTheme { }` the app started with — same role
 * names Material 3 components already read (primary/secondary/tertiary/
 * surface/etc.), just pointed at Nextlayer's actual palette instead of
 * the default baseline purple. Dark is the brand's "real" look (same
 * always-dark identity as the website and iOS app); light mode is a
 * genuinely separate, deliberately-adapted palette rather than a literal
 * inversion, so follows the system setting instead of forcing dark. */
@Composable
fun NextlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
