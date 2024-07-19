package io.future.laboratories.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListCollection
import io.future.laboratories.anilistapi.data.MediaTag
import io.future.laboratories.anilistapi.data.ScoreFormat
import io.future.laboratories.common.BasicSaver
import io.future.laboratories.common.BingoData
import io.future.laboratories.ui.Constants
import io.future.laboratories.ui.R
import io.future.laboratories.ui.components.AnimeHeader
import io.future.laboratories.ui.components.AnimeItem
import io.future.laboratories.ui.components.BooleanOption
import io.future.laboratories.ui.components.CustomPullToRefreshContainer
import io.future.laboratories.ui.components.DefaultSearchBar
import io.future.laboratories.ui.components.DropdownOption
import io.future.laboratories.ui.components.ModalBottomSheet

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
public fun AnimeOverviewPage(
    bingoData: BingoData,
    showFinished: BooleanOption,
    useCards: BooleanOption,
    pinned: DropdownOption,
    animeDataList: MediaListCollection?,
    mediaTags: List<MediaTag>?,
    scoreFormat: ScoreFormat,
    onRefresh: () -> Unit,
    onCommit: (scoreFormat: ScoreFormat, scoreValue: Float, animeData: MediaList, callback: (Float) -> Unit) -> Unit,
    onClickDelete: (bingoData: BingoData, animeData: MediaList) -> Unit,
    onSelectAnime: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    // Refreshing
    val state = rememberPullToRefreshState()
    if (state.isRefreshing) {
        LaunchedEffect(key1 = "refresh") {
            onRefresh()
            state.endRefresh()
        }
    }

    // Searchbar - Anime
    var animeQuery by rememberSaveable { mutableStateOf("") }
    var animeSearchActive by rememberSaveable { mutableStateOf(false) }

    // Modal
    var showModalBottomSheet by rememberSaveable { mutableStateOf(false) }

    // TagFilter
    val saver = BasicSaver(
        fromString = { string -> MediaTag(name = string) },
        toString = { mediaTag -> mediaTag.name },
    )

    @SuppressLint("MutableCollectionMutableState")
    val selectedTags by rememberSaveable(stateSaver = saver) { mutableStateOf(mutableStateListOf()) }

    DefaultSearchBar(
        query = animeQuery,
        onQueryChange = { query -> animeQuery = query },
        isSearchActive = animeSearchActive,
        onSearch = { active -> animeSearchActive = active },
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

    Spacer(modifier = Modifier.height(8.dp))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(state.nestedScrollConnection),
    ) {
        CustomPullToRefreshContainer(state = state)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                                    it.media.tags.any { tag -> tag in selectedTags }
                                }
                            },
                    ) { animeData ->
                        AnimeItem(
                            useCards = useCards,
                            animeData = animeData,
                            bingoData = bingoData.clone(),
                            scoreFormat = scoreFormat,
                            onCommit = { format, value, save ->
                                onCommit(format, value, animeData, save)
                            },
                            onClickDelete = onClickDelete,
                            onClick = onSelectAnime,
                        )
                    }
                }
        }
    }

    ModalBottomSheet(
        visible = showModalBottomSheet,
        mediaTags = mediaTags,
        selectedTags = selectedTags,
        onDismissRequest = { showModalBottomSheet = false },
    )
}
