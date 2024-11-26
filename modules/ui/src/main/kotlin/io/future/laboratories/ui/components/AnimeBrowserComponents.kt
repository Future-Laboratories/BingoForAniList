package io.future.laboratories.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.future.laboratories.anilistapi.data.Media
import io.future.laboratories.anilistapi.data.MediaFormat
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListStatus
import io.future.laboratories.anilistapi.data.MediaSeason
import io.future.laboratories.anilistapi.data.PageQueryParams
import io.future.laboratories.anilistapi.data.ScoreFormat
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.ui.R
import io.future.laboratories.ui.animations.ShakingState
import io.future.laboratories.ui.animations.rememberShakingState
import io.future.laboratories.ui.animations.shakable
import io.future.laboratories.ui.colon
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@Composable
internal fun AnimeBrowserItem(
    media: Media,
    modifier: Modifier = Modifier,
    onAddPressed: (id: Long) -> Unit,
) {
    AnimeItemScaffold(
        imageData = { media.coverImage.large },
        actions = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            ) {
                PositiveImageButton(
                    onClick = {
                        onAddPressed(media.id)
                    },
                    contentDescription = stringResource(id = R.string.delete),
                    imageVector = Icons.Rounded.Add,
                )
            }
        },
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
        Text(text = title)

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(data) { it ->
                Text(it.title.userPreferred)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun BrowserSearchbar(
    queryParams: PageQueryParams,
    onBottomSheetClose: () -> Unit,
) {
    var showModalBottomSheet by rememberSaveable { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(queryParams.search.value) }
    val coroutineScope = rememberCoroutineScope()
    var searchJob: Job? = null

    DefaultSearchBar(
        query = searchQuery.orEmpty(),
        onQueryChange = {
            searchQuery = if (it.isEmpty()) null else it
            queryParams.search.value = searchQuery


            onBottomSheetClose()
        },
        placeholderStringId = R.string.search_anime,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                modifier = Modifier.clickable {
                    showModalBottomSheet = true
                }
            )
        }
    )

    BrowserModalBottomSheet(
        visible = showModalBottomSheet,
        queryParams = queryParams,
        onDismissRequest = {
            showModalBottomSheet = false
            onBottomSheetClose()
        },
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class,
    ExperimentalLayoutApi::class,
)
@Composable
private fun BrowserModalBottomSheet(
    visible: Boolean,
    queryParams: PageQueryParams,
    onDismissRequest: () -> Unit,
) {
    val modalBottomSheetState = rememberModalBottomSheetState()

    if (visible) {
        ModalBottomSheet(
            modifier = Modifier.padding(bottom = 16.dp),
            onDismissRequest = onDismissRequest,
            sheetState = modalBottomSheetState,
            containerColor = StyleProvider.surfaceColor,
        ) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.Center,
                maxItemsInEachRow = 4,
            ) {
                DefaultBottomSheetRow(
                    text = stringResource(R.string.year).colon(),
                ) {
                    YearDropDown(queryParams)
                }

                DefaultBottomSheetRow(
                    text = stringResource(R.string.season).colon(),
                ) {
                    SeasonDropDown(queryParams)
                }

                DefaultBottomSheetRow(
                    text = stringResource(R.string.format).colon(),
                ) {
                    FormatDropDown(queryParams)
                }

                DefaultBottomSheetRow(
                    text = stringResource(R.string.operator_mode).colon(),
                ) {
                    LockedSegmentButton()
                }
            }
        }
    }
}

@Composable
private fun DefaultBottomSheetRow(
    text: String,
    content: @Composable () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, fontSize = 16.sp)

        content()
    }
}

@Composable
private fun YearDropDown(
    queryParams: PageQueryParams,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedYear by remember { mutableStateOf(queryParams.year.value) }

    Box {
        PositiveButton(
            onClick = { expanded = !expanded },
        ) {
            Text(
                text = if (selectedYear == null) {
                    stringResource(R.string.no_selection)
                } else {
                    "${stringResource(R.string.year).colon()} $selectedYear"
                },
            )
        }

        DropdownMenu(
            modifier = Modifier
                .height(240.dp)
                .background(StyleProvider.surfaceColor),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.no_selection))
                },
                onClick = {
                    selectedYear = null
                    queryParams.year.value = null
                    expanded = false
                },
            )

            for (year in LocalDateTime.now().year + 1 downTo 1950) {
                DropdownMenuItem(
                    text = { Text(text = "${stringResource(R.string.year).colon()} $year") },
                    onClick = {
                        selectedYear = year
                        queryParams.year.value = year
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SeasonDropDown(
    queryParams: PageQueryParams,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedSeason by remember { mutableStateOf(queryParams.season.value?.value ?: "") }

    Box {
        PositiveButton(
            onClick = { expanded = !expanded },
        ) {
            Text(
                text = if (selectedSeason.isEmpty()) stringResource(R.string.no_selection) else selectedSeason
            )
        }

        DropdownMenu(
            modifier = Modifier
                .height(240.dp)
                .background(StyleProvider.surfaceColor),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.no_selection))
                },
                onClick = {
                    selectedSeason = ""
                    queryParams.season.value = null
                    expanded = false
                }
            )

            MediaSeason.entries.forEach { season ->
                DropdownMenuItem(
                    text = { Text(text = season.value) },
                    onClick = {
                        selectedSeason = season.value
                        queryParams.season.value = season
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun FormatDropDown(
    queryParams: PageQueryParams,
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedFormats = remember<SnapshotStateList<MediaFormat>?> {
        mutableStateListOf<MediaFormat>(
            *(queryParams.format.value?.toTypedArray() ?: emptyArray())
        )
    }

    @Composable
    @ReadOnlyComposable
    fun getString(localSelectedFormats: List<MediaFormat>?): String {
        return when {
            localSelectedFormats == null -> stringResource(R.string.no_selection)
            localSelectedFormats.size == 1 -> localSelectedFormats.first().value
            localSelectedFormats.size > 1 -> "${localSelectedFormats.first().value} +${localSelectedFormats.size - 1}"
            else -> stringResource(R.string.no_selection)
        }
    }

    fun addOrRemoveFromList(format: MediaFormat) {
        var localSelectedFormats = selectedFormats

        when {
            localSelectedFormats == null -> selectedFormats = listOf(format).toMutableStateList()
            else -> {
                if (localSelectedFormats.contains(format)) {
                    localSelectedFormats -= format
                } else {
                    localSelectedFormats += format
                }
            }
        }

        queryParams.format.value =
            if (localSelectedFormats.isNullOrEmpty()) null else selectedFormats
    }

    Box {
        PositiveButton(
            onClick = { expanded = !expanded },
        ) {
            Text(text = getString(selectedFormats))
        }

        DropdownMenu(
            modifier = Modifier
                .height(240.dp)
                .background(StyleProvider.surfaceColor),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            MediaFormat.entries.forEach { format ->
                DropdownMenuItem(
                    text = { Text(text = format.value) },
                    onClick = {
                        addOrRemoveFromList(format)
                    },
                    leadingIcon = {
                        if (selectedFormats.orEmpty().contains(format)) Icon(
                            Icons.Rounded.Check,
                            null
                        )
                    },
                )
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LockedSegmentButton() {
    val shakeState = rememberShakingState(
        strength = ShakingState.Strength.Normal,
        direction = ShakingState.Directions.LEFT_THEN_RIGHT,
    )
    val coroutineScope = rememberCoroutineScope()

    SingleChoiceSegmentedButtonRow(modifier = Modifier) {
        SegmentedButton(
            modifier = Modifier.shakable(shakeState),
            shape = SegmentedButtonDefaults.itemShape(
                index = 0,
                count = 1,
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