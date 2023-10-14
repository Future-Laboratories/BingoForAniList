package io.future.laboratories.anilistapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
public data class DataHolder<T>(
    var data: T,
)

@JsonClass(generateAdapter = true)
public data class AniListBody(
    val query: String,
    val variables: Map<String, Any>,
)

//region ViewerData

@JsonClass(generateAdapter = true)
public data class ViewerData(
    @Json(name = "Viewer") val viewer: Viewer,
)

@JsonClass(generateAdapter = true)
public data class Viewer(
    val id: Long,
)

//endregion

//region ListData

@JsonClass(generateAdapter = true)
public data class MediaListCollectionAndUserData(
    @Json(name = "MediaListCollection") val mediaListCollection: MediaListCollection,
    @Json(name = "User") val user: User,
)

@JsonClass(generateAdapter = true)
public data class User(
    val avatar: UserAvatar,
)

@JsonClass(generateAdapter = true)
public data class UserAvatar(
    val medium: String,
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
    val media: Media,
)

@JsonClass(generateAdapter = true)
public data class Media(
    val id: Long,
    val title: MediaTitle,
    val coverImage: MediaCoverImage,
)

@JsonClass(generateAdapter = true)
public data class MediaCoverImage(
    val large: String,
)

@JsonClass(generateAdapter = true)
public data class MediaTitle(
    val userPreferred: String,
)

//endregion
