package io.future.laboratories.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

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

