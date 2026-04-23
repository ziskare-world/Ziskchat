package com.ziskchat.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = Color(0xFF0F766E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA8F0E0),
    onPrimaryContainer = Color(0xFF00201C),
    secondary = Color(0xFF1565C0),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6E3FF),
    onSecondaryContainer = Color(0xFF001B3F),
    tertiary = Color(0xFF5E35B1),
    background = Color(0xFFF4FBF9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE2F0ED),
    onSurface = Color(0xFF1A1C1C),
    onSurfaceVariant = Color(0xFF444948),
    outline = Color(0xFF747A79)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7FE8D4),
    onPrimary = Color(0xFF003731),
    primaryContainer = Color(0xFF005148),
    onPrimaryContainer = Color(0xFFA8F0E0),
    secondary = Color(0xFFA7C8FF),
    onSecondary = Color(0xFF002F67),
    secondaryContainer = Color(0xFF00458F),
    onSecondaryContainer = Color(0xFFD6E3FF),
    tertiary = Color(0xFFD0BCFF),
    background = Color(0xFF091513),
    surface = Color(0xFF101A19),
    surfaceVariant = Color(0xFF23312E),
    onSurface = Color(0xFFE7E3E2),
    onSurfaceVariant = Color(0xFFC2C8C6),
    outline = Color(0xFF8B9390)
)

@Immutable
data class ExtendedColors(
    val appBackground: Color,
    val chatIncoming: Color,
    val chatOutgoing: Color,
    val success: Color,
    val warning: Color,
    val danger: Color,
    val online: Color
)

private val LightExtendedColors = ExtendedColors(
    appBackground = Color(0xFFF0F8F6),
    chatIncoming = Color(0xFFFFFFFF),
    chatOutgoing = Color(0xFFD8FAF1),
    success = Color(0xFF128C7E),
    warning = Color(0xFFF4A62A),
    danger = Color(0xFFD64B4B),
    online = Color(0xFF18B47B)
)

private val DarkExtendedColors = ExtendedColors(
    appBackground = Color(0xFF08110F),
    chatIncoming = Color(0xFF14201D),
    chatOutgoing = Color(0xFF103A35),
    success = Color(0xFF55D6BE),
    warning = Color(0xFFF7BC5A),
    danger = Color(0xFFFF8C8C),
    online = Color(0xFF5AE8A5)
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

private val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)

private val AppShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(36.dp)
)

@Composable
fun ZiskChatAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme
    val extendedColors: ExtendedColors
    if (darkTheme) {
        colorScheme = DarkColors
        extendedColors = DarkExtendedColors
    } else {
        colorScheme = LightColors
        extendedColors = LightExtendedColors
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

object ZiskChatTheme {
    val extendedColors: ExtendedColors
        @Composable get() = LocalExtendedColors.current
}
