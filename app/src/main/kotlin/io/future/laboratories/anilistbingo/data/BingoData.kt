package io.future.laboratories.anilistbingo.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class BingoData(
    val id : Int,
    var name : String,
    var rowData : List<RowData>,
)

@JsonClass(generateAdapter = true)
public data class RowData(
    var fieldData : List<FieldData>,
)

@JsonClass(generateAdapter = true)
public data class FieldData(
    var text : String,
    var isMarked: Boolean = false,
)
