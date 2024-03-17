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
    val mediaListOptions: MediaListOptions?,
)

@JsonClass(generateAdapter = true)
public data class UserAvatar(
    val medium: String,
)

@JsonClass(generateAdapter = true)
public data class MediaListOptions(
    val scoreFormat: ScoreFormat,
)

public enum class ScoreFormat(public val value: String) {
    @Json(name = "POINT_100")
    POINT_100("100 Point (55/100)"),

    @Json(name = "POINT_10_DECIMAL")
    POINT_10_DECIMAL("10 Point Decimal (5.5/10)"),

    @Json(name = "POINT_10")
    POINT_10("10 Point (5/10)"),

    @Json(name = "POINT_5")
    POINT_5("5 Star (3/5)"),

    @Json(name = "POINT_3")
    POINT_3("3 Point Smiley"),
}

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
    val score: Float,
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
