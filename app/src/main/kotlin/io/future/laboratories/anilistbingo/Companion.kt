package io.future.laboratories.anilistbingo

import io.future.laboratories.anilistbingo.data.BingoData
import io.future.laboratories.anilistbingo.data.FieldData
import io.future.laboratories.anilistbingo.data.RowData

internal object Companion {
    private const val PREFIX = "BINGO"
    internal const val PREFERENCE_ACCESS_TOKEN: String = "${PREFIX}_TOKEN"
    internal const val PREFERENCE_ACCESS_TYPE: String = "${PREFIX}_BINGO_TYPE"
    internal const val PREFERENCE_ACCESS_EXPIRED: String = "${PREFIX}_BINGO_EXPIRED"
    internal const val PREFERENCE_ACCESS_USER_ID: String = "${PREFIX}_USER_ID"
    internal const val TEMP_PATH = "TEMP_DATA"

    internal var MOCK_DATA: BingoData = BingoData(
        id = 2070,
        name = "Mock Bingo",
        rowData = emptyList(),
    ).copy(
        rowData = listOf(
            RowData(
                fieldData = listOf(
                    FieldData(
                        text = "text 1 - 1",
                    ),
                    FieldData(
                        text = "text 1 - 2",
                    ),
                    FieldData(
                        text = "text 1 - 3",
                    ),
                    FieldData(
                        text = "text 1 - 4",
                        isMarked = true,
                    ),
                    FieldData(
                        text = "text 1 - 5",
                    ),
                ),
            ),
            RowData(
                fieldData = listOf(
                    FieldData(
                        text = "text 2 - 1",
                    ),
                    FieldData(
                        text = "text 2 - 2",
                        isMarked = true,
                    ),
                    FieldData(
                        text = "text 2 - 3",
                    ),
                    FieldData(
                        text = "text 2 - 4",
                    ),
                    FieldData(
                        text = "text 2 - 5",
                    ),
                ),
            ),
            RowData(
                fieldData = listOf(
                    FieldData(
                        text = "text 3 - 1",
                        isMarked = true,

                        ),
                    FieldData(
                        text = "text 3 - 2",
                    ),
                    FieldData(
                        text = "text 3 - 3",
                    ),
                    FieldData(
                        text = "text 3 - 4",
                        isMarked = true,
                    ),
                    FieldData(
                        text = "text 3 - 5",
                    ),
                ),
            ),
            RowData(
                fieldData = listOf(
                    FieldData(
                        text = "text 4 - 1",
                    ),
                    FieldData(
                        text = "text 4 - 2",
                    ),
                    FieldData(
                        text = "text 4 - 3",
                    ),
                    FieldData(
                        text = "text 4 - 4",
                    ),
                    FieldData(
                        text = "text 4 - 5",
                    ),
                ),
            ),
            RowData(
                fieldData = listOf(
                    FieldData(
                        text = "text 5 - 1",
                    ),
                    FieldData(
                        text = "text 5 - 2",
                    ),
                    FieldData(
                        text = "text 5 - 3",
                    ),
                    FieldData(
                        text = "text 5 - 4",
                        isMarked = true,
                    ),
                    FieldData(
                        text = "text 5 - 5",
                        isMarked = true,
                    ),
                ),
            ),
        ),
    )

    internal fun storagePath(subPath: String? = null): String {
        return if (subPath != null) "Bingo/${subPath}" else "Bingo"
    }
}