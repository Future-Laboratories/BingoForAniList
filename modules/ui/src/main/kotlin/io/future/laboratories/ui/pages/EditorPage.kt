package io.future.laboratories.ui.pages

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.FieldData
import io.future.laboratories.common.RowData
import io.future.laboratories.common.plus
import io.future.laboratories.ui.R
import io.future.laboratories.ui.components.BackButton
import io.future.laboratories.ui.components.BingoEditor
import io.future.laboratories.ui.components.BingoNameField
import io.future.laboratories.ui.components.DefaultHeader
import io.future.laboratories.ui.components.DefaultWarningDialog
import io.future.laboratories.ui.components.OptionDropdown
import io.future.laboratories.ui.components.OptionToggle
import io.future.laboratories.ui.components.PositiveButton

@Composable
public fun EditorPage(
    preferences: SharedPreferences,
    bingoData: BingoData? = null,
    isImported: Boolean,
    showDialog: Boolean,
    onBackDialogDismiss: () -> Unit,
    onBackDialogAccept: () -> Unit,
    onClickSave: (BingoData, isNew: Boolean) -> Unit,
) {
    var localShowDialog by remember(showDialog) { mutableStateOf(showDialog) }
    var bingoSize by rememberSaveable { mutableIntStateOf(bingoData?.size ?: 5) }
    var lastId = preferences.getInt("LAST_USED_ID", 0)
    val localBingoData by remember {
        mutableStateOf(
            when {
                bingoData != null && isImported -> bingoData.copy(id = ++lastId)
                bingoData != null && !isImported -> bingoData
                else -> BingoData(
                    id = ++lastId,
                    name = "",
                    rowData = List(bingoSize) {
                        RowData(
                            fieldData = List(bingoSize) {
                                FieldData(
                                    text = "",
                                    isMarked = false
                                )
                            }
                        )
                    },
                    size = bingoSize,
                )
            }
        )
    }

    if (localShowDialog) {
        DefaultWarningDialog(
            header = stringResource(id = R.string.edit_dismiss_header),
            body = stringResource(id = R.string.edit_dismiss_body),
            actionButtonText = stringResource(id = R.string.yes),
            abortText = stringResource(id = android.R.string.cancel),
            onDismiss = { localShowDialog = false } + onBackDialogDismiss,
            onAction = onBackDialogAccept,
        )
    }

    LazyColumn {
        item {
            DefaultHeader(title = stringResource(id = R.string.name))

            BingoNameField(bingoData = localBingoData)
        }

        item(bingoSize) {
            DefaultHeader(title = stringResource(id = R.string.bingo))

            BingoEditor(bingoData = localBingoData)
        }

        var shuffle = false
        item {
            DefaultHeader(title = stringResource(id = R.string.options))

            OptionToggle(
                optionName = stringResource(id = R.string.shuffle),
                initialValue = false,
            ) { value ->
                shuffle = value
            }

            if (bingoData != null && !isImported) {
                Text(
                    text = stringResource(id = R.string.shuffle_hint),
                    fontSize = 14.sp,
                )
            }

            OptionDropdown(
                optionName = stringResource(id = R.string.bingo_size),
                values = (1..12).associate { it.toString() to it.toString() },
                initialValue = bingoSize.toString()
            ) {
                val localSize = it.toInt()

                localBingoData.rowData = List(localSize) { rowIndex ->
                    RowData(
                        fieldData = List(localSize) { fieldIndex ->
                            localBingoData
                                .rowData
                                .getOrNull(rowIndex)
                                ?.fieldData
                                ?.getOrNull(fieldIndex)
                                ?: FieldData(
                                    text = "",
                                    isMarked = false
                                )
                        }
                    )
                }
                localBingoData.size = localSize

                bingoSize = localSize
            }
        }

        item {
            DefaultHeader(title = stringResource(id = R.string.complete))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                BackButton(onClick = { localShowDialog = true })

                PositiveButton(
                    onClick = {
                        validateDataAndSave(
                            preferences = preferences,
                            bingoData = localBingoData,
                            isNewDataSet = bingoData == null || isImported,
                            shuffle = shuffle,
                            onClickSave = onClickSave,
                        )
                    },
                ) {
                    Text(text = stringResource(id = R.string.done))
                }
            }
        }
    }
}

private fun validateDataAndSave(
    preferences: SharedPreferences,
    bingoData: BingoData,
    isNewDataSet: Boolean,
    shuffle: Boolean,
    onClickSave: (BingoData, isNewDataSet: Boolean) -> Unit,
) {
    if (bingoData.id == 0) return

    if (bingoData.name.isBlank()) return

    if (bingoData.rowData.count() != bingoData.size) return

    bingoData.rowData.forEach { row ->
        if (row.fieldData.count() != bingoData.size) return

        row.fieldData.forEach { field ->
            if (field.text.isBlank()) return
        }
    }

    if (shuffle) {
        bingoData.rowData = bingoData.rowData
            .flatMap { it.fieldData }
            .shuffled()
            .chunked(bingoData.size)
            .map { fieldData -> RowData(fieldData) }
    }

    if (isNewDataSet) {
        preferences.edit {
            putInt("LAST_USED_ID", bingoData.id)
        }
    }

    onClickSave(bingoData, isNewDataSet)
}
