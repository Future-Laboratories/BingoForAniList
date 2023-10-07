package io.future.laboratories.anilistbingo.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@JsonClass(generateAdapter = true)
public data class DataHolder<T>(
    var data: T
)

@JsonClass(generateAdapter = true)
public data class AniListBody(
    val query: String,
    val variables: Map<String, Any>
)

//region ViewerData

@JsonClass(generateAdapter = true)
public data class ViewerData(
    @Json(name = "Viewer") val viewer: Viewer
)

@JsonClass(generateAdapter = true)
public data class Viewer(
    val id: Long
)

//endregion

//region ListData

@JsonClass(generateAdapter = true)
public data class MediaListCollectionData(
    @Json(name = "MediaListCollection") val mediaListCollection : MediaListCollection,
)

@JsonClass(generateAdapter = true)
public data class MediaListCollection(
    val lists : List<MediaListGroup>,
)

@JsonClass(generateAdapter = true)
public data class MediaListGroup(
    val name: String,
    val entries: List<MediaList>,
)

@JsonClass(generateAdapter = true)
public data class MediaList(
    val id: Long,
    val media: Media,
)

@JsonClass(generateAdapter = true)
public data class Media(
    val title: MediaTitle
)

@JsonClass(generateAdapter = true)
public data class MediaTitle(
    val userPreferred: String
)

//endregion
