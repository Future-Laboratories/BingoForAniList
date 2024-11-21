package io.future.laboratories.ui.components

import android.R.attr.maxLines
import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.future.laboratories.anilistapi.data.Media
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.ui.R
import io.future.laboratories.ui.animations.ShakingState
import io.future.laboratories.ui.animations.rememberShakingState
import io.future.laboratories.ui.animations.shakable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun AnimeBrowserItem(
    media: Media,
    modifier: Modifier = Modifier,
) {
    AnimeItemScaffold(
        imageData = { media.coverImage.large },
        actions = { },
        content = { expanded ->
            Column {
                Text(
                    text = media.title.userPreferred,
                    textDecoration = TextDecoration.Underline,
                )

                Text(
                    text = media.tags.joinToString(limit = if (expanded) -1 else 10) { it.name },
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                )
            }
        }
    )
}

@Composable
internal fun DiscoverRow(
    title: String,
    data: List<Media>,
) {
    Column {
        Searchbar()

        Text(text = title)

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(data) { it ->
                Text(text = it.title.userPreferred)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Searchbar() {
    DefaultSearchBar(
        query = "",
        onQueryChange = {},
        placeholderStringId = R.string.search_anime,
        trailingIcon = { Icon(Icons.Rounded.MoreVert, null) }
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 3,
    ) {
        DefaultSearchBar(
            modifier = Modifier.weight(1f),
            query = "",
            onQueryChange = {},
            placeholderStringId = R.string.search_anime,
        )

        LockedSegmentButton()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FlowRowScope.LockedSegmentButton() {
    val shakeState = rememberShakingState(
        strength = ShakingState.Strength.Normal,
        direction = ShakingState.Directions.LEFT_THEN_RIGHT,
    )
    val coroutineScope = rememberCoroutineScope()

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxRowHeight()) {
        SegmentedButton(
            modifier = Modifier.shakable(shakeState),
            shape = SegmentedButtonDefaults.itemShape(
                index = 0,
                count = 1
            ),
            onClick = {
                coroutineScope.launch {
                    shakeState.shake(50)
                }
            },
            colors = StyleProvider.SegmentedButtonColor,
            selected = true,
            icon = { Icon(Icons.Rounded.Lock, null) }
        ) {
            Text(FilterOptions.AND.name)
        }
    }
}