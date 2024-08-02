package io.future.laboratories.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import io.future.laboratories.ui.Constants
import io.future.laboratories.ui.R

@Composable
internal fun BingoItem(
    useCards: BooleanOption,
    bingoData: BingoData,
    onClick: (BingoData) -> Unit,
    onShare: (BingoData) -> Unit,
    onEdit: (BingoData) -> Unit,
    onDelete: (BingoData) -> Unit,
) {
    var expended by rememberSaveable { mutableStateOf(false) }

    StyledContainer(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(bingoData)
            }
            .animateContentSize(),
    ) {
        Row(
            modifier = if (useCards.currentValue) Modifier.padding(all = 8.dp) else Modifier,
            horizontalArrangement = Constants.spacedByDefault,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = bingoData.name,
            )

            Column(
                verticalArrangement = Constants.spacedByDefault,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PositiveImageButton(
                    onClick = { expended = !expended },
                    contentDescription = "null",
                    imageVector = Icons.Rounded.MoreVert,
                )

                AnimatedVisibility(visible = expended) {
                    Column(
                        verticalArrangement = Constants.spacedByDefault,
                        horizontalAlignment = Alignment.CenterHorizontally,
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

            }
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
        DefaultWarningDialog(
            header = stringResource(id = R.string.delete_permanently_header),
            body = stringResource(id = R.string.delete_permanently_body),
            actionButtonText = stringResource(id = R.string.delete),
            abortText = stringResource(id = android.R.string.cancel),
            onDismiss = { showDeleteDialog = false },
            onAction = { onDelete(bingoData) },
        )
    }
}
