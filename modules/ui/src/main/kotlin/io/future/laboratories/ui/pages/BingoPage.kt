package io.future.laboratories.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.common.loadSingle
import io.future.laboratories.common.save
import io.future.laboratories.ui.components.Bingo

@Composable
public fun BingoPage(
    context: Context,
    bingoData: BingoData,
    animeData: MediaList,
) {
    Column {
        val data = context.loadSingle("${animeData.media.id}/${bingoData.id}") ?: bingoData

        Bingo(data) {
            context.save(data, "${animeData.media.id}/${bingoData.id}")
        }
    }
}