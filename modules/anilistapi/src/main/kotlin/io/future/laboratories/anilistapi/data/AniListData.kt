package io.future.laboratories.anilistapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//region MainData

@JsonClass(generateAdapter = true)
public data class MainData(
    @Json(name = "MediaTagCollection") val mediaTagCollection: List<MediaTag>,
    @Json(name = "User") val user: User,
    @Json(name = "MediaListCollection") val mediaListCollection: MediaListCollection,
)

@JsonClass(generateAdapter = true)
public data class MediaTag(
    val name: String,
    val isAdult: Boolean = false,
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
    val tags: List<MediaTag>,
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
