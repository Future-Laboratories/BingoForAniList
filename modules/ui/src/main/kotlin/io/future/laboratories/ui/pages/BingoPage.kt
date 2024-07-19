package io.future.laboratories.ui.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.future.laboratories.common.BingoData
import io.future.laboratories.common.FieldData
import io.future.laboratories.common.RowData
import io.future.laboratories.ui.AllPreview
import io.future.laboratories.ui.Constants
import io.future.laboratories.ui.R
import io.future.laboratories.ui.components.Bingo
import io.future.laboratories.ui.components.BingoStat
import io.future.laboratories.ui.components.DefaultHeader
import io.future.laboratories.ui.theme.AniListBingoTheme

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
        val rowCounts = IntArray(bingoData.size)
        val columnCounts = IntArray(bingoData.size)
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

            if (rowData.fieldData[bingoData.size - 1 - rowIndex].isMarked) {
                diagonalCounts[1]++
            }
        }

        horizontal = rowCounts.count { it == bingoData.size }
        vertical = columnCounts.count { it == bingoData.size }
        diagonal = diagonalCounts.count { it == bingoData.size }
    }

    LaunchedEffect(bingoData) {
        calculateCounts()
    }

    LazyColumn(
        verticalArrangement = Constants.spacedByDefault,
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
                max = bingoData.size,
            )

            Spacer(modifier = Modifier.height(8.dp))

            BingoStat(
                name = stringResource(id = R.string.bingo_vertical),
                outOf = vertical,
                max = bingoData.size,
            )

            Spacer(modifier = Modifier.height(8.dp))

            BingoStat(
                name = stringResource(id = R.string.bingo_diagonal),
                outOf = diagonal,
                max = 2,
            )
        }
    }
}

@AllPreview
@Composable
public fun BingoPagePreview() {
    AniListBingoTheme {
        Surface {
            BingoPage(
                bingoData = BingoData(2070, "TestBingo", List(5) {
                    RowData(List(5) {
                        FieldData("TestField", false)
                    })
                }),
                onDataChange = {},
            )
        }
    }
}