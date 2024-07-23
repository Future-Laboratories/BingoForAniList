package io.future.laboratories.ui.components

import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListStatus
import io.future.laboratories.anilistapi.data.MediaTag
import io.future.laboratories.anilistapi.data.ScoreFormat
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.common.StyleProvider.DefaultContainer
import io.future.laboratories.ui.Constants
import io.future.laboratories.ui.R
import io.future.laboratories.ui.pxValueToDp
import io.future.laboratories.ui.toTriple

@Composable
internal fun AnimeItem(
    useCards: BooleanOption,
    animeData: MediaList,
    bingoData: BingoData,
    scoreFormat: ScoreFormat,
    onCommit: (scoreFormat: ScoreFormat, scoreValue: Float, (Float) -> Unit) -> Unit,
    onClickDelete: (bingoData: BingoData, animeData: MediaList) -> Unit,
    onClick: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    val localDensity = LocalDensity.current
    val minHeight = 148.dp

    var textHeight by remember { mutableStateOf(minHeight) }
    var expended by remember { mutableStateOf(false) }

    val animatedHeight by animateDpAsState(
        targetValue = textHeight,
        animationSpec = tween(150),
        label = "animatedImageHeight"
    )

    DefaultContainer(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                onClick(bingoData, animeData)
            },
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(animeData.media.coverImage.large)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = "Cover",
                modifier = Modifier
                    .height(animatedHeight.coerceAtLeast(minHeight))
                    .width(100.dp)
                    .clip(shape = RoundedCornerShape(if (useCards.currentValue) 0.dp else 12.dp)),
            )

            Column(
                modifier = Modifier
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
                Column {
                    Text(
                        text = animeData.media.title.userPreferred,
                        textDecoration = TextDecoration.Underline,
                    )

                    Text(
                        text = animeData.media.tags.joinToString(limit = if (expended) -1 else 10) { it.name },
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                    )
                }

                if (expended) {
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
                }

                Icon(
                    imageVector = if (expended) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expended = !expended

                            if (!expended) {
                                textHeight = minHeight
                            }
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BoxScope.CustomPullToRefreshContainer(
    state: PullToRefreshState,
) {
    val targetAlpha by remember {
        derivedStateOf { if (state.progress >= 1f) 1f else 0.1f }
    }
    val alphaState by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(150),
        label = "refresh"
    )

    PullToRefreshContainer(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .zIndex(1f),
        containerColor = PullToRefreshDefaults.containerColor.copy(alpha = alphaState),
        state = state,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchBarWithModalBotttomSheet(
    query: String,
    onQueryChange: (query: String) -> Unit,
    mediaTags: List<MediaTag>?,
    selectedTags: SnapshotStateList<MediaTag>,
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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (query: String) -> Unit,
    @StringRes placeholderStringId: Int,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    var interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val shape by animateIntAsState((if (isFocused) 10 else 50), label = "cornerAnimation")

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        value = query,
        onValueChange = onQueryChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
        trailingIcon = trailingIcon,
        placeholder = { Text(stringResource(id = placeholderStringId)) },
        colors = StyleProvider.outLineTextColor,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(shape),
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
) {
    val modalBottomSheetState = rememberModalBottomSheetState()
    val preFilteredTags = mediaTags?.filterNot { it.isAdult }

    // Searchbar - Tags
    var tagQuery by rememberSaveable { mutableStateOf("") }

    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = modalBottomSheetState,
        ) {
            DefaultSearchBar(
                modifier = Modifier.padding(horizontal = 8.dp),
                query = tagQuery,
                onQueryChange = { query -> tagQuery = query },
                placeholderStringId = R.string.search_tag,
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
    onCommit: (scoreFormat: ScoreFormat, scoreValue: Float, (Float) -> Unit) -> Unit,
) {
    var showRatingDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var scoreValue by rememberSaveable {
        mutableFloatStateOf(0f)
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
                    .padding(bottom = 24.dp)
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
                    ) {
                        Row(
                            horizontalArrangement = Constants.spacedByDefault,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(
                                    id = R.string.rating_body,
                                    formatArgs = arrayOf(animeData.media.title.userPreferred + animeData.media.title.userPreferred),
                                )
                            )
                        }

                        Rating(
                            defaultValue = animeData.score,
                            scoreFormat = scoreFormat,
                            onValueChange = { scoreValue = it }
                        )

                        StatusDropDown(animeData.status)
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
                            onCommit(scoreFormat, scoreValue) {
                                animeData.score = it
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

    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        MediaListStatus.entries
            .filterNot { it == MediaListStatus.NONE }

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
            ScoreFormat.POINT_10 -> RatingSlider(
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

    Text(text = formatString.format(value))

    PositiveImageButton(
        onClick = { onValueChange((value - valueSteps).coerceIn(valueRange)) },
        contentDescription = "null",
        imageVector = Icons.Rounded.Remove,
    )

    Slider(
        value = value,
        modifier = Modifier.weight(1f),
        onValueChange = onValueChange,
        steps = if (scoreFormat != ScoreFormat.POINT_10_DECIMAL) valueRange.endInclusive.toInt() - 1 else 0,
        valueRange = valueRange,
    )

    PositiveImageButton(
        onClick = { onValueChange((value + valueSteps).coerceIn(valueRange)) },
        contentDescription = "null",
        imageVector = Icons.Rounded.Add,
    )
}

@Composable
private fun RowScope.RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit
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
                    vertical = 6.dp
                ),
                color = StyleProvider.onGradientColor,
                fontSize = 18.sp,
            )
        }

        DefaultDivider(front = false)
    }
}