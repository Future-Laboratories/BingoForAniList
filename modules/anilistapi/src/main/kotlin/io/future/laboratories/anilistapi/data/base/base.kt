package io.future.laboratories.anilistapi.data.base

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class DataHolder<T>(
    var data: T,
)

@JsonClass(generateAdapter = true)
public data class AniListQueryBody(
    val query: String,
    val variables: Map<String, Any>,
)

@JsonClass(generateAdapter = true)
public data class AniListMutationBody(
    val mutation: String,
    val variables: Map<String, Any>,
)
