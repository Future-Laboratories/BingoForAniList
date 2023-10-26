package io.future.laboratories.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.FieldData
import io.future.laboratories.common.RowData
import io.future.laboratories.ui.R
import io.future.laboratories.ui.components.Bingo
import io.future.laboratories.ui.components.BingoStat
import io.future.laboratories.ui.components.DefaultHeader

@Composable
public fun BingoPage(
    bingoData: BingoData,
    onDataChange: (bingoData: BingoData) -> Unit,
) {
    var horizontal by remember {
        mutableIntStateOf(0)
    }

    var vertical by remember {
        mutableIntStateOf(0)
    }

    var diagonal by remember {
        mutableIntStateOf(0)
    }

    fun calculateCounts() {
        val rowCounts = IntArray(5)
        val columnCounts = IntArray(5)
        val diagonalCounts = IntArray(2)

        bingoData.rowData.forEachIndexed { rowIndex, rowData ->
            rowData.fieldData.forEachIndexed { fieldIndex, fieldData ->
                if (fieldData.isMarked) {
                    columnCounts[fieldIndex]++
                    rowCounts[rowIndex]++
                }
            }

            if (rowData.fieldData[rowIndex].isMarked) {
                diagonalCounts[0]++
            }

            if (rowData.fieldData[4 - rowIndex].isMarked) {
                diagonalCounts[1]++
            }
        }

        horizontal = rowCounts.count { it == 5 }
        vertical = columnCounts.count { it == 5 }
        diagonal = diagonalCounts.count { it == 5 }
    }

    calculateCounts()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            DefaultHeader(title = stringResource(id = R.string.bingo))

            Bingo(bingoData = bingoData) { data ->
                onDataChange(data)

                calculateCounts()
            }
        }

        item {
            DefaultHeader(title = stringResource(id = R.string.bingo_stats))

            BingoStat(
                name = stringResource(id = R.string.bingo_horizontal),
                outOf = horizontal,
                max = 5,
            )

            BingoStat(
                name = stringResource(id = R.string.bingo_vertical),
                outOf = vertical,
                max = 5,
            )

            BingoStat(
                name = stringResource(id = R.string.bingo_diagonal),
                outOf = diagonal,
                max = 2,
            )
        }
    }
}

@Preview
@Composable
public fun BingoPagePreview() {
    BingoPage(
        bingoData = BingoData(2070, "TestBingo", List(5) {
            RowData(List(5) {
                FieldData("TestField", false)
            })
        }),
        onDataChange = {},
    )
}