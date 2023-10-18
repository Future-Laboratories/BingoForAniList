package io.future.laboratories.anilistbingo

import android.content.SharedPreferences
import androidx.compose.ui.res.stringResource
import io.future.laboratories.ui.components.BooleanOption
import io.future.laboratories.ui.components.OptionData
import io.future.laboratories.ui.components.OptionKey

public class Options private constructor(preferences: SharedPreferences) {
    private val showFinishedAnime: BooleanOption = BooleanOption(
        preferences,
        SHOW_FINISHED_ANIME,
        { stringResource(id = R.string.option_visibility) },
        false,
    )

    @PublishedApi
    @RestrictedApi
    internal val options: Map<OptionKey, OptionData<*>> = mapOf(
        showFinishedAnime.toPair(),
    )

    @PublishedApi
    @RestrictedApi
    internal inline fun <reified T : OptionData<*>> findOptionItemOfTypeWithKey(key: OptionKey): T {
        val option = options[key]

        return if (option is T) option else throw NoSuchElementException("no ${T::class} found under $key")
    }

    @OptIn(RestrictedApi::class)
    public inline operator fun <reified T : OptionData<*>> get(key: OptionKey): T {
        return findOptionItemOfTypeWithKey(key)
    }

    public companion object {
        internal val SHOW_FINISHED_ANIME = OptionKey("SHOW_FINISHED_ANIME")

        @Volatile
        private var instance: Options? = null

        internal fun getInstance(preferences: SharedPreferences): Options =
            instance ?: synchronized(this) {
                instance ?: Options(preferences).also { instance = it }
            }
    }
}
