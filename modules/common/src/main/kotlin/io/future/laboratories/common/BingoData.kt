package io.future.laboratories.common

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class BingoData(
    val id : Int,
    var name : String,
    var rowData : List<RowData>,
    var size: Int = 5,
) : Cloneable {
    /**
     * returns a completely new Instance of BingoData, instead of a soft-copy
     */
    public override fun clone(): BingoData =
        BingoData(id, name, rowData.map { rowData -> rowData.clone() }, size)
}

@JsonClass(generateAdapter = true)
public data class RowData(
    var fieldData : List<FieldData>,
) : Cloneable {
    /**
     * returns a completely new Instance of RowData, instead of a soft-copy
     */
    public override fun clone(): RowData =
        RowData(fieldData = fieldData.map { fieldData -> fieldData.copy() })
}

@JsonClass(generateAdapter = true)
public data class FieldData(
    var text : String,
    var isMarked: Boolean = false,
)
