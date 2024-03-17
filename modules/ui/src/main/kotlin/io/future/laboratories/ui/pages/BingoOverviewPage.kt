package io.future.laboratories.ui.pages

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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.textColor
import io.future.laboratories.ui.Constants
import io.future.laboratories.ui.components.BingoItem
import io.future.laboratories.ui.components.BooleanOption

@Composable
public fun BingoOverviewPage(
    useCards: BooleanOption,
    bingoDataList: SnapshotStateList<BingoData>,
    onShare: (BingoData) -> Unit,
    onEdit: (BingoData?) -> Unit,
    onDelete: (BingoData) -> Unit,
    onSelectBingo: (bingoData: BingoData) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Constants.spacedByDefault,
        ) {
            items(bingoDataList) { bingoData ->
                BingoItem(
                    useCards = useCards,
                    bingoData = bingoData,
                    onClick = onSelectBingo,
                    onShare = onShare,
                    onEdit = onEdit,
                    onDelete = onDelete,
                )
            }
        }

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