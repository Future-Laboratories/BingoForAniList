package io.future.laboratories.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.loadSingle
import io.future.laboratories.common.save
import io.future.laboratories.ui.components.Bingo

@Composable
public fun BingoPage(
    context: Context,
    bingoData: BingoData,
    animeData: MediaList,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val data = context.loadSingle("${animeData.media.id}/${bingoData.id}") ?: bingoData

        item {
            Bingo(bingoData = data) {
                context.save(data, "${animeData.media.id}/${bingoData.id}")
            }
        }
    }
}