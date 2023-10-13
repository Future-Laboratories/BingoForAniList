package io.future.laboratories.ui.pages

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
import io.future.laboratories.anilistapi.data.MediaListCollection
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.ui.components.AnimeItem
import io.future.laboratories.ui.components.BackButton
import io.future.laboratories.ui.components.BingoItem
import io.future.laboratories.ui.components.LoginButton

@Composable
public fun OverviewPage(
    context: Context,
    preferences: SharedPreferences,
    bingoDataList: SnapshotStateList<BingoData>,
    animeDataList: MediaListCollection?,
    defaultMode: Mode,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    onEdit: (BingoData) -> Unit,
    onDelete: (BingoData) -> Unit,
    onClickField: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    var mode: Mode by remember {
        mutableStateOf(defaultMode)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (mode) {
            is Mode.BINGO -> {
                items(bingoDataList) { bingoData ->
                    BingoItem(
                        bingoData = bingoData,
                        onClick = { localBingoData ->
                            mode = Mode.ANIME(bingoData = localBingoData)
                        },
                        onEdit = onEdit,
                        onDelete = onDelete,
                    )
                }
            }

            is Mode.ANIME -> {
                items(
                    items = animeDataList
                        ?.lists
                        ?.firstOrNull { it.name == "Watching" }
                        ?.entries
                        .orEmpty()
                ) { animeData ->
                    AnimeItem(
                        animeData = animeData,
                        bingoData = (mode as Mode.ANIME).bingoData,
                        onClick = onClickField,
                    )
                }

                item {
                    BackButton(
                        onClick = {
                            mode = Mode.BINGO
                        }
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

public sealed class Mode {
    public data object BINGO : Mode()

    public class ANIME(internal val bingoData: BingoData) : Mode()
}