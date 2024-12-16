package io.future.laboratories.ui.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.Media
import io.future.laboratories.anilistapi.data.PageQueryParams
import io.future.laboratories.ui.Constants
import io.future.laboratories.ui.components.AnimeBrowserItem
import io.future.laboratories.ui.components.BrowserSearchbar

@OptIn(ExperimentalFoundationApi::class)
@Composable
public fun AnimeBrowserPage(
    pages: SnapshotStateMap<Int, SnapshotStateList<Media>>,
    currentQueryParams: PageQueryParams,
    mediaIdList: List<Long>,
    onAddPressed: (id: Long, callback: () -> Unit) -> Unit,
    onRequestMore: (PageQueryParams) -> Unit,
) {
    // Initially load 50 anime
    SideEffect {
        onRequestMore(currentQueryParams)
    }

    BrowserSearchbar(
        queryParams = currentQueryParams,
        onBottomSheetClose = {
            onRequestMore(currentQueryParams.copy())
        }
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(top = 8.dp),
        verticalArrangement = Constants.spacedByDefault,
    ) {
        pages.entries.forEach { entry ->
            itemsIndexed(entry.value) { index, item ->
                if(index == entry.value.size / 2 && pages.entries.last().key == entry.key) {
                    currentQueryParams.pageNumber.value++

                    onRequestMore(currentQueryParams)
                }

                AnimeBrowserItem(
                    media = item,
                    mediaIdList = mediaIdList,
                    onAddPressed = onAddPressed,
                )
            }
        }
    }
}