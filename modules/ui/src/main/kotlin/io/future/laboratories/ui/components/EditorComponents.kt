package io.future.laboratories.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import io.future.laboratories.common.FieldData
import io.future.laboratories.common.RowData
import io.future.laboratories.common.textColor
import io.future.laboratories.ui.R


@Composable
internal fun BingoNameField(
    bingoData: BingoData,
) {
    var bingoName by rememberSaveable { mutableStateOf(bingoData.name) }

    OutlinedTextField(
        value = bingoName,
        label = {
            Text(text = stringResource(id = R.string.name))
        },
        onValueChange = {
            bingoName = it
            bingoData.name = it
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
internal fun BingoEditor(
    bingoData: BingoData,
) {
    Column {
        bingoData.rowData.take(bingoData.size).forEach { rowData ->
            BingoEditorRow(rowData = rowData, size = bingoData.size)
        }
    }
}

@Composable
private fun BingoEditorRow(
    rowData: RowData,
    size: Int,
) {
    Row {
        rowData.fieldData.take(size).forEach { fieldData ->
            BingoEditorField(fieldData = fieldData)
        }
    }
}

@Composable
private fun RowScope.BingoEditorField(
    fieldData: FieldData,
) {
    var fieldText by rememberSaveable { mutableStateOf(fieldData.text) }

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