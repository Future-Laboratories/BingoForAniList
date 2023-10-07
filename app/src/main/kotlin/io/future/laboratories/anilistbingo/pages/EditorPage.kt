package io.future.laboratories.anilistbingo.pages

import android.content.SharedPreferences
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import io.future.laboratories.anilistbingo.R
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.anilistbingo.data.FieldData
import io.future.laboratories.anilistbingo.data.RowData
import io.future.laboratories.anilistbingo.textColor
import io.future.laboratories.anilistbingo.ui.PositiveButton

@Composable
internal fun EditorPage(
    preferences: SharedPreferences,
    bingoData: BingoData? = null,
    onClickSave: (BingoData) -> Unit,
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

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(id = R.string.name))

            var bingoName by remember { mutableStateOf(localBingoData.name) }

            TextField(
                value = bingoName,
                onValueChange = {
                    bingoName = it
                    localBingoData.name = it
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Divider(color = MaterialTheme.colorScheme.tertiary)

        Spacer(modifier = Modifier.height(8.dp))

        BingoEditor(bingoData = localBingoData)

        PositiveButton(
            onClick = {
                validateDataAndSave(
                    preferences = preferences,
                    bingoData = localBingoData,
                    isNewDataSet = bingoData == null,
                    onClickSave = onClickSave,
                )
            },
        ) {
            Text(text = "Done")
        }
    }
}

@Composable
private fun BingoEditor(
    bingoData: BingoData,
) {
    Column {
        bingoData.rowData.take(5).forEach { rowData ->
            BingoEditorRow(
                rowData = rowData,
            )
        }
    }
}

@Composable
private fun BingoEditorRow(
    rowData: RowData,
) {
    Row {
        rowData.fieldData.take(5).forEach { fieldData ->
            BingoEditorField(
                fieldData = fieldData,
            )
        }
    }
}

@Composable
private fun RowScope.BingoEditorField(
    fieldData: FieldData,
) {
    var fieldText by remember { mutableStateOf(fieldData.text) }
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = fieldText,
            onValueChange = {
                fieldText = it
                fieldData.text = fieldText
            },
            modifier = Modifier.fillMaxSize(),
            textStyle = LocalTextStyle.current.copy(color = textColor),
        ) { innerTextField ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart,
            ) {
                innerTextField()
            }
        }
    }
}

private fun validateDataAndSave(
    preferences: SharedPreferences,
    bingoData: BingoData,
    isNewDataSet: Boolean,
    onClickSave: (BingoData) -> Unit,
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

    if (isNewDataSet) {
        preferences.edit {
            putInt("LAST_USED_ID", bingoData.id)
        }
    }

    onClickSave(bingoData)
}
