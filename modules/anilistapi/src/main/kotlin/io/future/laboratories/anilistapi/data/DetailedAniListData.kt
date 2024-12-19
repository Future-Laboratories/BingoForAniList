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