package io.future.laboratories.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.future.laboratories.anilistapi.data.Media

@Composable
internal fun AnimeBrowserItem(
    media: Media,
    modifier: Modifier = Modifier,
) {
    AnimeItemScaffold(
        imageData = { media.coverImage.large },
        actions = { },
        content = { expanded ->
            Column {
                Text(
                    text = media.title.userPreferred,
                    textDecoration = TextDecoration.Underline,
                )

                Text(
                    text = media.tags.joinToString(limit = if (expanded) -1 else 10) { it.name },
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                )
            }
        }
    )
}

@Composable
internal fun DiscoverRow(
    title: String,
    data: List<Media>,
) {
    Column {
        Text(text = title)

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            items(data) { it ->
                Text(text = it.title.userPreferred)
            }
        }
    }
}