package io.future.laboratories.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.DetailedAniListData
import io.future.laboratories.ui.components.AnimeReleaseInfo
import io.future.laboratories.ui.components.AnimeTagsCharts
import io.future.laboratories.ui.components.GeneralInfo

@Composable
public fun AnimeItemPage(
    details: DetailedAniListData?,
) {
    if (details == null) return

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(300.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalItemSpacing = 8.dp,
    ) {
        item {
            GeneralInfo(
                imageURL = details.data.coverImage.large,
                title = details.data.title.userPreferred,
                body = details.data.description,
            )
        }

        item {
            AnimeReleaseInfo(
                episodes = details.data.episodes,
                duration = details.data.duration,
                startDate = details.data.startDate,
                endDate = details.data.endDate,
            )
        }

        item {
            AnimeTagsCharts(
                tags = details.data.tags,
            )
        }
    }
}
