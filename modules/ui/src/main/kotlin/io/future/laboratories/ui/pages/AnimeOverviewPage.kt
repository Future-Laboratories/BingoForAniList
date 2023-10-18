package io.future.laboratories.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListCollection
import io.future.laboratories.common.BingoData
import io.future.laboratories.ui.components.AnimeItem
import io.future.laboratories.ui.components.BooleanOption
import io.future.laboratories.ui.components.DefaultHeader

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun AnimeOverviewPage(
    bingoData: BingoData,
    showFinished: BooleanOption,
    animeDataList: MediaListCollection?,
    onSelectAnime: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
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
                .orEmpty()
                .forEach { animeList ->
                    stickyHeader(
                        animeList.name,
                        "Header",
                    ) {
                        DefaultHeader(title = animeList.name)
                    }

                    items(items = animeList.entries) { animeData ->
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