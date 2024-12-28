package io.future.laboratories.anilistapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.DateFormat
import java.util.Calendar

@JsonClass(generateAdapter = true)
public data class DetailedAniListData(
    @Json(name = "Media") val data: DetailedAniListDataBody,
)

@JsonClass(generateAdapter = true)
public data class DetailedAniListDataBody(
    val id: Long,
    val title: MediaTitle,
    val description: String?,
    val episodes: Int?,
    val duration: Int?,
    val genres: List<String>,
    val averageScore: Float?,
    val meanScore: Float?,
    val popularity: Int?,
    val tags: List<DetailedMediaTag>,
    val bannerImage: String?,
    val coverImage: MediaCoverImage,
    val startDate: FuzzyDate?,
    val endDate: FuzzyDate?,
    val stats: MediaStats?,
    val characters: CharacterConnection?,
)

@JsonClass(generateAdapter = true)
public data class DetailedMediaTag(
    val name: String,
    val rank: Int,
    val isAdult: Boolean = false,
)

@JsonClass(generateAdapter = true)
public data class MediaStats(
    val scoreDistribution: List<ScoreDistribution>?,
    val statusDistribution: List<StatusDistribution>?,
)

@JsonClass(generateAdapter = true)
public data class ScoreDistribution(
    val score: Int,
    val amount: Int,
)

@JsonClass(generateAdapter = true)
public data class StatusDistribution(
    val status: MediaListStatus,
    val amount: Int,
)

@JsonClass(generateAdapter = true)
public data class FuzzyDate(
    val year: Int?,
    val month: Int?,
    val day: Int?,
) {
    override fun toString(): String {
        return if (day != null && month != null && year != null) {
            DateFormat.getDateInstance().format(
                Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, day)
                    set(Calendar.MONTH, month)
                    set(Calendar.YEAR, year)
                }.time
            )
        } else "N/A"
    }
}

@JsonClass(generateAdapter = true)
public data class CharacterConnection(
    val edges: List<CharacterEdge>?,
)

@JsonClass(generateAdapter = true)
public data class CharacterEdge(
    val node: Character,
    val role: CharacterRole,
)

@JsonClass(generateAdapter = true)
public data class Character(
    val name: CharacterName,
    val age: String?,
    val image: CharacterImage?,
    val dateOfBirth: FuzzyDate?,
    val gender: String?,
    val bloodType: String?,
)

@JsonClass(generateAdapter = true)
public data class CharacterName(
    val first: String?,
    val middle: String?,
    val last: String?,
    val full: String?,
    val alternative: List<String>?,
    val alternativeSpoiler: List<String>?,
    val userPreferred: String?,
)

@JsonClass(generateAdapter = true)
public data class CharacterImage(
    val large: String?,
)

public enum class CharacterRole(public val value: String) {
    @Json(name = "MAIN")
    MAIN("Main"),
    @Json(name = "SUPPORTING")
    SUPPORTING("Supporting"),
    @Json(name = "BACKGROUND")
    BACKGROUND("Background"),
}