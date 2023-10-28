package io.future.laboratories.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
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

@Composable
internal fun AnimeItem(
    useCards: BooleanOption,
    animeData: MediaList,
    bingoData: BingoData,
    onClick: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    val content: @Composable Any.() -> Unit = {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
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
                    .heightIn(min = 140.dp)
                    .width(100.dp),
            )


            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = animeData.media.title.userPreferred,
                    textDecoration = TextDecoration.Underline,
                )

                Text(
                    text = animeData.media.tags.joinToString(limit = 10) { it.name },
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                )
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

@Composable
internal fun SheetItem(
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

