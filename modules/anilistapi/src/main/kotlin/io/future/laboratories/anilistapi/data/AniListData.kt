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
public data class UpdateUserData(
    @Json(name = "UpdateUser") val updateUser: UpdateUser,
)

@JsonClass(generateAdapter = true)
public data class UpdateMediaListEntry(
    @Json(name = "SaveMediaListEntry") val saveMediaListEntry: Score,
)


@JsonClass(generateAdapter = true)
public data class MediaTag(
    val name: String,
    val isAdult: Boolean = false,
)

@JsonClass(generateAdapter = true)
public data class UpdateUser(
    val mediaListOptions: MediaListOptions,
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
    val id: Long,
    var score: Float,
    var status: MediaListStatus = MediaListStatus.NONE,
    val media: Media,
)

public enum class MediaListStatus(public val value: String) {
    @Json(ignore = true)
    NONE("No Selection"),

    @Json(name = "CURRENT")
    CURRENT("Watching"),

    @Json(name = "PLANNING")
    PLANNING("Planning"),

    @Json(name = "COMPLETED")
    COMPLETED("Completed"),

    @Json(name = "DROPPED")
    DROPPED("Dropped"),

    @Json(name = "PAUSED")
    PAUSED("Paused"),

    @Json(name = "REPEATING")
    REPEATING("Rewatching"),
}

@JsonClass(generateAdapter = true)
public data class Score(
    val score: Float,
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
