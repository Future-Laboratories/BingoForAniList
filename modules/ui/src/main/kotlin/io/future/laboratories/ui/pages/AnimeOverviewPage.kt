package io.future.laboratories.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListCollection
import io.future.laboratories.anilistapi.data.MediaListStatus
import io.future.laboratories.anilistapi.data.MediaTag
import io.future.laboratories.anilistapi.data.ScoreFormat
import io.future.laboratories.common.BasicSaver
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.ui.Constants
import io.future.laboratories.ui.Constants.contentPaddingForFAB
import io.future.laboratories.ui.applyOperator
import io.future.laboratories.ui.components.AnimeHeader
import io.future.laboratories.ui.components.AnimeItem
import io.future.laboratories.ui.components.BooleanOption
import io.future.laboratories.ui.components.CustomPullToRefreshBox
import io.future.laboratories.ui.components.DropdownOption
import io.future.laboratories.ui.components.FilterOptions
import io.future.laboratories.ui.components.SearchBarWithModalBotttomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
public fun AnimeOverviewPage(
    bingoData: BingoData,
    showFinished: BooleanOption,
    showFAB: BooleanOption,
    pinned: DropdownOption,
    animeDataList: MediaListCollection?,
    mediaTags: List<MediaTag>?,
    scoreFormat: ScoreFormat,
    onRefresh: () -> Unit,
    onCommit: (scoreFormat: ScoreFormat, scoreValue: Float, animeData: MediaList, ratingValue: MediaListStatus, callback: (Float, MediaListStatus) -> Unit) -> Unit,
    onClickDelete: (bingoData: BingoData, animeData: MediaList) -> Unit,
    onSelectAnime: (bingoData: BingoData, animeData: MediaList) -> Unit,
    onFABClick: () -> Unit,
) {
    // Refreshing
    val state = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by rememberSaveable { mutableStateOf(false) }

    // Searchbar - Anime
    var animeQuery by rememberSaveable { mutableStateOf("") }

    // TagFilter
    val saver = BasicSaver(
        fromString = { string -> MediaTag(name = string) },
        toString = { mediaTag -> mediaTag.name },
    )

    @SuppressLint("MutableCollectionMutableState")
    val selectedTags by rememberSaveable(stateSaver = saver) { mutableStateOf(mutableStateListOf()) }
    var selectMode by rememberSaveable { mutableStateOf(FilterOptions.OR) }

    SearchBarWithModalBotttomSheet(
        query = animeQuery,
        onQueryChange = { query -> animeQuery = query },
        mediaTags = mediaTags,
        selectedTags = selectedTags,
        initialValue = { selectMode },
        onOptionChange = {
            selectMode = it
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    CustomPullToRefreshBox(
        state = state,
        onRefresh = {
            isRefreshing = true
            coroutineScope.launch {
                onRefresh()
                //Delay is needed for Indicator to disappear, TODO: check with future versions
                kotlinx.coroutines.delay(1000L)
                isRefreshing = false
            }
        },
        isRefreshing = isRefreshing,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPaddingForFAB,
            verticalArrangement = Constants.spacedByDefault,
        ) {
            animeDataList
                ?.lists
                ?.filter { if (showFinished.currentValue) true else it.name == "Watching" }
                ?.sortedByDescending { it.name == pinned.currentValue }
                .orEmpty()
                .forEach { animeList ->
                    stickyHeader(
                        key = animeList.name,
                        contentType = "Header",
                    ) {
                        AnimeHeader(title = animeList.name)
                    }

                    items(
                        items = animeList.entries
                            .filter {
                                it.media.title.userPreferred
                                    .contains(
                                        other = animeQuery,
                                        ignoreCase = true,
                                    ) && if (selectedTags.isEmpty()) {
                                    true
                                } else {
                                    selectedTags.applyOperator(selectMode) { tag -> tag in it.media.tags }
                                }
                            },
                    ) { animeData ->
                        AnimeItem(
                            animeData = animeData,
                            bingoData = bingoData.clone(),
                            scoreFormat = scoreFormat,
                            onCommit = { format, rating, status, save ->
                                onCommit(format, rating, animeData, status, save)
                            },
                            onClickDelete = onClickDelete,
                            onClick = onSelectAnime,
                        )
                    }
                }
        }

        if (showFAB.currentValue) {
            FloatingActionButton(
                modifier = Modifier
                    .width(64.dp)
                    .aspectRatio(1f)
                    .align(Alignment.BottomEnd),
                onClick = { onFABClick() },
                shape = CircleShape,
                containerColor = StyleProvider.gradientColor,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Explore,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.7f),
                )
            }
        }
    }
}
