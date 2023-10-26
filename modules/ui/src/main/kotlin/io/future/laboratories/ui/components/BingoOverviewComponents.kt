package io.future.laboratories.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.future.laboratories.common.BingoData
import io.future.laboratories.ui.R

@Composable
internal fun BingoItem(
    bingoData: BingoData,
    onClick: (BingoData) -> Unit,
    onShare: (BingoData) -> Unit,
    onEdit: (BingoData) -> Unit,
    onDelete: (BingoData) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(bingoData)
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var expended by rememberSaveable { mutableStateOf(false) }

            Text(
                modifier = Modifier.weight(1f),
                text = bingoData.name,
            )

            AnimatedVisibility(visible = expended) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PositiveImageButton(
                        onClick = { onShare(bingoData) },
                        contentDescription = stringResource(id = R.string.share),
                        imageVector = Icons.Rounded.Share,
                    )

                    PositiveImageButton(
                        onClick = { onEdit(bingoData) },
                        contentDescription = stringResource(id = R.string.edit),
                        imageVector = Icons.Rounded.Edit,
                    )

                    DeleteDialog(
                        bingoData = bingoData,
                        onDelete = onDelete,
                    )
                }
            }

            PositiveImageButton(
                onClick = { expended = !expended },
                contentDescription = "null",
                imageVector = Icons.Rounded.MoreVert,
            )
        }
    }
}

@Composable
internal fun DeleteDialog(
    bingoData: BingoData,
    onDelete: (BingoData) -> Unit,
) {
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }

    NegativeImageButton(
        onClick = { showDeleteDialog = true },
        contentDescription = stringResource(id = R.string.delete),
        imageVector = Icons.Rounded.Delete,
    )

    if (showDeleteDialog) {
        DefaultDialog(
            text = stringResource(id = R.string.delete_permanently),
            actionButtonText = stringResource(id = R.string.delete),
            abortText = stringResource(id = android.R.string.cancel),
            onDismiss = { showDeleteDialog = false },
            onAction = { onDelete(bingoData) },
        )
    }
}
