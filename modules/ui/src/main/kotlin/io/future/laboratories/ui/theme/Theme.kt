package io.future.laboratories.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = TertiaryLight,
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = TertiaryDark,

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
public fun AniListBingoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useCustomScheme: Boolean = true,
    colorPrimary: Color = PrimaryLight,
    colorSecondary: Color = SecondaryLight,
    colorTertiary: Color = TertiaryLight,
    colorError: Color = Color(red = 96, green = 20, blue = 16),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    fun getOnColor(color: Color): Color = if(color.luminance() > 0.5f) Color.Black else Color.White

    val colorScheme = when {
        dynamicColor && !useCustomScheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> if(useCustomScheme) darkColorScheme(
            primary = colorPrimary,
            secondary = colorSecondary,
            tertiary = colorTertiary,
            primaryContainer = colorPrimary.compositeOver(Color.White),
            secondaryContainer = colorSecondary.compositeOver(Color.White),
            tertiaryContainer = colorTertiary.compositeOver(Color.White),
            onPrimary = getOnColor(colorPrimary),
            onSecondary = getOnColor(colorSecondary),
            onTertiary = getOnColor(colorTertiary),
            onPrimaryContainer = colorPrimary.copy(alpha = 0.4f).compositeOver(Color.White),
            onSecondaryContainer = colorSecondary.copy(alpha = 0.4f).compositeOver(Color.White),
            onTertiaryContainer = colorTertiary.copy(alpha = 0.4f).compositeOver(Color.White),
            error = colorError,
        ) else DarkColorScheme

        else -> if(useCustomScheme) lightColorScheme(
            primary = colorPrimary,
            secondary = colorSecondary,
            tertiary = colorTertiary,
            primaryContainer = colorPrimary.compositeOver(Color.White),
            secondaryContainer = colorSecondary.compositeOver(Color.White),
            tertiaryContainer = colorTertiary.compositeOver(Color.White),
            onPrimary = getOnColor(colorPrimary),
            onSecondary = getOnColor(colorSecondary),
            onTertiary = getOnColor(colorTertiary),
            onPrimaryContainer = colorPrimary.compositeOver(Color.White),
            onSecondaryContainer = colorSecondary.compositeOver(Color.White),
            onTertiaryContainer = colorTertiary.compositeOver(Color.White),
            error = colorError,
        ) else LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}