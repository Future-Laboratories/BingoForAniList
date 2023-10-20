package io.future.laboratories.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListCollection
import io.future.laboratories.common.BingoData
import io.future.laboratories.ui.components.AnimeItem
import io.future.laboratories.ui.components.BooleanOption
import io.future.laboratories.ui.components.DefaultHeader
import io.future.laboratories.ui.components.DropdownOption

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
public fun AnimeOverviewPage(
    bingoData: BingoData,
    showFinished: BooleanOption,
    pinned: DropdownOption,
    animeDataList: MediaListCollection?,
    onSelectAnime: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    SearchBar(
        modifier = Modifier.heightIn(max = 68.dp),
        query = query,
        onQueryChange = { query = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = {
            active = it
        },
        placeholder = { Text("Search title") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                modifier = Modifier.clickable {

                }
            )
        },
        content = {
            // TODO: Implement recommendations?
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
                ?.filter {
                    if (showFinished.currentValue) true else it.name == "Watching"
                }
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
                                        other = query,
                                        ignoreCase = true,
                                    )
                            }
                    ) { animeData ->
                        AnimeItem(
                            animeData = animeData,
                            bingoData = bingoData,
                            onClick = onSelectAnime,
                        )
                    }
                }
        }
    }
}