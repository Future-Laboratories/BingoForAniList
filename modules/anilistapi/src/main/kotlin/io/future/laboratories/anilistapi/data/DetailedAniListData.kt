package io.future.laboratories.anilistapi.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

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
    val averageScore: Float,
    val meanScore: Float,
    val popularity: Int,
    val tags: List<DetailedMediaTag>,
    val bannerImage: String?,
    val coverImage: MediaCoverImage,
    val startDate: FuzzyDate?,
    val endDate: FuzzyDate?,
)

@JsonClass(generateAdapter = true)
public data class DetailedMediaTag(
    val name: String,
    val rank: Int,
    val isAdult: Boolean = false,
)

@JsonClass(generateAdapter = true)
public data class FuzzyDate(
    val year: Int?,
    val month: Int?,
    val day: Int?,
)