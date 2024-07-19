package io.future.laboratories.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color

public object StyleProvider {
    public var useGradient: Boolean by mutableStateOf(true)

    @Stable
    private val gradientList: List<Color>
        @Composable get() = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.tertiaryContainer,
        )

    @Stable
    private val solidGradientList: List<Color>
        @Composable get() = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary,
        )

    @Stable
    public val containerGradient: Brush
        @Composable
        get() = linearGradient(
            if (useGradient) gradientList else solidGradientList,
        )

    @Stable
    public val gradientTextColor: Color
        @Composable
        get() = if (useGradient) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primary


    @Stable
    public val onGradientTextColor: Color
        @Composable
        get() = if (useGradient) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary
}