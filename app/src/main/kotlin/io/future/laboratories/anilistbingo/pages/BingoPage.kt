package io.future.laboratories.anilistbingo.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.anilistbingo.ui.Bingo

@Composable
public fun BingoPage(bingoData: BingoData) {
    Column {
        Bingo(bingoData)

        // TODO: Back-Navigation
    }
}