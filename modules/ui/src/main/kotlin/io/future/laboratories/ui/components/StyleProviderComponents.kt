package io.future.laboratories.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import io.future.laboratories.common.StyleProvider

@Stable
@Composable
internal fun StyledContainer(
    modifier: Modifier,
    content: @Composable Any.() -> Unit,
) = if (StyleProvider.useCards) {
    ElevatedCard(
        modifier = modifier,
        content = content,
        colors = StyleProvider.cardColor,
    )
} else {
    Box(
        modifier = modifier,
        content = content,
    )
}