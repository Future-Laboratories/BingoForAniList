package io.future.laboratories.anilistbingo.pages

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.anilistbingo.ui.AnimeItem
import io.future.laboratories.anilistbingo.ui.BingoItem
import io.future.laboratories.anilistbingo.ui.LoginButton

@Composable
internal fun OverviewPage(
    context: Context,
    preferences: SharedPreferences,
    bingoDataList: SnapshotStateList<BingoData>,
    animeDataList: SnapshotStateList<MediaList>,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    onEdit: (BingoData) -> Unit,
    onDelete: (BingoData) -> Unit,
    onClickField: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    var mode : Mode by remember {
        mutableStateOf(Mode.BINGO)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when(mode) {
            is Mode.BINGO -> {
                items(bingoDataList) { bingoData ->
                    BingoItem(
                        bingoData = bingoData,
                        onClick = { localBingoData -> mode = Mode.ANIME(bingoData = localBingoData) },
                        onEdit = onEdit,
                        onDelete = onDelete,
                    )
                }
            }

            is Mode.ANIME -> {
                items(animeDataList) { animeData ->
                    AnimeItem(
                        animeData = animeData,
                        bingoData = (mode as Mode.ANIME).bingoData,
                        onClick = onClickField,
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))

            LoginButton(
                context = context,
                preferences = preferences,
                isLoggedIn = isLoggedIn,
                onLogout = onLogout,
            )
        }
    }
}

private sealed class Mode {
    data object BINGO : Mode()

    class ANIME(val bingoData: BingoData) : Mode()
}