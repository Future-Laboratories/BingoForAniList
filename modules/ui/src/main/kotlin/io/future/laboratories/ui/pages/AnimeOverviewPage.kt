package io.future.laboratories.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListCollection
import io.future.laboratories.anilistapi.data.MediaTag
import io.future.laboratories.common.BingoData
import io.future.laboratories.ui.R
import io.future.laboratories.ui.components.AnimeItem
import io.future.laboratories.ui.components.BooleanOption
import io.future.laboratories.ui.components.DefaultHeader
import io.future.laboratories.ui.components.DefaultSearchBar
import io.future.laboratories.ui.components.DropdownOption
import io.future.laboratories.ui.components.ModalBottomSheet

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun AnimeOverviewPage(
    bingoData: BingoData,
    showFinished: BooleanOption,
    useCards: BooleanOption,
    pinned: DropdownOption,
    animeDataList: MediaListCollection?,
    mediaTags: List<MediaTag>?,
    onClickDelete: (bingoData: BingoData, animeData: MediaList) -> Unit,
    onSelectAnime: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    // Searchbar - Anime
    var animeQuery by rememberSaveable { mutableStateOf("") }
    var animeSearchActive by rememberSaveable { mutableStateOf(false) }

    // Modal
    var showModalBottomSheet by rememberSaveable { mutableStateOf(false) }

    // TagFilter
    val saver = object : Saver<SnapshotStateList<MediaTag>, String> {
        private val separator = ", "
        override fun restore(value: String): SnapshotStateList<MediaTag> {
            return value
                .split(separator)
                .filter { it.isNotBlank() }
                .map { MediaTag(name = it) }
                .toMutableStateList()
        }

        override fun SaverScope.save(value: SnapshotStateList<MediaTag>): String {
            return value.joinToString(separator = separator) { it.name }
        }
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
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
                        DefaultHeader(title = animeList.name)
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
                            }
                    ) { animeData ->
                        AnimeItem(
                            useCards = useCards,
                            animeData = animeData,
                            bingoData = bingoData,
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
