package io.future.laboratories.ui.pages

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.FieldData
import io.future.laboratories.common.RowData
import io.future.laboratories.ui.R
import io.future.laboratories.ui.components.BingoEditor
import io.future.laboratories.ui.components.BingoNameField
import io.future.laboratories.ui.components.DefaultHeader
import io.future.laboratories.ui.components.DismissDialog
import io.future.laboratories.ui.components.OptionToggle
import io.future.laboratories.ui.components.PositiveButton

@Composable
public fun EditorPage(
    preferences: SharedPreferences,
    bingoData: BingoData? = null,
    onBackButtonPress: () -> Unit,
    onClickSave: (BingoData, isNew: Boolean) -> Unit,
) {
    var lastId = preferences.getInt("LAST_USED_ID", 0)
    val localBingoData by remember {
        mutableStateOf(
            bingoData ?: BingoData(
                id = ++lastId,
                name = "",
                rowData = List(5) {
                    RowData(
                        fieldData = List(5) {
                            FieldData(
                                text = "",
                                isMarked = false
                            )
                        }
                    )
                }
            )
        )
    }

    LazyColumn {
        item {
            DefaultHeader(title = stringResource(id = R.string.name))

            BingoNameField(bingoData = localBingoData)
        }

        item {
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

            if (bingoData != null) {
                Text(
                    text = stringResource(id = R.string.shuffle_hint),
                    fontSize = 14.sp,
                )
            }
        }

        item {
            DefaultHeader(title = stringResource(id = R.string.complete))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                DismissDialog(onAccept = onBackButtonPress)

                PositiveButton(
                    onClick = {
                        validateDataAndSave(
                            preferences = preferences,
                            bingoData = localBingoData,
                            isNewDataSet = bingoData == null,
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

    if (bingoData.rowData.count() != 5) return

    bingoData.rowData.forEach { row ->
        if (row.fieldData.count() != 5) return

        row.fieldData.forEach { field ->
            if (field.text.isBlank()) return
        }
    }

    if (shuffle) {
        bingoData.rowData = bingoData.rowData
            .flatMap { it.fieldData }
            .shuffled()
            .chunked(5)
            .map { fieldData -> RowData(fieldData) }
    }

    if (isNewDataSet) {
        preferences.edit {
            putInt("LAST_USED_ID", bingoData.id)
        }
    }

    onClickSave(bingoData, isNewDataSet)
}
