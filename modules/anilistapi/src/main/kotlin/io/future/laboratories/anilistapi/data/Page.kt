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

public data class PageQueryParams(
    val pageNumber: KeyValue<Int>,
    val format: KeyValue<List<MediaFormat>?>,
    val season: KeyValue<MediaSeason?>,
    val year: KeyValue<Int?>,
    val search: KeyValue<String?>,
) : Cloneable {
    public fun toMap(): Map<String, Any> {
        val map = mutableMapOf<String, Any>()

        listOf(
            pageNumber,
            format,
            season,
            year,
            search,
        ).forEach {
            with(it) {
                map.addToMapOrReturn()
            }
        }

        return map
    }

    override fun equals(other: Any?): Boolean {
        return if (other is PageQueryParams) {
            this.format.value == other.format.value
                    && this.season.value == other.season.value
                    && this.year.value == other.year.value
                    && this.search.value == other.search.value
        } else false
    }

    override fun hashCode(): Int {
        var result = format.hashCode()
        result = 31 * result + season.hashCode()
        result = 31 * result + year.hashCode()
        result = 31 * result + search.hashCode()
        return result
    }

    public override fun clone(): PageQueryParams {
        return PageQueryParams(
            pageNumber = pageNumber.clone(),
            format = format.clone(),
            season = season.clone(),
            year = year.clone(),
            search = search.clone(),
        )
    }
}

public data class KeyValue<T>(
    val key: String,
    var value: T,
    val toMapValue: (T) -> Any = { value -> value as Any },
) : Cloneable {
    public fun MutableMap<String, Any>.addToMapOrReturn(): MutableMap<String, Any> {
        return value?.let {
            this[key] = toMapValue(it)
            this
        } ?: this
    }

    public override fun clone(): KeyValue<T> {
        return KeyValue(key, value, toMapValue)
    }
}