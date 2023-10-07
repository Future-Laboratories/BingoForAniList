package io.future.laboratories.anilistbingo.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BingoData(
    val id : Int,
    var name : String,
    var rowData : List<RowData>,
)

@JsonClass(generateAdapter = true)
internal data class RowData(
    var fieldData : List<FieldData>,
)

@JsonClass(generateAdapter = true)
internal data class FieldData(
    var text : String,
    var isMarked: Boolean = false,
)
