package io.future.laboratories.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.ui.pxValueToDp

@Composable
internal fun AnimeItemScaffold(
    modifier: Modifier = Modifier,
    imageData: () -> Any?,
    content: @Composable ColumnScope.(expanded: Boolean) -> Unit,
    actions: @Composable (RowScope.() -> Unit)? = null,
) {
    val localDensity = LocalDensity.current

    val minHeight by remember { mutableStateOf(148.dp) }
    var textHeight by remember { mutableStateOf(minHeight) }

    val animatedHeight by animateDpAsState(
        targetValue = textHeight,
        animationSpec = tween(150),
        label = "animatedImageHeight"
    )

    StyledContainer(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(modifier),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageData())
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = "Cover",
                modifier = Modifier
                    .height(animatedHeight.coerceAtLeast(minHeight))
                    .width(100.dp)
                    .clip(shape = RoundedCornerShape(if (StyleProvider.useCards) 0.dp else 12.dp)),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)

                        textHeight = placeable.height
                            .pxValueToDp(localDensity)
                            .coerceAtLeast(textHeight)

                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(0, 0)
                        }
                    }
                    .heightIn(minHeight),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                var expanded by remember { mutableStateOf(false) }

                content(expanded)

                if(actions != null) {
                    if (expanded) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                        ) {
                            actions()
                        }
                    }

                    Icon(
                        imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expanded = !expanded

                                // Set textHeight to minHeight if tolerance is reached
                                if (!expanded && textHeight - minHeight > 1.dp) {
                                    textHeight = minHeight
                                }
                            }
                    )
                }
            }
        }
    }
}