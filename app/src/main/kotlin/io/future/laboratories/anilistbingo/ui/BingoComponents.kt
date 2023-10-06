package io.future.laboratories.anilistbingo.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.future.laboratories.anilistbingo.R
import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.anilistbingo.data.FieldData
import io.future.laboratories.anilistbingo.data.RowData

@Composable
public fun Bingo(
    bingoData: BingoData,
    onValueChanged: ((BingoData) -> Unit)? = null,
) {
    Column {
        bingoData.rowData.take(5).forEach { rowData ->
            BingoRow(
                rowData = rowData,
                bingoData = bingoData,
                onValueChanged = onValueChanged,
            )
        }
    }
}

@Composable
private fun BingoRow(
    rowData: RowData,
    bingoData: BingoData,
    onValueChanged: ((BingoData) -> Unit)? = null,
) {
    Row {
        rowData.fieldData.take(5).forEach { fieldData ->
            BingoField(
                fieldData = fieldData,
                bingoData = bingoData,
                onValueChanged = onValueChanged,
            )
        }
    }
}

private val colorFilter = ColorFilter.tint(color = Color.Red.copy(alpha = 0.6f))

@Composable
private fun RowScope.BingoField(
    fieldData: FieldData,
    bingoData: BingoData,
    onValueChanged: ((BingoData) -> Unit)? = null,
) {
    var isMarked by remember { mutableStateOf(fieldData.isMarked) }

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            .clickable {
                fieldData.isMarked = !fieldData.isMarked
                isMarked = fieldData.isMarked
                onValueChanged?.invoke(bingoData)
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = fieldData.text)

        if (isMarked) {
            Image(
                painter = painterResource(id = R.drawable.checked),
                modifier = Modifier.fillMaxSize(),
                contentDescription = null,
                colorFilter = colorFilter,
            )
        }
    }
}