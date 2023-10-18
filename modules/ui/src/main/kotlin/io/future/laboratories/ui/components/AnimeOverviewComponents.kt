package io.future.laboratories.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.common.BingoData
import io.future.laboratories.ui.RoundedCornersTransformation

@Composable
internal fun AnimeItem(
    animeData: MediaList,
    bingoData: BingoData,
    onClick: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(bingoData, animeData)
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(animeData.media.coverImage.large)
                    .transformations(RoundedCornersTransformation(all = 30f))
                    .crossfade(true)
                    .build(),
                contentDescription = "Cover",
                modifier = Modifier
                    .width(80.dp)
            )

            Text(
                modifier = Modifier.weight(1f),
                text = animeData.media.title.userPreferred,
            )
        }
    }
}

