package io.future.laboratories.anilistbingo.pages

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import io.future.laboratories.anilistbingo.R
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.anilistbingo.logout
import io.future.laboratories.anilistbingo.ui.DefaultSpacer
import io.future.laboratories.anilistbingo.ui.NegativeButton
import io.future.laboratories.anilistbingo.ui.PositiveButton

@Composable
internal fun OverviewPage(
    context: Context,
    preferences: SharedPreferences,
    data: SnapshotStateList<BingoData>,
    isLoggedIn: Boolean,
    onLogout: () -> Unit,
    onEdit: (BingoData) -> Unit,
    onDelete: (BingoData) -> Unit,
    onClickField: (BingoData) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items(data) { bingoData ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClickField(bingoData)
                    },
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(text = bingoData.name)

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Row {
                        PositiveButton(onClick = { onEdit(bingoData) }) {
                            Image(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = stringResource(id = R.string.edit),
                            )
                        }

                        DefaultSpacer()

                        var showDeleteDialog by remember {
                            mutableStateOf(false)
                        }
                        NegativeButton(
                            onClick = { showDeleteDialog = true },
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.delete_forever),
                                contentDescription = stringResource(id = R.string.delete),
                            )
                        }

                        if (showDeleteDialog) {
                            Dialog(onDismissRequest = { showDeleteDialog = false }) {
                                Card {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(text = "Do you really want to delete this bingo permanently?")
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End,
                                        ) {
                                            PositiveButton(onClick = { showDeleteDialog = false }) {
                                                Text(text = stringResource(id = android.R.string.cancel))
                                            }
                                            DefaultSpacer()
                                            NegativeButton(onClick = {
                                                onDelete(bingoData)
                                                showDeleteDialog = false
                                            }) {
                                                Text(text = stringResource(id = R.string.delete))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
            if (!isLoggedIn) {
                PositiveButton(
                    onClick = {
                        val url =
                            "https://anilist.co/api/v2/oauth/authorize?client_id=14752&response_type=token"
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setData(Uri.parse(url))
                        startActivity(context, intent, null)
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
    }
}