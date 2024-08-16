package io.future.laboratories.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.Media
import io.future.laboratories.ui.Constants
import io.future.laboratories.ui.components.AnimeBrowserItem
import io.future.laboratories.ui.components.AnimeHeader
import io.future.laboratories.ui.components.StyledContainer

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
        for ((key, list) in pages) {
            stickyHeader(
                key = key,
                contentType = "Header",
            ) {
                AnimeHeader(title = "${50 * key + 1}-${50 * key + 50}")
            }

            itemsIndexed(list, key = { index, item -> item.id }) { index, item ->
                // Load More when reaching 80% of last call
                if (key == pages.keys.last() && index == 40) {
                    LaunchedEffect(key) { onRequestMore(key + 1) }
                }

                //TODO: Replace with real deal
                AnimeBrowserItem(
                    media = item,
                    modifier = Modifier.height(100.dp),
                )
            }
        }
    }
}
