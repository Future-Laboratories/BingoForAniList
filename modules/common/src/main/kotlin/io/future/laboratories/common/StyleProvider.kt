package io.future.laboratories.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import io.future.laboratories.common.StyleProvider.useCards

public object StyleProvider {
    public var useGradient: Boolean by mutableStateOf(false)
    public var useCards: Boolean by mutableStateOf(true)

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
        get() = if (useGradient) SwitchDefaults.colors(
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
    public val checkBoxColor: CheckboxColors
        @Composable
        get() = if (useGradient) CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colorScheme.tertiaryContainer,
            checkmarkColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        else CheckboxDefaults.colors()

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
            cursorColor = MaterialTheme.colorScheme.tertiaryContainer,
        ) else OutlinedTextFieldDefaults.colors()

    @Stable
    public val sliderColors: SliderColors
        @Composable
        get() = if (useGradient) SliderDefaults.colors(
            activeTrackColor = MaterialTheme.colorScheme.tertiaryContainer,
            thumbColor = MaterialTheme.colorScheme.tertiaryContainer,
        ) else SliderDefaults.colors()

    @Stable
    @Composable
    public fun DefaultContainer(
        modifier: Modifier,
        content: @Composable Any.() -> Unit,
    ): Unit = DefaultContainer(
        useCards = useCards,
        modifier = modifier,
        content = content,
    )
}

@Stable
@Composable
private fun DefaultContainer(
    useCards: Boolean,
    modifier: Modifier,
    content: @Composable Any.() -> Unit,
) = if (useCards) {
    ElevatedCard(
        modifier = modifier,
        content = content,
    )
} else {
    Box(
        modifier = modifier,
        content = content,
    )
}