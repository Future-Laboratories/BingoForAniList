package io.future.laboratories.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.FieldData
import io.future.laboratories.common.RowData
import io.future.laboratories.common.StyleProvider
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
        colors = StyleProvider.outLineTextColor,
    )
}

@Composable
internal fun BingoEditor(
    bingoData: BingoData,
) {
    val gradient = StyleProvider.containerGradient
    Column(
        modifier = Modifier
            .border(
                width = 2.dp, gradient,
                shape = RectangleShape,
            )
            .graphicsLayer(alpha = 0.99f)
            .drawWithCache {
                onDrawWithContent {
                    drawRect(gradient, blendMode = BlendMode.SrcOut)
                    drawContent()
                }
            },
    ) {
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
            .padding(1.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = fieldText,
            onValueChange = {
                fieldText = it
                fieldData.text = fieldText
            },
            modifier = Modifier.fillMaxSize(),
            textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface),
        ) { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(6.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                innerTextField()
            }
        }
    }
}