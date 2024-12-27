package io.future.laboratories.anilistbingo

import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.future.laboratories.anilistapi.data.MediaListStatus
import io.future.laboratories.anilistapi.data.ScoreFormat
import io.future.laboratories.anilistbingo.annotation.RestrictedApi
import io.future.laboratories.anilistbingo.controller.APIController
import io.future.laboratories.common.StyleProvider
import io.future.laboratories.ui.components.BaseOptionData
import io.future.laboratories.ui.components.BooleanOption
import io.future.laboratories.ui.components.ColorOption
import io.future.laboratories.ui.components.ColorPallets
import io.future.laboratories.ui.components.DropdownOption
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
    private val useCustomColorScheme: BooleanOption = BooleanOption(
        preferences = preferences,
        key = USE_CUSTOM_COLOR_SCHEME,
        name = { stringResource(id = R.string.option_custom_color_scheme) },
        defaultValue = false,
        isVisible = { showExperimental.currentValue },
    )
    private val customColorSchemePrimary: ColorOption = ColorOption(
        preferences = preferences,
        key = CUSTOM_COLOR_SCHEME_PRIMARY,
        name = { stringResource(id = R.string.option_custom_color_primary) },
        defaultValue = Color(0xFF0395b2),
        isVisible = { showExperimental.currentValue && useCustomColorScheme.currentValue },
    )
    private val customColorSchemeSecondary: ColorOption = ColorOption(
        preferences = preferences,
        key = CUSTOM_COLOR_SCHEME_SECONDARY,
        name = { stringResource(id = R.string.option_custom_color_secondary) },
        defaultValue = Color(0xFF00687F),
        isVisible = { showExperimental.currentValue && useCustomColorScheme.currentValue },
    )
    private val customColorSchemeTertiary: ColorOption = ColorOption(
        preferences = preferences,
        key = CUSTOM_COLOR_SCHEME_TERTIARY,
        name = { stringResource(id = R.string.option_custom_color_tertiary) },
        defaultValue = Color(0xFF005466),
        isVisible = { showExperimental.currentValue && useCustomColorScheme.currentValue },
    )
    private val customColorSchemeError: ColorOption = ColorOption(
        preferences = preferences,
        key = CUSTOM_COLOR_SCHEME_ERROR,
        name = { stringResource(id = R.string.option_custom_color_error) },
        defaultValue = Color(0xFF7A0D0D),
        isVisible = { showExperimental.currentValue && useCustomColorScheme.currentValue },
    )
    private val presetOptions: ColorPallets = ColorPallets(
        key = COLOR_PALLET,
        onSelectPressed = {
            customColorSchemePrimary.currentValue = it.primary
            customColorSchemeSecondary.currentValue = it.secondary
            customColorSchemeTertiary.currentValue = it.tertiary
        },
        isVisible = { showExperimental.currentValue && useCustomColorScheme.currentValue },
        defaultValue = mutableStateListOf(
            ColorPallets.PalletPreset(
                name = "Default",
                primary = Color(0xFF0395b2),
                secondary = Color(0xFF00687F),
                tertiary = Color(0xFF005466),
            ),
            ColorPallets.PalletPreset(
                name = "Teal",
                primary = Color(0xFFA6F6F1),
                secondary = Color(0xFF41AEA9),
                tertiary = Color(0xFF213E3B),
            ),
            ColorPallets.PalletPreset(
                name = "Navy",
                primary = Color(0xFF4B70F5),
                secondary = Color(0xFF4C3BCF),
                tertiary = Color(0xFF402E7A),
            ),
            ColorPallets.PalletPreset(
                name = "Mysti",
                primary = Color(0xFFb45dbb),
                secondary = Color(0xFF2e0f38),
                tertiary = Color(0xFF8351a8),
            ),
            ColorPallets.PalletPreset(
                name = "Sunrise",
                primary = Color(0xFFFFEEA9),
                secondary = Color(0xFFFFBF78),
                tertiary = Color(0xFFFF7D29),
            ),
            ColorPallets.PalletPreset(
                name = "Maroon",
                primary = Color(0xFFCC2B52),
                secondary = Color(0xFFAF1740),
                tertiary = Color(0xFF740938),
            ),
            ColorPallets.PalletPreset(
                name = "Spezi",
                primary = Color(0xFFEB5B00),
                secondary = Color(0xFFE4003A),
                tertiary = Color(0xFFB60071),
            ),
            ColorPallets.PalletPreset(
                name = "Coffee",
                primary = Color(0xFFae3c31),
                secondary = Color(0xFF331013),
                tertiary = Color(0xFF5e1718),
            ),
            ColorPallets.PalletPreset(
                name = "Contrast",
                primary = Color(0xFFfcfcfc),
                secondary = Color(0xFF525252),
                tertiary = Color(0xFFababab),
            ),
        )
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
    internal val options: Map<OptionKey, BaseOptionData<*>> = listOf(
        showFinishedAnime,
        pinnedCategory,
        useCards,
        useGradient,
        useCustomColorScheme,
        customColorSchemePrimary,
        customColorSchemeSecondary,
        customColorSchemeTertiary,
        customColorSchemeError,
        presetOptions,
        scoringSystem,
        showExperimental,
        showExperimentalBrowser,
    ).associate { it.toPair() }

    @PublishedApi
    @RestrictedApi
    internal inline fun <reified T : BaseOptionData<*>> findOptionItemOfTypeWithKey(key: OptionKey): T {
        val option = options[key]

        return option as? T ?: throw NoSuchElementException("no ${T::class} found under $key")
    }

    @OptIn(RestrictedApi::class)
    public inline operator fun <reified T : BaseOptionData<*>> get(key: OptionKey): T {
        return findOptionItemOfTypeWithKey(key)
    }

    public companion object {
        internal val SHOW_FINISHED_ANIME = OptionKey("SHOW_FINISHED_ANIME")
        internal val PINNED_CATEGORY = OptionKey("PINNED_CATEGORY")
        internal val USE_CARDS = OptionKey("USE_CARDS")
        internal val USE_GRADIENT = OptionKey("USE_GRADIENT")
        internal val USE_CUSTOM_COLOR_SCHEME = OptionKey("USE_CUSTOM_COLOR_SCHEME")
        internal val CUSTOM_COLOR_SCHEME_PRIMARY = OptionKey("CUSTOM_COLOR_SCHEME_PRIMARY")
        internal val CUSTOM_COLOR_SCHEME_SECONDARY = OptionKey("CUSTOM_COLOR_SCHEME_SECONDARY")
        internal val CUSTOM_COLOR_SCHEME_TERTIARY = OptionKey("CUSTOM_COLOR_SCHEME_TERTIARY")
        internal val CUSTOM_COLOR_SCHEME_ERROR = OptionKey("CUSTOM_COLOR_SCHEME_ERROR")
        internal val COLOR_PALLET = OptionKey("COLOR_PALLET")
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
