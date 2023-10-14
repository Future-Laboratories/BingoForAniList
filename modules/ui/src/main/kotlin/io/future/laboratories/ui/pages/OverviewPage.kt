package io.future.laboratories.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistapi.data.MediaListCollection
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.common.textColor
import io.future.laboratories.ui.components.AnimeItem
import io.future.laboratories.ui.components.BackButton
import io.future.laboratories.ui.components.BingoItem

@Composable
public fun OverviewPage(
    bingoDataList: SnapshotStateList<BingoData>,
    animeDataList: MediaListCollection?,
    defaultMode: Mode,
    onEdit: (BingoData?) -> Unit,
    onDelete: (BingoData) -> Unit,
    onClickField: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    var mode: Mode by remember {
        mutableStateOf(defaultMode)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
        }

        if (mode == Mode.BINGO) {
            FloatingActionButton(
                modifier = Modifier
                    .width(64.dp)
                    .aspectRatio(1f)
                    .align(Alignment.BottomEnd),
                onClick = { onEdit(null) },
                shape = RoundedCornerShape(32.dp),
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier
                        .fillMaxSize(0.9f),
                )
            }
        }
    }
}

public sealed class Mode {
    public data object BINGO : Mode()

    public class ANIME(internal val bingoData: BingoData) : Mode()
}