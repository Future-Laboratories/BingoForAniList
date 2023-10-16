package io.future.laboratories.ui.pages

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
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.ui.components.AnimeItem

@Composable
public fun AnimeOverviewPage(
    bingoData: BingoData,
    animeDataList: MediaListCollection?,
    onSelectAnime: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(
                items = animeDataList
                    ?.lists
                    ?.firstOrNull { it.name == "Watching" }
                    ?.entries
                    .orEmpty()
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