package io.future.laboratories.anilistbingo.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.future.laboratories.anilistbingo.R
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.anilistbingo.data.api.MediaList
import io.future.laboratories.anilistbingo.logout

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = bingoData.name,
            )

            PositiveImageButton(
                onClick = { onEdit(bingoData) },
                contentDescription = stringResource(id = R.string.edit),
                imageResId = R.drawable.edit,
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(animeData.media.coverImage.large)
                    .crossfade(true)
                    .build(),
                contentDescription = "Cover",
                modifier = Modifier
                    .width(80.dp)
                    .clip(RoundedCornerShape(12.dp)),
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
    val onDismiss = { showDeleteDialog = false }

    NegativeImageButton(
        onClick = { showDeleteDialog = true },
        contentDescription = stringResource(id = R.string.delete),
        imageResId = R.drawable.delete_forever,
    )

    if (showDeleteDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(text = stringResource(id = R.string.delete_permanently))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        PositiveButton(onClick = onDismiss) {
                            Text(text = stringResource(id = android.R.string.cancel))
                        }
                        DefaultSpacer()
                        NegativeButton(onClick = {
                            onDelete(bingoData)
                            onDismiss()
                        }) {
                            Text(text = stringResource(id = R.string.delete))
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun LoginButton(
    context: Context,
    preferences: SharedPreferences,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
) {
    if (!isLoggedIn) {
        PositiveButton(
            onClick = {
                val url = context.getString(R.string.anilistUrl)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(url))
                ContextCompat.startActivity(context, intent, null)
            },
        ) {
            Text(text = "Login")
        }
    } else {
        PositiveButton(
            onClick = {
                preferences.logout()
                onLogout()
            },
        ) {
            Text(text = "Logout")
        }
    }
}