package io.future.laboratories.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

//region Strings

internal fun String.colon(): String = "$this:"

//endregion

//region dp

@Composable
@ReadOnlyComposable
internal fun Int.pxValueToDp(): Dp {
    val density = LocalDensity.current

    return with(density) { this@pxValueToDp.toDp() }
}
//endregion

