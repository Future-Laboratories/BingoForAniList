package io.future.laboratories.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import io.future.laboratories.ui.components.FilterOptions
import kotlin.collections.any

//region Strings

internal fun String.colon(): String = "$this:"

internal infix fun <A, B, C : Pair<A, B>, D> C.toTriple(other: D) = Triple(first, second, other)

//endregion

//region dp

@Composable
@ReadOnlyComposable
internal fun Int.pxValueToDp(): Dp {
    val density = LocalDensity.current

    return with(density) { this@pxValueToDp.toDp() }
}

internal fun Int.pxValueToDp(density: Density): Dp {
    return with(density) { this@pxValueToDp.toDp() }
}

//endregion

//region FilterOptions

internal fun <T> List<T>.applyOperator(
    filterOptions: FilterOptions,
    predicate: (T) -> Boolean,
): Boolean = when (filterOptions) {
    FilterOptions.OR -> any(predicate)
    FilterOptions.AND -> all(predicate)
    FilterOptions.XOR -> count(predicate) == 1
    FilterOptions.NOT -> none(predicate)
}

//endregion

//annotation

@Preview(
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
    apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
    apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE,
    apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE,
    apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
    apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE,
    apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE,
    apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE,
    apiLevel = 33,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@PreviewScreenSizes
@PreviewFontScale
public annotation class AllPreview

