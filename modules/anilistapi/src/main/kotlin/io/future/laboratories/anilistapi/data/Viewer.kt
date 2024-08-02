package io.future.laboratories.anilistapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class ViewerData(
    @Json(name = "Viewer") val viewer: Viewer,
)

@JsonClass(generateAdapter = true)
public data class Viewer(
    val id: Long,
)
