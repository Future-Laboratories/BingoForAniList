package io.future.laboratories.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import coil.transform.RoundedCornersTransformation
import coil.transform.Transformation

//region Strings

internal fun String.colon(): String = "$this:"

//endregion

//region Transformations

@Suppress("FunctionName")
internal fun EvenRoundedCornersTransformation(all: Float): Transformation =
    RoundedCornersTransformation(all, all, all, all)

//endregion

//region dp

@Composable
@ReadOnlyComposable
internal fun Int.pxValueToDp(): Dp {
    val density = LocalDensity.current

    return with(density) { this@pxValueToDp.toDp() }
}
//endregion

