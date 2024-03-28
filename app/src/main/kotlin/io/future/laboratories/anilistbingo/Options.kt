package io.future.laboratories.anilistbingo

import android.content.SharedPreferences
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import io.future.laboratories.anilistapi.data.ScoreFormat
import io.future.laboratories.anilistbingo.annotation.RestrictedApi
import io.future.laboratories.anilistbingo.controller.APIController
import io.future.laboratories.ui.components.BooleanOption
import io.future.laboratories.ui.components.DropdownOption
import io.future.laboratories.ui.components.OptionData
import io.future.laboratories.ui.components.OptionKey

public class Options private constructor(preferences: SharedPreferences) {
    private val showFinishedAnime: BooleanOption = BooleanOption(
        preferences = preferences,
        key = SHOW_FINISHED_ANIME,
        name = { stringResource(id = R.string.option_visibility) },
        defaultValue = false,
    )
    private val pinnedCategory: DropdownOption = DropdownOption(
        preferences = preferences,
        key = PINNED_CATEGORY,
        name = { stringResource(id = R.string.option_pinned_category) },
        defaultValue = "Completed",
        values = {
            stringArrayResource(id = R.array.option_pinned_category_values).associateWith { it }
        },
        isVisible = { showFinishedAnime.currentValue },
    )
    private val useCards: BooleanOption = BooleanOption(
        preferences = preferences,
        key = USE_CARDS,
        name = { stringResource(id = R.string.option_use_cards) },
        defaultValue = true,
    )
    private val scoringSystem: DropdownOption = DropdownOption(
        preferences = preferences,
        key = SCORING_SYSTEM,
        name = { stringResource(id = R.string.option_scoring_system) },
        defaultValue = "100 Point (55/100)",
        values = {
            stringArrayResource(id = R.array.option_score_system).associateWith { it }
        },
        onValueChanged = { value ->
            val controller = APIController.getInstance(preferences)

            val type = ScoreFormat.entries.first { it.value == value }

            controller.mutateUser(type)
        }
    )

    @PublishedApi
    @RestrictedApi
    internal val options: Map<OptionKey, OptionData<*>> = listOf(
        showFinishedAnime,
        pinnedCategory,
        useCards,
        scoringSystem,
    ).associate { it.toPair() }

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
        internal val PINNED_CATEGORY = OptionKey("PINNED_CATEGORY")
        internal val USE_CARDS = OptionKey("USE_CARDS")
        internal val SCORING_SYSTEM = OptionKey("SCORING_SYSTEM")

        @Volatile
        private var instance: Options? = null

        internal fun getInstance(preferences: SharedPreferences): Options =
            instance ?: synchronized(this) {
                instance ?: Options(preferences).also { instance = it }
            }
    }
}
