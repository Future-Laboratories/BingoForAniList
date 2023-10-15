package io.future.laboratories.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.future.laboratories.anilistapi.data.MediaList
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.ui.R
import io.future.laboratories.ui.RoundedCornersTransformation

@Composable
internal fun BingoItem(
    bingoData: BingoData,
    onClick: (BingoData) -> Unit,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = bingoData.name,
            )

            PositiveImageButton(
                onClick = { onEdit(bingoData) },
                contentDescription = stringResource(id = R.string.edit),
                imageVector = Icons.Rounded.Edit,
            )

            DefaultSpacer()

            DeleteDialog(
                bingoData = bingoData,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
internal fun AnimeItem(
    animeData: MediaList,
    bingoData: BingoData,
    onClick: (bingoData: BingoData, animeData: MediaList) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick(bingoData, animeData)
            },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(animeData.media.coverImage.large)
                    .transformations(RoundedCornersTransformation(all = 30f))
                    .crossfade(true)
                    .build(),
                contentDescription = "Cover",
                modifier = Modifier
                    .width(80.dp)
            )

            Text(
                modifier = Modifier.weight(1f),
                text = animeData.media.title.userPreferred,
            )
        }
    }
}

@Composable
internal fun DeleteDialog(
    bingoData: BingoData,
    onDelete: (BingoData) -> Unit,
) {
    var showDeleteDialog by remember {
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
