package io.future.laboratories

import com.squareup.moshi.Moshi

public object Companion {
    private const val PREFIX = "BINGO"
    public const val PREFERENCE_ACCESS_TOKEN: String = "${PREFIX}_TOKEN"
    public const val PREFERENCE_ACCESS_TYPE: String = "${PREFIX}_TYPE"
    public const val PREFERENCE_ACCESS_EXPIRED: String = "${PREFIX}_EXPIRED"
    public const val PREFERENCE_USER_ID: String = "${PREFIX}_USER_ID"
    public const val TEMP_PATH: String = "TEMP_DATA"

    public val BUILDER: Moshi
        get() = Moshi.Builder().build()

    public fun bingoStoragePath(subPath: String? = null): String {
        return if (subPath != null) "Bingo/${subPath}" else "Bingo"
    }
}