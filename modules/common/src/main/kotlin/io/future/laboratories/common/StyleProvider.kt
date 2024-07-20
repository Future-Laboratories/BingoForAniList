package io.future.laboratories.common

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color

public object StyleProvider {
    public var useGradient: Boolean by mutableStateOf(false)

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
    public val gradientColor: Color
        @Composable
        get() = if (useGradient) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.primary

    @Stable
    public val onGradientColor: Color
        @Composable
        get() = if (useGradient) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onPrimary

    @Stable
    public val switchColor: SwitchColors
        @Composable
        get() = if (useGradient) SwitchDefaults.colors().copy(
            checkedTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
            checkedThumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) else SwitchDefaults.colors()

    @Stable
    public val negativeButtonColors: ButtonColors
        @Composable
        get() = if (useGradient) ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) else ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
        )

    @Stable
    public val positiveButtonColors: ButtonColors
        @Composable
        get() = if (useGradient) ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ) else ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        )

    @Stable
    public val outLineTextColor: TextFieldColors
        @Composable
        get() = if (useGradient) OutlinedTextFieldDefaults.colors().copy(
            textSelectionColors = TextSelectionColors(
                handleColor = MaterialTheme.colorScheme.tertiaryContainer,
                backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
            focusedIndicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
            focusedLabelColor = MaterialTheme.colorScheme.tertiaryContainer,
        ) else OutlinedTextFieldDefaults.colors()
}