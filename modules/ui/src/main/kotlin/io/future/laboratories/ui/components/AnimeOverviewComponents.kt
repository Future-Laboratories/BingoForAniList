package io.future.laboratories.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaTag
import io.future.laboratories.common.BingoData
import io.future.laboratories.ui.EvenRoundedCornersTransformation
import io.future.laboratories.ui.R
import io.future.laboratories.ui.pxValueToDp

@Composable
internal fun AnimeItem(
    useCards: BooleanOption,
    animeData: MediaList,
    bingoData: BingoData,
    onClick: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    var textHeight by remember { mutableIntStateOf(0) }
    var extended by remember { mutableStateOf(false) }

    val minHeight = 148.dp
    val animatedHeight by animateIntAsState(
        targetValue = textHeight,
        animationSpec = tween(150),
        label = "animatedImageHeight"
    )

    val content: @Composable Any.() -> Unit = {
        Row(
            modifier = Modifier.animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(animeData.media.coverImage.large)
                    .transformations(
                        if (useCards.currentValue) {
                            RoundedCornersTransformation()
                        } else {
                            EvenRoundedCornersTransformation(all = 30f)
                        },
                    )
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = "Cover",
                modifier = Modifier
                    .height(
                        animatedHeight
                            .pxValueToDp()
                            .coerceAtLeast(minHeight)
                    )
                    .width(100.dp),
            )

            Column(
                modifier = Modifier
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        textHeight = placeable.height.coerceAtLeast(textHeight)

                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative(0, 0)
                        }
                    }
                    .heightIn(minHeight),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier) {
                    Text(
                        text = animeData.media.title.userPreferred,
                        textDecoration = TextDecoration.Underline,
                    )

                    Text(
                        text = animeData.media.tags.joinToString(limit = if (extended) -1 else 10) { it.name },
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                    )
                }

                if (animeData.media.tags.count() > 10) {
                    Icon(
                        imageVector = if (extended) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                extended = !extended

                                if (!extended) {
                                    textHeight = 0
                                }
                            }
                    )
                }
            }
        }
    }

    val modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick(bingoData, animeData)
        }

    if (useCards.currentValue) {
        Card(
            modifier = modifier,
            content = content,
        )
    } else {
        Box(
            modifier = modifier,
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DefaultSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (query: String) -> Unit,
    isSearchActive: Boolean,
    onSearch: (active: Boolean) -> Unit,
    @StringRes placeholderStringId: Int,
    trailingIcon: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit = { },
) {
    SearchBar(
        modifier = Modifier
            .heightIn(max = 68.dp)
            .fillMaxWidth()
            .then(modifier),
        query = query,
        onQueryChange = onQueryChange,
        onSearch = { onSearch(false) },
        active = isSearchActive,
        onActiveChange = onSearch,
        placeholder = { Text(stringResource(id = placeholderStringId)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
        trailingIcon = trailingIcon,
        content = content
        // TODO: Implement recommendations?
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun ModalBottomSheet(
    visible: Boolean,
    mediaTags: List<MediaTag>?,
    selectedTags: SnapshotStateList<MediaTag>,
    onDismissRequest: () -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    // Searchbar - Tags
    var tagQuery by rememberSaveable { mutableStateOf("") }
    var tagSearchActive by rememberSaveable { mutableStateOf(false) }

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = modalBottomSheetState,
        ) {
            DefaultSearchBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                query = tagQuery,
                onQueryChange = { query -> tagQuery = query },
                isSearchActive = tagSearchActive,
                onSearch = { active -> tagSearchActive = active },
                placeholderStringId = R.string.search_tag,
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                for (letter in 'a'..'z') {
                    stickyHeader {
                        DefaultHeader(title = letter.uppercase())
                    }

                    items(items = mediaTags
                        .orEmpty()
                        .filter {
                            it.name.startsWith(letter, ignoreCase = true) && it.name.contains(
                                other = tagQuery,
                                ignoreCase = true,
                            )
                        }
                    ) { tag ->
                        SheetItem(
                            item = tag,
                            initialValue = tag in selectedTags,
                            onClick = { change, value ->
                                selectedTags.apply {
                                    if (change) add(value) else remove(value)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SheetItem(
    item: MediaTag,
    initialValue: Boolean,
    onClick: (isChecked: Boolean, tag: MediaTag) -> Unit,
) {
    var checked by rememberSaveable { mutableStateOf(initialValue) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = { value ->
                checked = !checked
                onClick(value, item)
            }
        )

        Text(text = item.name)
    }
}
