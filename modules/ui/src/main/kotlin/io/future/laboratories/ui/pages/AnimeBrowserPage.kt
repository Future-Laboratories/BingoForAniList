package io.future.laboratories.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import io.future.laboratories.anilistapi.data.Media
import io.future.laboratories.ui.Constants
import io.future.laboratories.ui.components.DiscoverRow

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun AnimeBrowserPage(
    pages: SnapshotStateMap<Int, SnapshotStateList<Media>>,
    onRequestMore: (Int) -> Unit,
) {
    // Initially load 50 anime
    if (pages.isEmpty()) {
        onRequestMore(0)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Constants.spacedByDefault,
    ) {
        for ((_, list) in pages) {
            item {
                DiscoverRow(
                    title = "Discover",
                    data = list,
                )
            }
        }
    }
}