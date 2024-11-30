package io.future.laboratories.ui.components

import android.R.attr.text
import android.media.Rating
import android.widget.RatingBar
import androidx.annotation.FloatRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.SentimentNeutral
import androidx.compose.material.icons.rounded.SentimentVeryDissatisfied
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListStatus
import io.future.laboratories.anilistapi.data.MediaTag
import io.future.laboratories.anilistapi.data.ScoreFormat
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.ui.Constants
import io.future.laboratories.ui.R
import io.future.laboratories.ui.toTriple

@Composable
internal fun AnimeItem(
    animeData: MediaList,
    bingoData: BingoData,
    scoreFormat: ScoreFormat,
    onCommit: (scoreFormat: ScoreFormat, scoreValue: Float, ratingValue: MediaListStatus, (Float, MediaListStatus) -> Unit) -> Unit,
    onClickDelete: (bingoData: BingoData, animeData: MediaList) -> Unit,
    onClick: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    AnimeItemScaffold(
        imageData = { animeData.media.coverImage.large },
        modifier = Modifier.clickable {
            onClick(bingoData, animeData)
        },
        content = { expanded ->
            Column {
                Text(
                    text = animeData.media.title.userPreferred,
                    textDecoration = TextDecoration.Underline,
                )

                Text(
                    text = animeData.media.tags.joinToString(limit = if (expanded) -1 else 10) { it.name },
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                )
            }
        },
        actions = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            ) {
                RatingDialog(
                    animeData = animeData,
                    scoreFormat = scoreFormat,
                    onCommit = onCommit,
                )

                DeleteDialog(
                    bingoData = bingoData,
                    animeData = animeData,
                    onDelete = onClickDelete,
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CustomPullToRefreshBox(
    state: PullToRefreshState,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    content: @Composable BoxScope.() -> Unit,
) {
    PullToRefreshBox(
        onRefresh = onRefresh,
        isRefreshing = isRefreshing,
        state = state,
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchBarWithModalBotttomSheet(
    query: String,
    onQueryChange: (query: String) -> Unit,
    mediaTags: List<MediaTag>?,
    selectedTags: SnapshotStateList<MediaTag>,
    initialValue: () -> FilterOptions,
    onOptionChange: (filterOption: FilterOptions) -> Unit,
) {
    // Modal
    var showModalBottomSheet by rememberSaveable { mutableStateOf(false) }

    DefaultSearchBar(
        query = query,
        onQueryChange = onQueryChange,
        placeholderStringId = R.string.search_anime,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                modifier = Modifier.clickable {
                    showModalBottomSheet = true
                }
            )
        },
    )

    ModalBottomSheet(
        visible = showModalBottomSheet,
        mediaTags = mediaTags,
        selectedTags = selectedTags,
        onDismissRequest = { showModalBottomSheet = false },
        initialValue = initialValue,
        onOptionChange = onOptionChange
    )
}

@Composable
private fun DeleteDialog(
    bingoData: BingoData,
    animeData: MediaList,
    onDelete: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }

    NegativeImageButton(
        onClick = { showDeleteDialog = true },
        contentDescription = stringResource(id = R.string.delete),
        imageVector = Icons.Rounded.Delete,
    )

    if (showDeleteDialog) {
        DefaultWarningDialog(
            header = stringResource(id = R.string.delete_permanently_header),
            body = stringResource(id = R.string.delete_permanently_body),
            actionButtonText = stringResource(id = R.string.delete),
            abortText = stringResource(id = android.R.string.cancel),
            onDismiss = { showDeleteDialog = false },
            onAction = { onDelete(bingoData, animeData) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ModalBottomSheet(
    visible: Boolean,
    mediaTags: List<MediaTag>?,
    selectedTags: SnapshotStateList<MediaTag>,
    onDismissRequest: () -> Unit,
    initialValue: () -> FilterOptions,
    onOptionChange: (filterOption: FilterOptions) -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val preFilteredTags = mediaTags?.filterNot { it.isAdult }

    // Searchbar - Tags
    var tagQuery by rememberSaveable { mutableStateOf("") }

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = modalBottomSheetState,
            containerColor = StyleProvider.surfaceColor,
        ) {
            DefaultSearchBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                query = tagQuery,
                onQueryChange = { query -> tagQuery = query },
                placeholderStringId = R.string.search_tag,
            )

            Spacer(Modifier.size(8.dp))

            SingleChoiceSegmentedButtonRow(
                initialValue = initialValue,
                onOptionChange = onOptionChange,
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                verticalArrangement = Constants.spacedByDefault,
            ) {
                for (letter in 'a'..'z') {
                    stickyHeader {
                        DefaultHeader(title = letter.uppercase())
                    }

                    items(
                        items = preFilteredTags
                            .orEmpty()
                            .filter {
                                it.name.startsWith(letter, ignoreCase = true) && it.name.contains(
                                    other = tagQuery,
                                    ignoreCase = true,
                                )
                            },
                    ) { tag ->
                        val isSelected by rememberSaveable { mutableStateOf(tag in selectedTags) }

                        SheetItem(
                            item = tag,
                            initialValue = isSelected,
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
@OptIn(ExperimentalMaterial3Api::class)
private fun SingleChoiceSegmentedButtonRow(
    initialValue: () -> FilterOptions,
    onOptionChange: (filterOption: FilterOptions) -> Unit = {},
) {
    var selectedIndex by remember { mutableIntStateOf(initialValue().ordinal) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        SingleChoiceSegmentedButtonRow {
            FilterOptions.entries.forEachIndexed { index, option ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = FilterOptions.entries.size
                    ),
                    onClick = {
                        selectedIndex = index
                        onOptionChange(option)
                    },
                    colors = StyleProvider.SegmentedButtonColor,
                    selected = index == selectedIndex,
                ) {
                    Text(option.name)
                }
            }
        }
    }
}

internal enum class FilterOptions {
    OR,
    AND,
    XOR,
    NOT,
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
            },
            colors = StyleProvider.checkBoxColor,
        )

        Text(text = item.name)
    }
}

@Composable
private fun RatingDialog(
    animeData: MediaList,
    scoreFormat: ScoreFormat,
    onCommit: (scoreFormat: ScoreFormat, scoreValue: Float, ratingValue: MediaListStatus, (Float, MediaListStatus) -> Unit) -> Unit,
) {
    var showRatingDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var scoreValue by rememberSaveable {
        mutableFloatStateOf(0f)
    }
    var ratingValue by rememberSaveable {
        mutableStateOf(animeData.status)
    }

    PositiveImageButton(
        onClick = { showRatingDialog = true },
        contentDescription = stringResource(id = R.string.delete),
        imageVector = Icons.Rounded.Star,
    )

    if (showRatingDialog) {
        Dialog(
            onDismissRequest = { showRatingDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.8f)
                    .padding(bottom = 24.dp),
                colors = StyleProvider.cardColor,
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Constants.spacedByDefault,
                ) {
                    DefaultHeader(title = stringResource(id = R.string.rating_header))

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(weight = 1f, fill = false),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            border = BorderStroke(2.dp, StyleProvider.containerGradient),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                horizontalArrangement = Constants.spacedByDefault,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = stringResource(
                                        id = R.string.rating_body,
                                        formatArgs = arrayOf(animeData.media.title.userPreferred),
                                    )
                                )
                            }
                        }

                        OutlinedCard(
                            shape = MaterialTheme.shapes.large,
                            border = BorderStroke(2.dp, StyleProvider.containerGradient),
                        ) {
                            Column(
                                modifier = Modifier.padding(all = 8.dp)
                            ) {
                                Rating(
                                    defaultValue = animeData.score,
                                    scoreFormat = scoreFormat,
                                    onValueChange = { scoreValue = it }
                                )

                                StatusDropDown(
                                    status = animeData.status,
                                    onValueChange = { status -> ratingValue = status }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                    ) {
                        PositiveButton(onClick = {
                            showRatingDialog = false
                        }) {
                            Text(text = stringResource(id = android.R.string.cancel))
                        }

                        NegativeButton(onClick = {
                            onCommit(scoreFormat, scoreValue, ratingValue) { score, status ->
                                animeData.score = score
                                animeData.status = status
                            }
                            showRatingDialog = false
                        }) {
                            Text(text = stringResource(id = R.string.commit))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusDropDown(
    status: MediaListStatus,
    onValueChange: (MediaListStatus) -> Unit,
) {
    var showMenu by rememberSaveable {
        mutableStateOf(true)
    }

    OptionDropdown(
        optionName = "Status",
        values = MediaListStatus.entries
            .filterNot { it == MediaListStatus.NONE }
            .map { it.value }
            .associateWith { it },
        initialValue = status.value,
    ) {
        onValueChange(MediaListStatus.entries.first{entry -> entry.value == it})
    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        MediaListStatus.entries.filterNot { it == MediaListStatus.NONE }
    }
}

@Composable
private fun Rating(
    defaultValue: Float,
    scoreFormat: ScoreFormat,
    onValueChange: (Float) -> Unit,
) {
    var value by remember {
        mutableFloatStateOf(defaultValue)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
    ) {
        when (scoreFormat) {
            ScoreFormat.POINT_100,
            ScoreFormat.POINT_10_DECIMAL,
            ScoreFormat.POINT_10, -> RatingSlider(
                value = value,
                scoreFormat = scoreFormat,
                onValueChange = {
                    value = it

                    onValueChange(it)
                },
            )

            ScoreFormat.POINT_5 -> RatingBar(
                rating = value,
                onRatingChanged = {
                    value = it

                    onValueChange(it)
                },
            )

            ScoreFormat.POINT_3 -> EmojiRepresentation(
                rating = value,
                onRatingChanged = {
                    value = it

                    onValueChange(it)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowScope.RatingSlider(
    value: Float,
    scoreFormat: ScoreFormat,
    onValueChange: (Float) -> Unit,
) {
    val (valueRange, valueSteps, formatString) = when (scoreFormat) {
        ScoreFormat.POINT_100 -> 0f..100f to 1f toTriple "%03.0f"
        ScoreFormat.POINT_10_DECIMAL -> 0f..10f to 0.1f toTriple "%04.1f"
        ScoreFormat.POINT_10 -> 0f..10f to 1f toTriple "%02.0f"
        else -> return
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        ) {
            PositiveImageButton(
                onClick = { onValueChange((value - valueSteps).coerceIn(valueRange)) },
                contentDescription = "null",
                size = 32.dp,
                imageVector = Icons.Rounded.Remove,
            )

            Slider(
                value = value,
                modifier = Modifier.weight(1f),
                onValueChange = onValueChange,
                steps = if (scoreFormat != ScoreFormat.POINT_10_DECIMAL) valueRange.endInclusive.toInt() - 1 else 0,
                valueRange = valueRange,
                colors = StyleProvider.sliderColors,
            )

            PositiveImageButton(
                onClick = { onValueChange((value + valueSteps).coerceIn(valueRange)) },
                contentDescription = "null",
                size = 32.dp,
                imageVector = Icons.Rounded.Add,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        ) {
            OutlinedCard(
                shape = MaterialTheme.shapes.large,
                border = BorderStroke(2.dp, StyleProvider.containerGradient),
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 2.dp),
                    text = formatString.format(value),
                    fontSize = 20.sp,
                )
            }
        }
    }
}

@Composable
private fun RowScope.RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
) {
    for (i in 1..5) {
        IconButton(onClick = { onRatingChanged(i.toFloat()) }) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun RowScope.EmojiRepresentation(
    @FloatRange(from = 0.0, to = 3.0)
    rating: Float,
    onRatingChanged: (Float) -> Unit,
) {
    listOf(
        Icons.Rounded.SentimentVeryDissatisfied to Color.Red,
        Icons.Rounded.SentimentNeutral to MaterialTheme.colorScheme.onSurface,
        Icons.Rounded.SentimentVerySatisfied to Color.Green,
    ).forEachIndexed { index, buttonData ->
        IconButton(onClick = { onRatingChanged(index.toFloat()) }) {
            Icon(
                imageVector = buttonData.first,
                contentDescription = null,
                tint = if (rating == index.toFloat()) buttonData.second else MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.4f
                ),
            )
        }
    }
}

@Composable
internal fun AnimeHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        DefaultDivider(front = false)

        OutlinedCard(
            modifier = Modifier.padding(4.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.elevatedCardColors().copy(
                containerColor = StyleProvider.gradientColor.copy(alpha = 0.8f),
            ),
            border = BorderStroke(2.dp, StyleProvider.containerGradient),
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp,
                ),
                color = StyleProvider.onGradientColor,
                fontSize = 18.sp,
            )
        }

        DefaultDivider(front = false)
    }
}