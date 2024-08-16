package io.future.laboratories.anilistbingo

import android.R.attr.type
import android.content.SharedPreferences
import androidx.compose.ui.res.stringResource
import io.future.laboratories.anilistapi.data.MediaListStatus
import io.future.laboratories.anilistapi.data.ScoreFormat
import io.future.laboratories.anilistbingo.annotation.RestrictedApi
import io.future.laboratories.anilistbingo.controller.APIController
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.ui.components.BooleanOption
import io.future.laboratories.ui.components.DropdownOption
import io.future.laboratories.ui.components.OptionData
import io.future.laboratories.ui.components.OptionKey

public class Options private constructor(
    preferences: SharedPreferences,
    controller: APIController,
) {
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
        defaultValue = MediaListStatus.NONE.value,
        values = { MediaListStatus.entries.map { it.value }.associateWith { it } },
        isVisible = { showFinishedAnime.currentValue },
    )
    private val useCards: BooleanOption = BooleanOption(
        preferences = preferences,
        key = USE_CARDS,
        name = { stringResource(id = R.string.option_use_cards) },
        onValueChanged = { value, save ->
            StyleProvider.useCards = value

            save(value)
        },
        defaultValue = true,
    )
    private val useGradient: BooleanOption = BooleanOption(
        preferences = preferences,
        key = USE_GRADIENT,
        name = { stringResource(id = R.string.option_use_gradient) },
        onValueChanged = { value, save ->
            StyleProvider.useGradient = value

            save(value)
        },
        defaultValue = false,
    )
    private val scoringSystem: DropdownOption = DropdownOption(
        preferences = preferences,
        key = SCORING_SYSTEM,
        name = { stringResource(id = R.string.option_scoring_system) },
        defaultValue = ScoreFormat.POINT_100.value,
        values = { ScoreFormat.entries.map { it.value }.associateWith { it } },
        onValueChanged = { value, save ->
            val type = ScoreFormat.entries.first { it.value == value }

            controller.mutateUser(type, save)
        },
    )
    private val showExperimental: BooleanOption = BooleanOption(
        preferences = preferences,
        key = SHOW_EXPERIMENTAL,
        name = { stringResource(id = R.string.option_show_experimental_options) },
        defaultValue = false,
        onValueChanged = { value, save ->
            if(!value) {
                showExperimentalBrowser.currentValue = false
            }

            save(value)
        },
    )
    private val showExperimentalBrowser: BooleanOption = BooleanOption(
        preferences = preferences,
        key = SHOW_EXPERIMENTAL_BROWSER,
        name = { stringResource(id = R.string.option_show_experimental_Browser) },
        defaultValue = false,
        isVisible = { showExperimental.currentValue },
    )

    @PublishedApi
    @RestrictedApi
    internal val options: Map<OptionKey, OptionData<*>> = listOf(
        showFinishedAnime,
        pinnedCategory,
        useCards,
        useGradient,
        scoringSystem,
        showExperimental,
        showExperimentalBrowser,
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
        internal val USE_GRADIENT = OptionKey("USE_GRADIENT")
        internal val SCORING_SYSTEM = OptionKey("SCORING_SYSTEM")
        internal val SHOW_EXPERIMENTAL = OptionKey("SHOW_EXPERIMENTAL")
        internal val SHOW_EXPERIMENTAL_BROWSER = OptionKey("SHOW_EXPERIMENTAL_BROWSER")

        @Volatile
        private var instance: Options? = null

        internal fun getInstance(
            preferences: SharedPreferences,
            controller: APIController,
        ): Options =
            instance ?: synchronized(this) {
                instance ?: Options(
                    preferences = preferences,
                    controller = controller,
                ).also { instance = it }
            }
    }
}
