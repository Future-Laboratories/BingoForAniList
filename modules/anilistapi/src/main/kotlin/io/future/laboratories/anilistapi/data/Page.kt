package io.future.laboratories.anilistapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class PageData(
    @Json(name = "Page") val page: Page,
)

@JsonClass(generateAdapter = true)
public data class Page(
    val media: List<Media>,
)